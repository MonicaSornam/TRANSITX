// ==========================================================================
// TRANSITX - STUDENT DASHBOARD MODULE
// ==========================================================================

async function loadStudentDashboard() {
  if (!auth.requireAuth('ROLE_STUDENT')) return;

  const user = auth.getUser();
  const welcomeName = document.getElementById('student-welcome-name');
  if (welcomeName && user) welcomeName.innerText = user.fullName || user.username;

  try {
    const res = await api.get('/student/profile');
    const profile = res.data;

    const elBus = document.getElementById('stat-assigned-bus');
    const elRoute = document.getElementById('stat-assigned-route');
    const elRegNo = document.getElementById('student-reg-no');
    const elDept = document.getElementById('student-dept');

    if (elBus) elBus.innerText = profile.assignedBusNumber || 'Unassigned';
    if (elRoute) elRoute.innerText = profile.assignedRouteName || 'Unassigned';
    if (elRegNo) elRegNo.innerText = profile.regNo;
    if (elDept) elDept.innerText = profile.department;

    // Load pass status shortcut
    loadPassStatusBadge();
  } catch (err) {
    console.error('Error loading student profile:', err);
  }
}

async function loadPassStatusBadge() {
  const elBadge = document.getElementById('stat-pass-status');
  if (!elBadge) return;

  try {
    const res = await api.get('/bus-pass/my-pass');
    const pass = res.data;

    if (pass.status === 'APPROVED') {
      elBadge.innerHTML = `<span class="badge badge-active">Approved</span>`;
    } else if (pass.status === 'PENDING') {
      elBadge.innerHTML = `<span class="badge badge-pending">Pending Approval</span>`;
    } else {
      elBadge.innerHTML = `<span class="badge badge-delayed">Rejected</span>`;
    }
  } catch (err) {
    elBadge.innerHTML = `<span class="badge" style="background:#E2E8F0; color:#475569;">No Pass</span>`;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('student-dashboard')) {
    loadStudentDashboard();
  }
});
