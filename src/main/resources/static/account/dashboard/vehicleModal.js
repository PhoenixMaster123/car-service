function openVehicleDetailsModal(button) {
    // 1. Extract data from the clicked button
    const make = button.getAttribute('data-make');
    const model = button.getAttribute('data-model');
    const year = button.getAttribute('data-year');
    const engine = button.getAttribute('data-engine') || 'N/A';
    const body = button.getAttribute('data-body') || 'N/A';
    const plate = button.getAttribute('data-plate');
    const color = button.getAttribute('data-color') || 'N/A';

    // 2. Populate the Modal DOM elements
    document.getElementById('modalVehicleName').innerText = `${make} ${model}`;
    document.getElementById('modalVehicleType').innerText = body;

    document.getElementById('modalMakeModel').innerText = `${make} ${model}`;
    document.getElementById('modalYear').innerText = year;
    document.getElementById('modalEngine').innerText = engine;
    document.getElementById('modalBodyType').innerText = body;

    document.getElementById('modalLicensePlate').innerText = plate;
    document.getElementById('modalColor').innerText = color;

    // 3. Show the modal
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