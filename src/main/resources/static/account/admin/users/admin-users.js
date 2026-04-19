// ========================================
// ADMIN USERS PAGE FUNCTIONALITY
// ========================================

/**
 * Opens the View User modal and populates it with data
 * from the button's data-* attributes.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openViewUserModal(button) {
    const data = button.dataset;

    document.getElementById('viewUserName').textContent = data.firstName + ' ' + data.lastName;
    document.getElementById('viewUserEmail').textContent = data.email;
    document.getElementById('viewUserPhone').textContent = data.phone;
    document.getElementById('viewUserCountry').textContent = data.country;
    document.getElementById('viewUserRole').textContent = data.role;
    document.getElementById('viewUserRole').className = 'role-badge ' + (data.role === 'ADMIN' ? 'role-admin' : 'role-user');
    document.getElementById('viewUserCreated').textContent = data.created;
    document.getElementById('viewUserUpdated').textContent = data.updated;

    const overlay = document.getElementById('viewUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeViewUserModal() {
    const overlay = document.getElementById('viewUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
}

let currentEditUserId = null;

/**
 * Opens the Edit User modal and populates it with data
 * from the button's data-* attributes.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openEditUserModal(button) {
    const data = button.dataset;
    currentEditUserId = data.id;

    document.getElementById('editUserId').value = data.id;
    document.getElementById('editFirstName').value = data.firstName;
    document.getElementById('editLastName').value = data.lastName;
    document.getElementById('editEmail').value = data.email;
    document.getElementById('editPhone').value = data.phone;

    const countrySelect = document.getElementById('editCountry');
    const countryName = data.country;

    if (countryName && countryName !== 'N/A' && countryName !== '') {
        const options = countrySelect.options;
        let found = false;
        for (let i = 0; i < options.length; i++) {
            if (options[i].text === countryName) {
                countrySelect.value = options[i].value;
                found = true;
                break;
            }
        }
        if (!found) countrySelect.value = '';
    } else {
        countrySelect.value = '';
    }

    document.getElementById('editRole').value = data.role;

    const overlay = document.getElementById('editUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Edit User modal and resets the form.
 */
function closeEditUserModal() {
    const overlay = document.getElementById('editUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentEditUserId = null;
    document.getElementById('editUserForm').reset();
}

let currentDeleteUserId = null;
let currentDeleteUserName = null;

/**
 * Opens the Delete User confirmation modal.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openDeleteUserModal(button) {
    const userId = button.dataset.id;
    document.getElementById('deleteUserName').textContent = button.dataset.name;

    const form = document.getElementById('deleteUserForm');

    form.action = '/admin/delete-user/' + userId;

    const overlay = document.getElementById('deleteUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Delete User confirmation modal.
 */
function closeDeleteUserModal() {
    const overlay = document.getElementById('deleteUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentDeleteUserId = null;
    currentDeleteUserName = null;
}

/**
 * Opens the Add User modal.
 */
function openAddUserModal() {
    document.getElementById('addUserForm').reset();

    const overlay = document.getElementById('addUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

/**
 * Closes the Add User modal and resets the form.
 */
function closeAddUserModal() {
    const overlay = document.getElementById('addUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    document.getElementById('addUserForm').reset();
}

/**
 * Filters the admin users table based on the search input.
 */
function filterUsers() {
    const searchInput = document.getElementById('userSearch');
    const filter = searchInput.value.toLowerCase();
    const table = document.getElementById('usersTable');
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    const noResults = document.getElementById('noResults');

    let visibleCount = 0;

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const name = row.cells[0].textContent.toLowerCase();
        const email = row.cells[1].textContent.toLowerCase();
        const phone = row.cells[2].textContent.toLowerCase();

        if (name.includes(filter) || email.includes(filter) || phone.includes(filter)) {
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

// Global Event Listeners

/**
 * Closes all modals when the Escape key is pressed.
 */
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeViewUserModal();
        closeEditUserModal();
        closeDeleteUserModal();
        closeAddUserModal();
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
                case 'viewUserModal':
                    closeViewUserModal();
                    break;
                case 'editUserModal':
                    closeEditUserModal();
                    break;
                case 'deleteUserModal':
                    closeDeleteUserModal();
                    break;
                case 'addUserModal':
                    closeAddUserModal();
                    break;
            }
        }
    });
});