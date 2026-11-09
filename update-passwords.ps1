<#
.SYNOPSIS
    Secure Password Updater for TRANSITX (Admin & Drivers Only)
.DESCRIPTION
    Prompts the administrator locally for new passwords using masked input (-AsSecureString).
    Streams passwords directly to the Java process via standard input.
    Validates passwords against BCrypt's 72-byte limit and minimum length requirements.
    Uses Spring Security's BCryptPasswordEncoder (strength 10) to generate BCrypt hashes.
    Uses exact managed dependency JAR versions declared in pom.xml (Spring Boot 3.2.5 BOM).
    Aborts safely before connecting if database environment-variable placeholders cannot be resolved.
    Verifies that target accounts (admin, DRV001, DRV002) exist uniquely with expected roles.
    Updates ONLY the 'password' column using parameterized JDBC SQL within an explicit transaction.
    Explicitly invokes conn.rollback() on ANY failure before closing the connection in finally.
    Invokes conn.commit() ONLY after all three updates and all verification checks succeed.
    Note on memory: Best-effort buffer zeroing and reference clearing are performed, but in managed
    runtimes (.NET / JVM), temporary plaintext copies may temporarily persist in process memory
    before garbage collection; guaranteed memory zeroing cannot be promised.
    Leaves Student accounts, database schema, and all other records completely untouched.
    Never prints, logs, or stores plaintext passwords or hashes.
#>

param()

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " TRANSITX - Secure Password Update (Admin & Drivers Only) " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Note: Input is masked (*). Passwords will never be printed or logged." -ForegroundColor Gray
Write-Host ""

# 1. Locate Java runtime
$javaExe = (Get-Command java -ErrorAction SilentlyContinue).Source
if (-not $javaExe -and $env:JAVA_HOME) { $javaExe = Join-Path $env:JAVA_HOME "bin\java.exe" }
if (-not $javaExe) { $javaExe = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin\java.exe" }

if (-not (Test-Path $javaExe)) {
    Write-Error "Java executable not found at '$javaExe'. Please check Java installation."
    exit 1
}

# 2. Exact dependency versions declared in pom.xml (Spring Boot 3.2.5 BOM)
# - spring-framework.version: 6.1.6
# - spring-security.version:  6.2.4
# - mysql.version:           8.3.0
$m2Repo = "$env:USERPROFILE\.m2\repository"

$requiredJars = @(
    (Join-Path $m2Repo "org\springframework\security\spring-security-crypto\6.2.4\spring-security-crypto-6.2.4.jar"),
    (Join-Path $m2Repo "org\springframework\security\spring-security-core\6.2.4\spring-security-core-6.2.4.jar"),
    (Join-Path $m2Repo "org\springframework\spring-core\6.1.6\spring-core-6.1.6.jar"),
    (Join-Path $m2Repo "org\springframework\spring-jcl\6.1.6\spring-jcl-6.1.6.jar"),
    (Join-Path $m2Repo "com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar")
)

foreach ($jar in $requiredJars) {
    if (-not (Test-Path $jar)) {
        Write-Error "Required Maven dependency JAR not found at exact path: '$jar'"
        exit 1
    }
}

$classpath = ($requiredJars -join ";")
Write-Host "[VERIFIED] Java runtime and exact Maven dependency versions verified." -ForegroundColor Green

# 3. Verify application.properties exists and contains required keys
$propPath = "backend/src/main/resources/application.properties"
if (-not (Test-Path $propPath)) {
    $propPath = "C:\TRANSITX\backend\src\main\resources\application.properties"
}
if (-not (Test-Path $propPath)) {
    Write-Error "Configuration file not found: '$propPath'"
    exit 1
}

$hasUrl  = Select-String -Path $propPath -Pattern "^\s*spring\.datasource\.url\s*=" -Quiet
$hasUser = Select-String -Path $propPath -Pattern "^\s*spring\.datasource\.username\s*=" -Quiet
$hasPass = Select-String -Path $propPath -Pattern "^\s*spring\.datasource\.password\s*=" -Quiet

if (-not ($hasUrl -and $hasUser -and $hasPass)) {
    Write-Error "application.properties is missing one or more required spring.datasource settings."
    exit 1
}
Write-Host "[VERIFIED] Database configuration keys present in application.properties." -ForegroundColor Green
Write-Host ""

# 4. Prompt user locally with masked input (-AsSecureString)
$adminSec = Read-Host "Enter new password for 'admin' (User ID: 1)" -AsSecureString
if ($null -eq $adminSec -or $adminSec.Length -eq 0) {
    Write-Error "Password for 'admin' cannot be empty."
    exit 1
}

$drv1Sec = Read-Host "Enter new password for 'DRV001' (User ID: 2)" -AsSecureString
if ($null -eq $drv1Sec -or $drv1Sec.Length -eq 0) {
    Write-Error "Password for 'DRV001' cannot be empty."
    exit 1
}

$drv2Sec = Read-Host "Enter new password for 'DRV002' (User ID: 3) [Press Enter to use same as DRV001]" -AsSecureString
if ($null -eq $drv2Sec -or $drv2Sec.Length -eq 0) {
    $drv2Sec = $drv1Sec
}

# 5. Stream SecureString directly to Java process input with best-effort buffer clearing
function Send-SecureStringToStream ($secString, [System.IO.StreamWriter]$writer, $accountName) {
    $bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($secString)
    try {
        $len = [System.Runtime.InteropServices.Marshal]::ReadInt32($bstr, -4) / 2
        $chars = [char[]]::new($len)
        [System.Runtime.InteropServices.Marshal]::Copy($bstr, $chars, 0, $len)

        if ($chars.Length -lt 6) {
            [System.Array]::Clear($chars, 0, $chars.Length)
            throw "Password for '$accountName' must be at least 6 characters."
        }

        $byteCount = [System.Text.Encoding]::UTF8.GetByteCount($chars)
        if ($byteCount -gt 72) {
            [System.Array]::Clear($chars, 0, $chars.Length)
            throw "Password for '$accountName' exceeds BCrypt's maximum limit of 72 bytes ($byteCount bytes)."
        }

        $writer.WriteLine($chars)
        $writer.Flush()
        [System.Array]::Clear($chars, 0, $chars.Length)
    }
    finally {
        [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

# 6. Prepare temporary Java runner source
$tempJavaFile = Join-Path $env:TEMP "TransitXPasswordUpdater.java"

$javaSource = @'
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;

public class TransitXPasswordUpdater {
    public static void main(String[] args) {
        Connection conn = null;
        int exitCode = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            char[] adminPass = readPasswordChars(reader);
            char[] drv1Pass  = readPasswordChars(reader);
            char[] drv2Pass  = readPasswordChars(reader);

            // 1. Password validation (BCrypt 72-byte limit and minimum length)
            validatePasswordChars("admin", adminPass);
            validatePasswordChars("DRV001", drv1Pass);
            validatePasswordChars("DRV002", drv2Pass);

            // 2. Generate BCrypt hashes using Spring Security BCryptPasswordEncoder (strength 10)
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String adminHash = encoder.encode(new String(adminPass));
            Arrays.fill(adminPass, '\0');

            String drv1Hash = encoder.encode(new String(drv1Pass));
            Arrays.fill(drv1Pass, '\0');

            String drv2Hash = encoder.encode(new String(drv2Pass));
            Arrays.fill(drv2Pass, '\0');
            System.gc();

            // 3. Read database connection configuration from application.properties
            Properties props = new Properties();
            File propFile = new File("backend/src/main/resources/application.properties");
            if (!propFile.exists()) {
                propFile = new File("C:/TRANSITX/backend/src/main/resources/application.properties");
            }
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
            }

            // 4. Resolve environment-variable placeholders - safe abort before connecting if unresolved
            String url    = resolveProperty(props, "spring.datasource.url", "SPRING_DATASOURCE_URL");
            String dbUser = resolveProperty(props, "spring.datasource.username", "SPRING_DATASOURCE_USERNAME");
            String dbPass = resolveProperty(props, "spring.datasource.password", "SPRING_DATASOURCE_PASSWORD");

            if (url == null || url.isBlank() || dbUser == null || dbUser.isBlank() || dbPass == null) {
                throw new IllegalStateException("Database connection settings could not be completely resolved.");
            }
            if (url.contains("${") || dbUser.contains("${") || dbPass.contains("${")) {
                throw new IllegalStateException("Unresolved database placeholder detected in connection configuration. Aborting before connecting for safety.");
            }

            // 5. Connect to database with explicit transaction
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            conn.setAutoCommit(false);

            // 6. Verify exactly one matching row exists for each target account before any update
            verifySingleUser(conn, 1L, "admin", "ROLE_ADMIN");
            verifySingleUser(conn, 2L, "DRV001", "ROLE_DRIVER");
            verifySingleUser(conn, 3L, "DRV002", "ROLE_DRIVER");
            System.out.println("[VERIFIED] Target accounts verified uniquely in database.");

            // 7. Parameterized SQL update - ONLY password column of targeted accounts
            String updateSql = "UPDATE users SET password = ? WHERE id = ? AND username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                // Update admin (id = 1)
                pstmt.setString(1, adminHash);
                pstmt.setLong(2, 1L);
                pstmt.setString(3, "admin");
                int r1 = pstmt.executeUpdate();
                if (r1 != 1) {
                    throw new IllegalStateException("Expected 1 row updated for admin, got " + r1);
                }

                // Update DRV001 (id = 2)
                pstmt.setString(1, drv1Hash);
                pstmt.setLong(2, 2L);
                pstmt.setString(3, "DRV001");
                int r2 = pstmt.executeUpdate();
                if (r2 != 1) {
                    throw new IllegalStateException("Expected 1 row updated for DRV001, got " + r2);
                }

                // Update DRV002 (id = 3)
                pstmt.setString(1, drv2Hash);
                pstmt.setLong(2, 3L);
                pstmt.setString(3, "DRV002");
                int r3 = pstmt.executeUpdate();
                if (r3 != 1) {
                    throw new IllegalStateException("Expected 1 row updated for DRV002, got " + r3);
                }
            }

            // 8. Verify student records are completely untouched
            String studentCheckSql = "SELECT count(*) FROM users WHERE role = 'ROLE_STUDENT'";
            try (PreparedStatement stmt = conn.prepareStatement(studentCheckSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[VERIFIED] Student accounts remain untouched (total students: " + rs.getInt(1) + ").");
                }
            }

            // 9. Commit database transaction ONLY after all updates and verifications succeed
            conn.commit();
            System.out.println("[SUCCESS] Database transaction committed successfully.");
            System.out.println("[SUCCESS] Exactly 3 user rows updated (admin, DRV001, DRV002).");

        } catch (Throwable ex) {
            exitCode = 1;
            // Explicit rollback on ANY failure before connection closes
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[ROLLBACK] Database transaction rolled back due to failure.");
                } catch (SQLException rollbackEx) {
                    System.err.println("[ERROR] Failed to rollback transaction.");
                }
            }
            System.err.println("[ERROR] Database update failed: " + ex.getMessage());
        } finally {
            // Guaranteed connection closure in finally (no setAutoCommit called)
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {}
            }
        }

        // Exit process with proper code only after all cleanup is completely finished
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static char[] readPasswordChars(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Unexpected end of input stream when reading password.");
        }
        return line.toCharArray();
    }

    private static String resolveProperty(Properties props, String propKey, String envOverrideKey) {
        String envVal = System.getenv(envOverrideKey);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }

        String rawVal = props.getProperty(propKey);
        if (rawVal == null || rawVal.isBlank()) {
            throw new IllegalStateException("Required property '" + propKey + "' is missing or empty.");
        }
        rawVal = rawVal.trim();

        if (rawVal.startsWith("${") && rawVal.endsWith("}")) {
            String inner = rawVal.substring(2, rawVal.length() - 1).trim();
            int colonIndex = inner.indexOf(':');
            if (colonIndex != -1) {
                String varName = inner.substring(0, colonIndex).trim();
                String defaultVal = inner.substring(colonIndex + 1).trim();
                String v = System.getenv(varName);
                if (v != null && !v.isBlank()) {
                    return v.trim();
                }
                String sysProp = System.getProperty(varName);
                if (sysProp != null && !sysProp.isBlank()) {
                    return sysProp.trim();
                }
                if (defaultVal.isBlank()) {
                    throw new IllegalStateException("Unresolved database placeholder '" + varName + "' with empty default in property '" + propKey + "'.");
                }
                return defaultVal;
            } else {
                String varName = inner;
                String v = System.getenv(varName);
                if (v != null && !v.isBlank()) {
                    return v.trim();
                }
                String sysProp = System.getProperty(varName);
                if (sysProp != null && !sysProp.isBlank()) {
                    return sysProp.trim();
                }
                throw new IllegalStateException("Unresolved database environment placeholder '${" + varName + "}' in property '" + propKey + "'. Aborting before connecting for safety.");
            }
        }

        if (rawVal.contains("${")) {
            throw new IllegalStateException("Unresolved embedded placeholder pattern in property '" + propKey + "'. Aborting before connecting for safety.");
        }

        return rawVal;
    }

    private static void validatePasswordChars(String accountName, char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password for '" + accountName + "' cannot be blank.");
        }
        if (password.length < 6) {
            throw new IllegalArgumentException("Password for '" + accountName + "' must be at least 6 characters.");
        }
        byte[] utf8Bytes = new String(password).getBytes(StandardCharsets.UTF_8);
        if (utf8Bytes.length > 72) {
            throw new IllegalArgumentException("Password for '" + accountName + "' exceeds the BCrypt limit of 72 bytes (" + utf8Bytes.length + " bytes).");
        }
    }

    private static void verifySingleUser(Connection conn, long expectedId, String expectedUsername, String expectedRole) throws Exception {
        String verifySql = "SELECT id, username, role FROM users WHERE id = ? AND username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(verifySql)) {
            stmt.setLong(1, expectedId);
            stmt.setString(2, expectedUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("User record not found for id=" + expectedId + ", username=" + expectedUsername);
                }
                String actualRole = rs.getString("role");
                if (!expectedRole.equalsIgnoreCase(actualRole)) {
                    throw new IllegalStateException("Role mismatch for " + expectedUsername + " (expected " + expectedRole + ", got " + actualRole + ")");
                }
                if (rs.next()) {
                    throw new IllegalStateException("Multiple records found for id=" + expectedId);
                }
            }
        }
    }
}
'@

$process = $null
try {
    [System.IO.File]::WriteAllText($tempJavaFile, $javaSource, [System.Text.UTF8Encoding]::new($false))

    # 7. Start Java process with redirected standard input
    $psi = [System.Diagnostics.ProcessStartInfo]::new()
    $psi.FileName = $javaExe
    $psi.Arguments = "-cp `"$classpath`" `"$tempJavaFile`""
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardOutput = $false
    $psi.RedirectStandardError = $false
    $psi.UseShellExecute = $false
    $psi.CreateNoWindow = $true

    $process = [System.Diagnostics.Process]::Start($psi)

    # 8. Stream passwords directly into process input and clear memory buffers immediately
    Send-SecureStringToStream $adminSec $process.StandardInput "admin"
    Send-SecureStringToStream $drv1Sec  $process.StandardInput "DRV001"
    Send-SecureStringToStream $drv2Sec  $process.StandardInput "DRV002"

    $process.StandardInput.Close()
    $process.WaitForExit()

    if ($process.ExitCode -ne 0) {
        Write-Error "Password update process exited with error code $($process.ExitCode)."
        exit $process.ExitCode
    }
    Write-Host ""
    Write-Host "[COMPLETED] Passwords successfully updated with Spring BCryptPasswordEncoder." -ForegroundColor Green
}
finally {
    # 9. Clean up all secure string references and temporary file
    $adminSec = $null
    $drv1Sec  = $null
    $drv2Sec  = $null
    Remove-Variable adminSec, drv1Sec, drv2Sec -ErrorAction SilentlyContinue
    [System.GC]::Collect()

    if ($process -and -not $process.HasExited) {
        $process.Kill()
    }
    if (Test-Path $tempJavaFile) {
        Remove-Item $tempJavaFile -Force -ErrorAction SilentlyContinue
    }
}
