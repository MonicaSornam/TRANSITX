package com.transitx.service;

import com.transitx.dto.*;
import com.transitx.entity.*;
import com.transitx.exception.BadRequestException;
import com.transitx.repository.*;
import com.transitx.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public ApiResponse<String> registerStudent(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }
        if (userRepository.existsByUsername(req.getRegNo())) {
            throw new BadRequestException("Register number is already registered as a username");
        }
        if (studentRepository.existsByRegNo(req.getRegNo())) {
            throw new BadRequestException("Register number already exists");
        }
        if (userRepository.existsByEmail(req.getEmail()) || studentRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User(
                req.getRegNo(),
                req.getEmail(),
                passwordEncoder.encode(req.getPassword()),
                Role.ROLE_STUDENT,
                req.getFullName(),
                req.getPhone()
        );
        User savedUser = userRepository.save(user);

        Student student = new Student(
                savedUser,
                req.getRegNo(),
                req.getDepartment(),
                req.getYearOfStudy(),
                req.getFullName(),
                req.getEmail(),
                req.getPhone()
        );
        studentRepository.save(student);

        return ApiResponse.ok("Student registered successfully! You can now log in.");
    }

    public ApiResponse<AuthResponse> login(LoginRequest req) {
        String identifier = req.getIdentifier().trim();

        // Check if student with register number exists
        Optional<Student> studentOpt = studentRepository.findByRegNo(identifier);
        String targetUser = identifier;
        if (studentOpt.isPresent()) {
            targetUser = studentOpt.get().getUser().getUsername();
        }
        final String usernameToAuthenticate = targetUser;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameToAuthenticate, req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(usernameToAuthenticate)
                .or(() -> userRepository.findByEmail(usernameToAuthenticate))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        Long studentId = null;
        Long driverId = null;

        if (user.getRole() == Role.ROLE_STUDENT) {
            studentId = studentRepository.findByUserId(user.getId())
                    .map(Student::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_DRIVER) {
            driverId = driverRepository.findByUserId(user.getId())
                    .map(Driver::getId).orElse(null);
        }

        AuthResponse resp = new AuthResponse(
                token,
                user.getRole().name(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                studentId,
                driverId
        );

        return ApiResponse.ok("Login successful", resp);
    }
}
