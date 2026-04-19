// ========================================
// ADMIN SERVICES PAGE FUNCTIONALITY
// ========================================

/**
 * Opens the View Service modal and populates it with data
 * from the button's data-* attributes.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openViewServiceModal(button) {
    const data = button.dataset;

    document.getElementById('viewServiceName').textContent = data.name;
    document.getElementById('viewServiceCategory').textContent = data.category;
    document.getElementById('viewServiceDescription').textContent = data.description;
    document.getElementById('viewServicePrice').textContent = '€' + parseFloat(data.price).toFixed(2);
    document.getElementById('viewServiceDuration').textContent = data.duration + ' min';

    const overlay = document.getElementById('viewServiceModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeViewServiceModal() {
    const overlay = document.getElementById('viewServiceModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
}

let currentEditServiceId = null;

/**
 * Opens the Edit Service modal and populates it with data
 * from the button's data-* attributes.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openEditServiceModal(button) {
    const data = button.dataset;
    currentEditServiceId = data.id;

    document.getElementById('editServiceId').value = data.id;
    document.getElementById('editServiceName').value = data.name;
    document.getElementById('editServiceDescription').value = data.description;
    document.getElementById('editServicePrice').value = data.price;
    document.getElementById('editServiceDuration').value = data.duration;

    const categorySelect = document.getElementById('editServiceCategory');
    categorySelect.value = data.categoryId;

    const overlay = document.getElementById('editServiceModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Edit Service modal and resets the form.
 */
function closeEditServiceModal() {
    const overlay = document.getElementById('editServiceModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentEditServiceId = null;
    document.getElementById('editServiceForm').reset();
}

let currentDeleteServiceId = null;
let currentDeleteServiceName = null;

/**
 * Opens the Delete Service confirmation modal.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openDeleteServiceModal(button) {
    const data = button.dataset;
    const serviceId = data.id;

    document.getElementById('deleteServiceName').textContent = data.name;

    const form = document.getElementById('deleteServiceForm');
    form.action = '/admin/delete-service/' + serviceId;

    const overlay = document.getElementById('deleteServiceModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Delete Service confirmation modal.
 */
function closeDeleteServiceModal() {
    const overlay = document.getElementById('deleteServiceModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentDeleteServiceId = null;
    currentDeleteServiceName = null;
}

/**
 * Deletes the service from the admin services table.
 */
function confirmDeleteService() {

    const csrfTokenTag = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderTag = document.querySelector('meta[name="_csrf_header"]');

    const csrfToken = csrfTokenTag ? csrfTokenTag.getAttribute('content') : '';
    const csrfHeader = csrfHeaderTag ? csrfHeaderTag.getAttribute('content') : 'X-CSRF-TOKEN';

    if (!currentDeleteServiceId) return;

    fetch('/admin/delete-service/' + currentDeleteServiceId, {
        method: 'DELETE',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(response => {
            if (response.ok) {
                const rowToRemove = document.querySelector(`tr[data-service-id="${currentDeleteServiceId}"]`);
                if (rowToRemove) {
                    rowToRemove.remove();
                }
                closeDeleteServiceModal();
            } else {
                alert('Error deleting service. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting service. Please try again.');
        });
}

/**
 * Opens the Add Service modal.
 */
function openAddServiceModal() {
    document.getElementById('addServiceForm').reset();

    const overlay = document.getElementById('addServiceModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Add Service modal and resets the form.
 */
function closeAddServiceModal() {
    const overlay = document.getElementById('addServiceModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    document.getElementById('addServiceForm').reset();
}

/**
 * Filters the admin services table based on the search input.
 */
function filterServices() {
    const searchInput = document.getElementById('serviceSearch');
    const filter = searchInput.value.toLowerCase();
    const table = document.getElementById('servicesTable');
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    const noResults = document.getElementById('noResults');

    let visibleCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const name = row.cells[0].textContent.toLowerCase();
        const category = row.cells[1].textContent.toLowerCase();
        const description = row.cells[2].textContent.toLowerCase();

        if (name.includes(filter) || category.includes(filter) || description.includes(filter)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    }

    if (visibleCount === 0 && rows.length > 0) {
        noResults.style.display = 'block';
    } else {
        noResults.style.display = 'none';
    }
}

/**
 * Closes all modals when the Escape key is pressed.
 */
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeViewServiceModal();
        closeEditServiceModal();
        closeDeleteServiceModal();
        closeAddServiceModal();
    }
});

/**
 * Closes the modal when the overlay is clicked.
 */
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(event) {
        if (event.target === overlay) {
            const modalId = overlay.id.replace('Overlay', '');
            switch(modalId) {
                case 'viewServiceModal':
                    closeViewServiceModal();
                    break;
                case 'editServiceModal':
                    closeEditServiceModal();
                    break;
                case 'deleteServiceModal':
                    closeDeleteServiceModal();
                    break;
                case 'addServiceModal':
                    closeAddServiceModal();
                    break;
            }
        }
    });
});