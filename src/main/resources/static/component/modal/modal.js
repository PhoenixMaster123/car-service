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

function openDeleteModal(button) {
    // 1. Get data from the button's data attributes
    const id = button.getAttribute('data-id');
    const vehicleName = button.getAttribute('data-name');

    // 2. Set the text in the modal
    const deleteNameSpan = document.getElementById('delete-vehicle-name');
    if (deleteNameSpan) {
        deleteNameSpan.textContent = vehicleName;
    }

    // 3. Set the hidden input ID (This matches the HTML I gave you in the previous step)
    // We do NOT change the form.action anymore, we just change the hidden input value
    const deleteIdInput = document.getElementById('delete-vehicle-id');
    if (deleteIdInput) {
        deleteIdInput.value = id;
    }

    // 4. Show Modal
    modalOverlay.classList.add('active');
    deleteModal.classList.add('active');
}

// --- CLOSE LOGIC ---
function closeModal() {
    modalOverlay.classList.remove('active');
    if (editModal) editModal.classList.remove('active');
    if (deleteModal) deleteModal.classList.remove('active');
}

if (modalOverlay) {
    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            closeModal();
        }
    });
}