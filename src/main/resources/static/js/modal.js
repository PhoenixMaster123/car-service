const modalOverlay = document.getElementById('modalOverlay');
const editModal = document.getElementById('editVehicleModal');
const deleteModal = document.getElementById('deleteVehicleModal');

// --- EDIT MODAL LOGIC ---
function openEditModal(button) {
    const id = button.getAttribute('data-id');
    const make = button.getAttribute('data-make');
    const model = button.getAttribute('data-model');
    const year = button.getAttribute('data-year');
    const license = button.getAttribute('data-license');
    const vin = button.getAttribute('data-vin');
    const color = button.getAttribute('data-color');

    document.getElementById('edit-id').value = id;
    document.getElementById('edit-make').value = make;
    document.getElementById('edit-model').value = model;
    document.getElementById('edit-year').value = year;
    document.getElementById('edit-licensePlate').value = license;
    document.getElementById('edit-vin').value = vin;
    document.getElementById('edit-color').value = color;

    modalOverlay.classList.add('active');
    editModal.classList.add('active');
}

// --- DELETE MODAL LOGIC ---
function openDeleteModal(id, vehicleName) {
    const deleteNameSpan = document.getElementById('delete-vehicle-name');
    const deleteForm = document.getElementById('deleteVehicleForm');

    deleteNameSpan.textContent = vehicleName;

    deleteForm.action = '/vehicles/delete/' + id;

    modalOverlay.classList.add('active');
    deleteModal.classList.add('active');
}

// --- CLOSE LOGIC ---
function closeModal() {
    modalOverlay.classList.remove('active');
    editModal.classList.remove('active');
    deleteModal.classList.remove('active');
}
    modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) {
    closeModal();
}
});