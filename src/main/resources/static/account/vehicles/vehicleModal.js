function openVehicleDetailsModal(button) {
    const vehicleId = button.getAttribute('data-vehicle-id');
    const make = button.getAttribute('data-make');
    const model = button.getAttribute('data-model');
    const year = button.getAttribute('data-year');
    const engine = button.getAttribute('data-engine') || 'N/A';
    const body = button.getAttribute('data-body') || 'N/A';
    const plate = button.getAttribute('data-plate');
    const color = button.getAttribute('data-color') || 'N/A';
    const lastService = button.getAttribute('data-last-service') || '—';

    document.getElementById('modalVehicleName').innerText = `${make} ${model}`;
    document.getElementById('modalVehicleType').innerText = body;

    document.getElementById('modalMakeModel').innerText = `${make} ${model}`;
    document.getElementById('modalYear').innerText = year;
    document.getElementById('modalEngine').innerText = engine;
    document.getElementById('modalBodyType').innerText = body;

    document.getElementById('modalLicensePlate').innerText = plate;
    document.getElementById('modalColor').innerText = color;
    document.getElementById('modalLastService').innerText = lastService;

    const historyList = document.getElementById('modalServiceHistory');
    historyList.innerHTML = '';
    const template = vehicleId ? document.getElementById('vehicle-history-' + vehicleId) : null;
    if (template && template.content) {
        historyList.appendChild(template.content.cloneNode(true));
    } else {
        historyList.innerHTML = '<div class="service-history-empty">No service history yet for this vehicle.</div>';
    }

    const modal = document.getElementById('vehicleModal');
    const overlay = document.getElementById('vehicleModalOverlay');

    overlay.classList.add('active');
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeVehicleDetailsModal() {
    const modal = document.getElementById('vehicleModal');
    const overlay = document.getElementById('vehicleModalOverlay');
    overlay.classList.remove('active');
    modal.classList.remove('active');
    document.body.style.overflow = '';
}