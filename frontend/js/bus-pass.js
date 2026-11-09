// ==========================================================================
// TRANSITX - BUS PASS MANAGEMENT MODULE
// ==========================================================================

let availableRoutes = [];

async function initPassApplication() {
  if (!auth.requireAuth('ROLE_STUDENT')) return;

  const routeSelect = document.getElementById('pass-route-select');
  const stopSelect = document.getElementById('pass-stop-select');
  if (!routeSelect) return;

  try {
    const res = await api.get('/routes');
    availableRoutes = res.data || [];

    routeSelect.innerHTML = `<option value="">-- Choose a Bus Route --</option>` +
      availableRoutes.map(r => `<option value="${r.id}">${r.routeCode} - ${r.routeName}</option>`).join('');

    routeSelect.addEventListener('change', () => {
      const selectedId = parseInt(routeSelect.value);
      const route = availableRoutes.find(r => r.id === selectedId);

      if (route && route.stops && route.stops.length > 0) {
        stopSelect.innerHTML = `<option value="">-- Select Your Boarding Stop --</option>` +
          route.stops.map(s => `<option value="${s.id}">${s.sequence}. ${s.stopName} (Pickup: ${s.pickupTime})</option>`).join('');
        stopSelect.disabled = false;
      } else {
        stopSelect.innerHTML = `<option value="">No stops available for this route</option>`;
        stopSelect.disabled = true;
      }
    });
  } catch (err) {
    showToast('Failed to load routes for bus pass application', 'error');
  }

  const form = document.getElementById('apply-pass-form');
  if (form) {
    form.addEventListener('submit', handlePassSubmit);
  }
}

async function handlePassSubmit(e) {
  e.preventDefault();

  const routeId = document.getElementById('pass-route-select').value;
  const stopId = document.getElementById('pass-stop-select').value;
  const fileInput = document.getElementById('pass-id-proof');

  if (!routeId || !stopId) {
    showToast('Please select both a route and a boarding stop', 'error');
    return;
  }

  const formData = new FormData();
  formData.append('routeId', routeId);
  formData.append('boardingStopId', stopId);
  if (fileInput && fileInput.files[0]) {
    formData.append('idProof', fileInput.files[0]);
  }

  const submitBtn = e.target.querySelector('button[type="submit"]');
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.innerText = 'Submitting Application...';
  }

  try {
    const res = await api.postFormData('/bus-pass/apply', formData);
    showToast('Bus pass application submitted successfully!', 'success');
    setTimeout(() => {
      window.location.href = 'my-bus-pass.html';
    }, 1500);
  } catch (err) {
    showToast(err.message || 'Application failed', 'error');
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.innerText = 'Submit Bus Pass Application';
    }
  }
}

// Digital Bus Pass Rendering with QR Code
async function loadMyBusPass() {
  if (!auth.requireAuth('ROLE_STUDENT')) return;

  const container = document.getElementById('my-pass-view');
  if (!container) return;

  try {
    const res = await api.get('/bus-pass/my-pass');
    const pass = res.data;

    if (pass.status === 'PENDING') {
      container.innerHTML = `
        <div class="content-box" style="text-align: center; padding: 3rem;">
          <div style="font-size: 3rem; margin-bottom: 1rem;">⏳</div>
          <h2>Application Under Review</h2>
          <p style="margin: 1rem 0;">Your bus pass application (<strong>${pass.passCode}</strong>) is currently pending approval by the Transport Admin.</p>
          <div style="background: var(--sky-blue-light); padding: 1.25rem; border-radius: var(--radius-md); max-width: 480px; margin: 0 auto; text-align: left;">
            <div><strong>Applied Route:</strong> ${pass.routeName}</div>
            <div><strong>Boarding Stop:</strong> ${pass.boardingStopName}</div>
            <div><strong>Date Applied:</strong> ${new Date(pass.createdAt).toLocaleDateString()}</div>
          </div>
        </div>
      `;
      return;
    }

    if (pass.status === 'REJECTED') {
      container.innerHTML = `
        <div class="content-box" style="text-align: center; padding: 3rem; border-color: #F8B4B4;">
          <div style="font-size: 3rem; color: #E02424; margin-bottom: 1rem;">✖</div>
          <h2>Application Rejected</h2>
          <p style="color: #9B1C1C; margin: 1rem 0;"><strong>Reason:</strong> ${pass.rejectionReason || 'Incomplete verification documents'}</p>
          <a href="bus-pass.html" class="btn btn-teal">Re-apply for Bus Pass</a>
        </div>
      `;
      return;
    }

    // Approved: render digital bus pass
    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=140x140&data=${encodeURIComponent(pass.qrCodeData || pass.passCode)}`;

    container.innerHTML = `
      <div id="printable-pass" class="digital-pass-card">
        <div class="pass-header">
          <div class="pass-logo">🚌 TRANSITX</div>
          <span class="pass-code-badge">${pass.passCode}</span>
        </div>

        <div class="pass-grid">
          <div class="pass-details-list">
            <div class="pass-field">
              <div class="label">Student Name</div>
              <div class="value">${pass.studentName}</div>
            </div>
            <div class="pass-field">
              <div class="label">Register No</div>
              <div class="value">${pass.regNo}</div>
            </div>
            <div class="pass-field">
              <div class="label">Department</div>
              <div class="value">${pass.department}</div>
            </div>
            <div class="pass-field">
              <div class="label">Assigned Bus</div>
              <div class="value">${pass.busNumber}</div>
            </div>
            <div class="pass-field">
              <div class="label">Route</div>
              <div class="value">${pass.routeName}</div>
            </div>
            <div class="pass-field">
              <div class="label">Boarding Stop</div>
              <div class="value">${pass.boardingStopName}</div>
            </div>
            <div class="pass-field">
              <div class="label">Valid From</div>
              <div class="value">${pass.validityStart}</div>
            </div>
            <div class="pass-field">
              <div class="label">Valid Until</div>
              <div class="value" style="color: #68D391;">${pass.validityEnd}</div>
            </div>
          </div>

          <div class="pass-qr-container">
            <img src="${qrUrl}" alt="Pass QR Code" style="width: 120px; height: 120px;" />
            <div class="pass-qr-label">Scan to Verify</div>
          </div>
        </div>
      </div>

      <div style="display: flex; justify-content: center; gap: 1rem; margin-top: 1.5rem;">
        <button class="btn btn-teal" onclick="window.print()"><i class="fas fa-print"></i> Print Bus Pass</button>
        <button class="btn btn-navy" onclick="window.print()"><i class="fas fa-file-pdf"></i> Download as PDF</button>
      </div>
    `;

  } catch (err) {
    container.innerHTML = `
      <div class="content-box" style="text-align: center; padding: 3rem;">
        <h3>No Bus Pass Found</h3>
        <p style="margin: 1rem 0;">You have not applied for a college bus pass yet.</p>
        <a href="bus-pass.html" class="btn btn-teal">Apply for Bus Pass Now</a>
      </div>
    `;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  if (window.location.pathname.includes('bus-pass.html') && !window.location.pathname.includes('my-bus-pass')) {
    initPassApplication();
  } else if (window.location.pathname.includes('my-bus-pass.html')) {
    loadMyBusPass();
  }
});
