// ==========================================================================
// TRANSITX - BUS ROUTES MODULE
// ==========================================================================

let allRoutes = [];

async function loadRoutes() {
  const container = document.getElementById('routes-container');
  if (!container) return;

  container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 3rem;"><p>Loading bus routes...</p></div>`;

  try {
    const res = await api.get('/routes');
    allRoutes = res.data || [];
    renderRoutes(allRoutes);
  } catch (err) {
    container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: #E02424;"><p>Failed to load routes. Ensure the backend server is running.</p></div>`;
  }
}

function renderRoutes(routes) {
  const container = document.getElementById('routes-container');
  if (!container) return;

  if (routes.length === 0) {
    container.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 3rem; background: var(--white); border-radius: var(--radius-md);">
        <h3>No routes found</h3>
        <p>Try refining your search keyword.</p>
      </div>`;
    return;
  }

  container.innerHTML = routes.map(r => `
    <div class="route-card">
      <div class="route-card-header">
        <span class="route-code-badge">${r.routeCode}</span>
        <span style="font-size: 0.85rem; font-weight: 600; color: var(--navy);"><i class="fas fa-bus"></i> ${r.assignedBusNumber || 'Bus TBD'}</span>
      </div>
      <div class="route-card-body">
        <h3 style="margin-bottom: 0.5rem; font-size: 1.25rem;">${r.routeName}</h3>
        <div class="route-path">
          <span>${r.startPoint}</span>
          <span style="color: var(--teal);">➔</span>
          <span>${r.endPoint}</span>
        </div>
        <div class="route-meta">
          <div><strong>Departure:</strong> ${r.departureTime}</div>
          <div><strong>Arrival:</strong> ${r.arrivalTime}</div>
          <div><strong>Duration:</strong> ${r.estDuration}</div>
          <div><strong>Total Stops:</strong> ${r.totalStops} stops</div>
        </div>
      </div>
      <div class="route-card-footer">
        <span style="font-size: 0.85rem; color: var(--text-muted);"><i class="fas fa-user-tie"></i> ${r.assignedDriverName || 'Driver TBD'}</span>
        <button class="btn btn-teal btn-sm" onclick="openRouteDetailsModal(${r.id})">View Route Details</button>
      </div>
    </div>
  `).join('');
}

function filterRoutes() {
  const searchInput = document.getElementById('route-search-input');
  if (!searchInput) return;

  const keyword = searchInput.value.toLowerCase().trim();
  if (!keyword) {
    renderRoutes(allRoutes);
    return;
  }

  const filtered = allRoutes.filter(r => 
    r.routeName.toLowerCase().includes(keyword) ||
    r.routeCode.toLowerCase().includes(keyword) ||
    r.startPoint.toLowerCase().includes(keyword) ||
    r.endPoint.toLowerCase().includes(keyword) ||
    (r.assignedBusNumber && r.assignedBusNumber.toLowerCase().includes(keyword))
  );

  renderRoutes(filtered);
}

async function openRouteDetailsModal(routeId) {
  const modal = document.getElementById('route-modal');
  const title = document.getElementById('modal-route-title');
  const body = document.getElementById('modal-route-body');

  if (!modal) return;

  modal.classList.add('open');
  title.innerText = 'Loading route details...';
  body.innerHTML = '<p>Fetching stops sequence...</p>';

  try {
    const res = await api.get(`/routes/${routeId}`);
    const route = res.data;

    title.innerText = `${route.routeCode}: ${route.routeName}`;

    let stopsHtml = `
      <div style="background: var(--sky-blue-light); padding: 1.25rem; border-radius: var(--radius-md); margin-bottom: 1.5rem;">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; font-size: 0.95rem;">
          <div><strong>Origin:</strong> ${route.startPoint}</div>
          <div><strong>Destination:</strong> ${route.endPoint}</div>
          <div><strong>Departure:</strong> ${route.departureTime}</div>
          <div><strong>Arrival:</strong> ${route.arrivalTime}</div>
          <div><strong>Distance:</strong> ${route.distanceKm} km</div>
          <div><strong>Est. Time:</strong> ${route.estDuration}</div>
        </div>
      </div>
      <h4 style="margin-bottom: 1rem; color: var(--navy);">Bus Stops & Schedule:</h4>
      <div style="border-left: 3px solid var(--teal); padding-left: 1.25rem; margin-left: 0.5rem;">
    `;

    if (route.stops && route.stops.length > 0) {
      stopsHtml += route.stops.map((s, idx) => `
        <div style="position: relative; margin-bottom: 1.25rem;">
          <div style="position: absolute; left: -1.7rem; top: 2px; width: 14px; height: 14px; border-radius: 50%; background: var(--teal); border: 2px solid var(--white);"></div>
          <div style="font-weight: 700; color: var(--navy);">${idx + 1}. ${s.stopName}</div>
          <div style="font-size: 0.85rem; color: var(--text-muted);">
            Pickup: <span style="font-weight: 600; color: var(--teal);">${s.pickupTime}</span> 
            ${s.landmark ? `• Landmark: ${s.landmark}` : ''}
          </div>
        </div>
      `).join('');
    } else {
      stopsHtml += `<p>No sequential stops registered for this route.</p>`;
    }

    stopsHtml += `</div>
      <div style="margin-top: 2rem; display: flex; justify-content: flex-end; gap: 1rem;">
        <a href="bus-tracking.html?busId=1" class="btn btn-teal btn-sm">Live Track This Bus</a>
        <button class="btn btn-navy btn-sm" onclick="closeRouteModal()">Close</button>
      </div>
    `;

    body.innerHTML = stopsHtml;
  } catch (err) {
    body.innerHTML = `<p style="color: #E02424;">Failed to load route details.</p>`;
  }
}

function closeRouteModal() {
  const modal = document.getElementById('route-modal');
  if (modal) modal.classList.remove('open');
}

document.addEventListener('DOMContentLoaded', () => {
  loadRoutes();
  const searchInput = document.getElementById('route-search-input');
  if (searchInput) {
    searchInput.addEventListener('input', filterRoutes);
  }
});
