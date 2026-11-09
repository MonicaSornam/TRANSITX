// ==========================================================================
// TRANSITX - AUTHENTICATION & ACCESS CONTROL
// ==========================================================================

const auth = {
  getUser() {
    const userStr = localStorage.getItem('transitx_user');
    return userStr ? JSON.parse(userStr) : null;
  },

  setUser(user, token) {
    localStorage.setItem('transitx_token', token);
    localStorage.setItem('transitx_user', JSON.stringify(user));
  },

  isAuthenticated() {
    return !!localStorage.getItem('transitx_token');
  },

  getRole() {
    const user = this.getUser();
    return user ? user.role : null;
  },

  logout() {
    localStorage.removeItem('transitx_token');
    localStorage.removeItem('transitx_user');
    window.location.href = 'index.html';
  },

  requireAuth(allowedRole = null) {
    if (!this.isAuthenticated()) {
      window.location.href = 'login.html';
      return false;
    }
    const role = this.getRole();
    if (allowedRole && role !== allowedRole && role !== 'ROLE_ADMIN') {
      showToast('Unauthorized access. Redirecting...', 'error');
      if (role === 'ROLE_DRIVER') window.location.href = 'driver-dashboard.html';
      else if (role === 'ROLE_STUDENT') window.location.href = 'student-dashboard.html';
      else window.location.href = 'index.html';
      return false;
    }
    return true;
  },

  updateNavbarUI() {
    const authContainer = document.getElementById('navbar-auth-section');
    if (!authContainer) return;

    if (this.isAuthenticated()) {
      const user = this.getUser();
      let dashboardUrl = 'student-dashboard.html';
      if (user.role === 'ROLE_ADMIN') dashboardUrl = 'admin-dashboard.html';
      else if (user.role === 'ROLE_DRIVER') dashboardUrl = 'driver-dashboard.html';

      authContainer.innerHTML = `
        <a href="${dashboardUrl}" class="btn btn-outline-sky btn-sm">Dashboard</a>
        <button onclick="auth.logout()" class="btn btn-teal btn-sm">Logout</button>
      `;
    } else {
      authContainer.innerHTML = `
        <a href="login.html" class="btn btn-outline-sky btn-sm">Login</a>
        <a href="register.html" class="btn btn-teal btn-sm">Register</a>
      `;
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  auth.updateNavbarUI();
});
