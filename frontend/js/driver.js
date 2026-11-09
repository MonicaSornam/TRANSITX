// ==========================================================================
// TRANSITX - DRIVER DASHBOARD MODULE
// ==========================================================================

let driverProfile = null;
let currentTrip = null;

async function loadDriverDashboard() {
  if (!auth.requireAuth('ROLE_DRIVER')) return;

  try {
    const res = await api.get('/driver/dashboard');
    driverProfile = res.data;

    const elName = document.getElementById('driver-name');
    const elCode = document.getElementById('driver-code');
    const elBus = document.getElementById('driver-bus');
    const elRoute = document.getElementById('driver-route');

    if (elName) elName.innerText = driverProfile.fullName;
    if (elCode) elCode.innerText = driverProfile.driverCode;
    if (elBus) elBus.innerText = driverProfile.assignedBusNumber || 'Unassigned';
    if (elRoute) elRoute.innerText = driverProfile.assignedRouteName || 'Unassigned';

    loadDriverTrips();
    loadRouteStopsForDriver();
  } catch (err) {
    showToast('Failed to load driver dashboard', 'error');
  }
}

async function loadDriverTrips() {
  if (!driverProfile) return;

  try {
    const res = await api.get(`/trips/driver/${driverProfile.id}`);
    const trips = res.data || [];
    currentTrip = trips.find(t => t.tripStatus === 'ON_ROUTE') || null;

    const elStatus = document.getElementById('driver-trip-status');
    const btnStart = document.getElementById('btn-start-trip');
    const btnEnd = document.getElementById('btn-end-trip');

    if (elStatus) {
      if (currentTrip) {
        elStatus.innerHTML = `<span class="badge badge-active">Active Trip: ON ROUTE</span>`;
        if (btnStart) btnStart.disabled = true;
        if (btnEnd) btnEnd.disabled = false;
      } else {
        elStatus.innerHTML = `<span class="badge" style="background:#E2E8F0; color:#475569;">No Active Trip</span>`;
        if (btnStart) btnStart.disabled = false;
        if (btnEnd) btnEnd.disabled = true;
      }
    }
  } catch (err) {
    console.error('Error fetching driver trips:', err);
  }
}

async function loadRouteStopsForDriver() {
  const container = document.getElementById('driver-stops-list');
  if (!container) return;

  try {
    const res = await api.get('/routes/1');
    const route = res.data;

    if (route.stops && route.stops.length > 0) {
      container.innerHTML = route.stops.map(s => `
        <div style="padding: 0.75rem 1rem; border-bottom: 1px solid var(--border-light); display: flex; justify-content: space-between;">
          <div><strong>${s.sequence}. ${s.stopName}</strong></div>
          <div style="color: var(--teal); font-weight: 600;">Pickup: ${s.pickupTime}</div>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error(err);
  }
}

async function startDriverTrip() {
  if (!driverProfile) return;

  try {
    await api.post('/trips/start', { driverId: driverProfile.id });
    showToast('Trip started successfully! Bus location is now active.', 'success');
    loadDriverTrips();
  } catch (err) {
    showToast(err.message || 'Failed to start trip', 'error');
  }
}

async function endDriverTrip() {
  if (!currentTrip) return;

  try {
    await api.post(`/trips/end/${currentTrip.id}`);
    showToast('Trip completed. Great job!', 'success');
    loadDriverTrips();
  } catch (err) {
    showToast(err.message || 'Failed to end trip', 'error');
  }
}

async function updateDriverStatus(newStatus) {
  if (!currentTrip) {
    showToast('Start a trip first before changing status', 'error');
    return;
  }

  try {
    await api.post(`/trips/status/${currentTrip.id}`, { status: newStatus });
    showToast(`Trip status updated to ${newStatus}`, 'success');
    loadDriverTrips();
  } catch (err) {
    showToast(err.message || 'Status update failed', 'error');
  }
}

async function simulateDriverLocationStep() {
  if (!driverProfile || !driverProfile.assignedBusId) {
    showToast('No bus assigned to simulate location', 'error');
    return;
  }

  try {
    const res = await api.post(`/tracking/simulate-step/${driverProfile.assignedBusId}`);
    showToast(`Location updated to [${res.data.latitude}, ${res.data.longitude}]`, 'info');
  } catch (err) {
    showToast('Failed to update coordinates', 'error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('driver-dashboard')) {
    loadDriverDashboard();

    const btnStart = document.getElementById('btn-start-trip');
    const btnEnd = document.getElementById('btn-end-trip');
    if (btnStart) btnStart.addEventListener('click', startDriverTrip);
    if (btnEnd) btnEnd.addEventListener('click', endDriverTrip);
  }
});
