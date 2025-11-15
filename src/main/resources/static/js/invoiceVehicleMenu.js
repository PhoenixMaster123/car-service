function openVehicleDetailsModal(makeModel, year, engine, licensePlate, lastService, bodyType, color) {
    const modal = document.getElementById('vehicleModal');
    const overlay = document.getElementById('vehicleModalOverlay');

    // Set vehicle data
    document.getElementById('modalVehicleName').textContent = makeModel;
    document.getElementById('modalMakeModel').textContent = makeModel;
    document.getElementById('modalVehicleType').textContent = bodyType;
    document.getElementById('modalYear').textContent = year;
    document.getElementById('modalEngine').textContent = engine;
    document.getElementById('modalBodyType').textContent = bodyType;
    document.getElementById('modalLicensePlate').textContent = licensePlate;
    document.getElementById('modalColor').textContent = color;
    document.getElementById('modalLastService').textContent = lastService;

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

// Close on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeVehicleDetailsModal();
    }
});