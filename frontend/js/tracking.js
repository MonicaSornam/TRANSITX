// ==========================================================================
// TRANSITX - LIVE BUS TRACKING & LEAFLET MAP MODULE
// ==========================================================================

let map = null;
let busMarker = null;
let stopMarkers = [];
let routeLine = null;
let selectedBusId = 1;
let simulationInterval = null;
let isSimulating = false;

// Custom Leaflet Icons
const busIcon = L.divIcon({
  className: 'custom-bus-marker',
  html: `<div style="background: #2F4156; color: #FFFFFF; border: 2px solid #567C8D; border-radius: 50%; width: 42px; height: 42px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; box-shadow: 0 4px 12px rgba(47,65,86,0.35);">🚌</div>`,
  iconSize: [42, 42],
  iconAnchor: [21, 21]
});

const stopIcon = L.divIcon({
  className: 'custom-stop-marker',
  html: `<div style="background: #567C8D; color: #FFFFFF; border: 2px solid #FFFFFF; border-radius: 50%; width: 22px; height: 22px; display: flex; align-items: center; justify-content: center; font-size: 0.65rem; font-weight: 700; box-shadow: 0 2px 6px rgba(0,0,0,0.2);">📍</div>`,
  iconSize: [22, 22],
  iconAnchor: [11, 11]
});

function initMap() {
  const mapElement = document.getElementById('leaflet-map');
  if (!mapElement) return;

  // Initialize center at Bangalore campus coordinates
  map = L.map('leaflet-map').setView([12.9351, 77.6012], 13);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 19
  }).addTo(map);

  loadBusSelector();
  loadBusData(selectedBusId);
}

async function loadBusSelector() {
  const selector = document.getElementById('bus-selector');
  if (!selector) return;

  try {
    const res = await api.get('/buses');
    const buses = res.data || [];
    selector.innerHTML = buses.map(b => `
      <option value="${b.id}" ${b.id === selectedBusId ? 'selected' : ''}>
        ${b.busNumber} (${b.currentRouteName || 'Active Fleet'})
      </option>
    `).join('');

    selector.addEventListener('change', (e) => {
      selectedBusId = parseInt(e.target.value);
      loadBusData(selectedBusId);
    });
  } catch (err) {
    console.error('Failed to load buses:', err);
  }
}

async function loadBusData(busId) {
  try {
    const res = await api.get(`/tracking/bus/${busId}`);
    const loc = res.data;

    updateUI(loc);
    updateMapLocation(loc);
    loadRouteStops(busId);
  } catch (err) {
    console.error('Error fetching bus location:', err);
  }
}

function updateUI(loc) {
  const elBusNumber = document.getElementById('track-bus-number');
  const elRoute = document.getElementById('track-route-name');
  const elStatus = document.getElementById('track-status');
  const elSpeed = document.getElementById('track-speed');
  const elETA = document.getElementById('track-eta');
  const elNextStop = document.getElementById('track-next-stop');
  const elUpdated = document.getElementById('track-last-updated');
  const elDemoBadge = document.getElementById('demo-mode-badge');

  if (elBusNumber) elBusNumber.innerText = loc.busNumber;
  if (elRoute) elRoute.innerText = loc.routeName;
  if (elStatus) {
    elStatus.innerText = loc.status.replace('_', ' ');
    elStatus.className = `badge ${loc.status === 'ON_ROUTE' ? 'badge-active' : 'badge-pending'}`;
  }
  if (elSpeed) elSpeed.innerText = `${loc.speed} km/h`;
  if (elETA) elETA.innerText = loc.estimatedArrival || '15 mins (approx)';
  if (elNextStop) elNextStop.innerText = loc.nextStopName || 'Campus Main Gate';
  if (elUpdated) {
    const d = new Date(loc.lastUpdated);
    elUpdated.innerText = d.toLocaleTimeString();
  }

  // Clearly show DEMO MODE tag if coordinates are simulated
  if (elDemoBadge) {
    elDemoBadge.style.display = loc.isSimulated ? 'inline-block' : 'none';
  }
}

function updateMapLocation(loc) {
  const lat = parseFloat(loc.latitude);
  const lng = parseFloat(loc.longitude);

  if (!busMarker) {
    busMarker = L.marker([lat, lng], { icon: busIcon }).addTo(map);
    busMarker.bindPopup(`<b>${loc.busNumber}</b><br>${loc.routeName}<br>Status: ${loc.status}`).openPopup();
    map.panTo([lat, lng]);
  } else {
    busMarker.setLatLng([lat, lng]);
    busMarker.setPopupContent(`<b>${loc.busNumber}</b><br>${loc.routeName}<br>Speed: ${loc.speed} km/h`);
  }
}

async function loadRouteStops(busId) {
  // Clear previous stop markers and lines
  stopMarkers.forEach(m => map.removeLayer(m));
  stopMarkers = [];
  if (routeLine) {
    map.removeLayer(routeLine);
    routeLine = null;
  }

  try {
    const res = await api.get('/routes/1'); // Default route
    const route = res.data;

    if (route.stops && route.stops.length > 0) {
      const latlngs = [];
      route.stops.forEach((s, idx) => {
        const pt = [parseFloat(s.latitude), parseFloat(s.longitude)];
        latlngs.push(pt);

        const m = L.marker(pt, { icon: stopIcon }).addTo(map);
        m.bindPopup(`<b>${idx + 1}. ${s.stopName}</b><br>Pickup: ${s.pickupTime}`);
        stopMarkers.push(m);
      });

      // Draw polyline connecting stops
      routeLine = L.polyline(latlngs, {
        color: '#567C8D',
        weight: 5,
        opacity: 0.85,
        dashArray: '8, 8'
      }).addTo(map);
    }
  } catch (err) {
    console.error('Failed to load stops:', err);
  }
}

// Simulated GPS Movement
async function triggerSimulationStep() {
  try {
    const res = await api.post(`/tracking/simulate-step/${selectedBusId}`);
    const loc = res.data;
    updateUI(loc);
    updateMapLocation(loc);
  } catch (err) {
    console.error('Simulation step error:', err);
  }
}

function toggleSimulation() {
  const btn = document.getElementById('btn-toggle-sim');
  if (isSimulating) {
    clearInterval(simulationInterval);
    isSimulating = false;
    if (btn) btn.innerText = 'Start Simulation';
    showToast('GPS movement simulation paused', 'info');
  } else {
    simulationInterval = setInterval(triggerSimulationStep, 2500);
    isSimulating = true;
    if (btn) btn.innerText = 'Pause Simulation';
    showToast('Simulated GPS tracking active (DEMO MODE)', 'info');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const urlParams = new URLSearchParams(window.location.search);
  const busParam = urlParams.get('busId');
  if (busParam) selectedBusId = parseInt(busParam);

  initMap();

  const btnSim = document.getElementById('btn-toggle-sim');
  if (btnSim) btnSim.addEventListener('click', toggleSimulation);

  const btnStep = document.getElementById('btn-step-sim');
  if (btnStep) btnStep.addEventListener('click', triggerSimulationStep);
});
