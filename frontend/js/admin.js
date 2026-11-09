// ==========================================================================
// TRANSITX - ADMIN DASHBOARD MODULE
// ==========================================================================

async function loadAdminDashboard() {
  if (!auth.requireAuth('ROLE_ADMIN')) return;

  loadAdminStats();
  loadAdminBuses();
  loadAdminRoutes();
  loadAdminDrivers();
  loadAdminStudents();
  loadAdminPasses();
}

async function loadAdminStats() {
  try {
    const res = await api.get('/admin/stats');
    const s = res.data;

    const elStudents = document.getElementById('stat-total-students');
    const elBuses = document.getElementById('stat-total-buses');
    const elDrivers = document.getElementById('stat-total-drivers');
    const elRoutes = document.getElementById('stat-total-routes');
    const elPasses = document.getElementById('stat-total-passes');
    const elTrips = document.getElementById('stat-active-trips');

    if (elStudents) elStudents.innerText = s.totalStudents;
    if (elBuses) elBuses.innerText = s.totalBuses;
    if (elDrivers) elDrivers.innerText = s.totalDrivers;
    if (elRoutes) elRoutes.innerText = s.totalRoutes;
    if (elPasses) elPasses.innerText = s.totalPassApplications;
    if (elTrips) elTrips.innerText = s.activeTrips;
  } catch (err) {
    console.error('Error fetching admin stats:', err);
  }
}

// BUS MANAGEMENT
async function loadAdminBuses() {
  const table = document.getElementById('admin-buses-table');
  if (!table) return;

  try {
    const res = await api.get('/buses');
    const buses = res.data || [];

    table.innerHTML = buses.map(b => `
      <tr>
        <td><strong>${b.busNumber}</strong></td>
        <td>${b.modelPlate || 'N/A'}</td>
        <td>${b.capacity} seats</td>
        <td><span class="badge ${b.status === 'ACTIVE' ? 'badge-active' : 'badge-delayed'}">${b.status}</span></td>
        <td>${b.currentRouteName || 'Unassigned'}</td>
        <td>${b.currentDriverName || 'Unassigned'}</td>
        <td>
          <button class="btn btn-outline-teal btn-sm" onclick="editBusPrompt(${b.id}, '${b.busNumber}', ${b.capacity}, '${b.status}')">Edit</button>
          <button class="btn btn-sm" style="background:#E02424; color:#fff;" onclick="deleteBus(${b.id})">Delete</button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    table.innerHTML = `<tr><td colspan="7">Failed to load buses.</td></tr>`;
  }
}

async function handleAddBus(e) {
  e.preventDefault();
  const busNumber = document.getElementById('add-bus-number').value.trim();
  const capacity = parseInt(document.getElementById('add-bus-capacity').value);
  const modelPlate = document.getElementById('add-bus-model').value.trim();

  try {
    await api.post('/admin/buses', { busNumber, capacity, status: 'ACTIVE', modelPlate });
    showToast('Bus added successfully', 'success');
    closeAdminModal('bus-modal');
    loadAdminBuses();
    loadAdminStats();
  } catch (err) {
    showToast(err.message || 'Failed to add bus', 'error');
  }
}

async function deleteBus(id) {
  if (!confirm('Are you sure you want to delete this bus?')) return;
  try {
    await api.delete(`/admin/buses/${id}`);
    showToast('Bus deleted successfully', 'success');
    loadAdminBuses();
    loadAdminStats();
  } catch (err) {
    showToast(err.message || 'Failed to delete bus', 'error');
  }
}

// ROUTE MANAGEMENT
async function loadAdminRoutes() {
  const table = document.getElementById('admin-routes-table');
  if (!table) return;

  try {
    const res = await api.get('/routes');
    const routes = res.data || [];

    table.innerHTML = routes.map(r => `
      <tr>
        <td><span class="route-code-badge">${r.routeCode}</span></td>
        <td><strong>${r.routeName}</strong></td>
        <td>${r.startPoint} ➔ ${r.endPoint}</td>
        <td>${r.departureTime} - ${r.arrivalTime}</td>
        <td>${r.totalStops} stops</td>
        <td>${r.assignedBusNumber || 'Unassigned'}</td>
        <td>
          <button class="btn btn-sm" style="background:#E02424; color:#fff;" onclick="deleteRoute(${r.id})">Delete</button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    table.innerHTML = `<tr><td colspan="7">Failed to load routes.</td></tr>`;
  }
}

async function handleAddRoute(e) {
  e.preventDefault();
  const routeName = document.getElementById('add-route-name').value.trim();
  const routeCode = document.getElementById('add-route-code').value.trim();
  const startPoint = document.getElementById('add-route-start').value.trim();
  const endPoint = document.getElementById('add-route-end').value.trim();
  const departureTime = document.getElementById('add-route-dep').value.trim();
  const arrivalTime = document.getElementById('add-route-arr').value.trim();
  const estDuration = document.getElementById('add-route-dur').value.trim();
  const distanceKm = parseFloat(document.getElementById('add-route-dist').value || '15');

  try {
    await api.post('/admin/routes', {
      routeName, routeCode, startPoint, endPoint, departureTime, arrivalTime, estDuration, distanceKm
    });
    showToast('Route created successfully', 'success');
    closeAdminModal('route-modal');
    loadAdminRoutes();
    loadAdminStats();
  } catch (err) {
    showToast(err.message || 'Failed to add route', 'error');
  }
}

async function deleteRoute(id) {
  if (!confirm('Are you sure you want to delete this route?')) return;
  try {
    await api.delete(`/admin/routes/${id}`);
    showToast('Route deleted', 'success');
    loadAdminRoutes();
    loadAdminStats();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// DRIVER MANAGEMENT
async function loadAdminDrivers() {
  const table = document.getElementById('admin-drivers-table');
  if (!table) return;

  try {
    const res = await api.get('/drivers');
    const drivers = res.data || [];

    table.innerHTML = drivers.map(d => `
      <tr>
        <td><strong>${d.driverCode}</strong></td>
        <td>${d.fullName}</td>
        <td>${d.phone}</td>
        <td>${d.licenseNo}</td>
        <td>${d.experienceYears} years</td>
        <td>${d.assignedBusNumber || 'Unassigned'}</td>
        <td>${d.assignedRouteName || 'Unassigned'}</td>
      </tr>
    `).join('');
  } catch (err) {
    table.innerHTML = `<tr><td colspan="7">Failed to load drivers.</td></tr>`;
  }
}

// STUDENT MANAGEMENT
async function loadAdminStudents() {
  const table = document.getElementById('admin-students-table');
  if (!table) return;

  try {
    const res = await api.get('/admin/students');
    const students = res.data || [];

    table.innerHTML = students.map(s => `
      <tr>
        <td><strong>${s.regNo}</strong></td>
        <td>${s.fullName}</td>
        <td>${s.department} (${s.yearOfStudy})</td>
        <td>${s.email}</td>
        <td>${s.assignedBusNumber || 'Not Allocated'}</td>
        <td>${s.assignedRouteName || 'Not Allocated'}</td>
      </tr>
    `).join('');
  } catch (err) {
    table.innerHTML = `<tr><td colspan="6">Failed to load students.</td></tr>`;
  }
}

// BUS PASS APPROVAL MANAGEMENT
async function loadAdminPasses() {
  const table = document.getElementById('admin-passes-table');
  if (!table) return;

  try {
    const res = await api.get('/bus-pass/all');
    const passes = res.data || [];

    table.innerHTML = passes.map(p => `
      <tr>
        <td><strong>${p.passCode}</strong></td>
        <td>${p.studentName} (${p.regNo})</td>
        <td>${p.routeName}</td>
        <td>${p.boardingStopName || 'N/A'}</td>
        <td>
          <span class="badge ${p.status === 'APPROVED' ? 'badge-active' : p.status === 'PENDING' ? 'badge-pending' : 'badge-delayed'}">
            ${p.status}
          </span>
        </td>
        <td>${p.validityEnd ? p.validityEnd : 'Not Set'}</td>
        <td>
          ${p.status === 'PENDING' ? `
            <button class="btn btn-teal btn-sm" onclick="approvePassAction(${p.id})">Approve</button>
            <button class="btn btn-sm" style="background:#E02424; color:#fff;" onclick="rejectPassAction(${p.id})">Reject</button>
          ` : `
            <span style="color: var(--teal); font-size: 0.85rem; font-weight: 600;">Processed</span>
          `}
        </td>
      </tr>
    `).join('');
  } catch (err) {
    table.innerHTML = `<tr><td colspan="7">Failed to load passes.</td></tr>`;
  }
}

async function approvePassAction(passId) {
  if (!confirm('Approve this bus pass for 6 months validity?')) return;
  try {
    await api.post(`/bus-pass/${passId}/approve`, { validityMonths: 6 });
    showToast('Bus pass approved and digital pass generated!', 'success');
    loadAdminPasses();
    loadAdminStats();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function rejectPassAction(passId) {
  const reason = prompt('Enter rejection reason for the student:', 'Incomplete route documents');
  if (!reason) return;
  try {
    await api.post(`/bus-pass/${passId}/reject`, { reason });
    showToast('Bus pass rejected', 'info');
    loadAdminPasses();
    loadAdminStats();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Tab Switching
function switchTab(tabId) {
  document.querySelectorAll('.tab-content').forEach(el => el.style.display = 'none');
  document.querySelectorAll('.sidebar-link').forEach(el => el.classList.remove('active'));

  const target = document.getElementById(tabId);
  if (target) target.style.display = 'block';

  const activeLink = document.querySelector(`[data-tab="${tabId}"]`);
  if (activeLink) activeLink.classList.add('active');
}

function openAdminModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add('open');
}

function closeAdminModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove('open');
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('admin-dashboard')) {
    loadAdminDashboard();

    const busForm = document.getElementById('add-bus-form');
    if (busForm) busForm.addEventListener('submit', handleAddBus);

    const routeForm = document.getElementById('add-route-form');
    if (routeForm) routeForm.addEventListener('submit', handleAddRoute);
  }
});
