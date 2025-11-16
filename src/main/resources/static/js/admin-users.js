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

// Edit User Modal
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
    const countryName = data.country; // This is the display name, e.g., "United States"

    if (countryName && countryName !== 'N/A' && countryName !== '') {
        const options = countrySelect.options;
        let found = false;
        for (let i = 0; i < options.length; i++) {
            // Match against the option's visible text
            if (options[i].text === countryName) {
                countrySelect.value = options[i].value;
                found = true;
                break;
            }
        }
        if (!found) countrySelect.value = ''; // Default if no match
    } else {
        countrySelect.value = ''; // No country provided
    }

    document.getElementById('editRole').value = data.role;

    const overlay = document.getElementById('editUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeEditUserModal() {
    const overlay = document.getElementById('editUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentEditUserId = null;
    document.getElementById('editUserForm').reset();
}

function saveUserEdit(event) {
    event.preventDefault();

    const formData = {
        userId: document.getElementById('editUserId').value,
        firstName: document.getElementById('editFirstName').value,
        lastName: document.getElementById('editLastName').value,
        email: document.getElementById('editEmail').value,
        phoneNumber: document.getElementById('editPhone').value,
        country: document.getElementById('editCountry').value,
        role: document.getElementById('editRole').value
    };

    // TODO: Add CSRF tokens to this fetch request
    fetch('/admin/users/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // [csrfHeader]: csrfToken  // <-- You will need to add this
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                alert('User updated successfully!');
                closeEditUserModal();
                location.reload();
            } else {
                alert('Error updating user. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating user. Please try again.');
        });
}

// Delete User Modal
let currentDeleteUserId = null;
let currentDeleteUserName = null;

/**
 * Opens the Delete User confirmation modal.
 * @param {HTMLButtonElement} button - The button element that was clicked.
 */
function openDeleteUserModal(button) {
    const data = button.dataset;

    currentDeleteUserId = data.id;
    currentDeleteUserName = data.name;
    document.getElementById('deleteUserName').textContent = data.name;

    const overlay = document.getElementById('deleteUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeDeleteUserModal() {
    const overlay = document.getElementById('deleteUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    currentDeleteUserId = null;
    currentDeleteUserName = null;
}

function confirmDeleteUser() {
    if (!currentDeleteUserId) return;

    // TODO: Add CSRF tokens to this fetch request
    fetch('/admin/users/delete/' + currentDeleteUserId, {
        method: 'DELETE',
        headers: {
            // [csrfHeader]: csrfToken  // <-- You will need to add this
        }
    })
        .then(response => {
            if (response.ok) {
                alert('User deleted successfully!');
                // Optimistically remove the row instead of reloading
                const rowToRemove = document.querySelector(`tr[data-user-id="${currentDeleteUserId}"]`);
                if (rowToRemove) {
                    rowToRemove.remove();
                }
                closeDeleteUserModal();
                // location.reload(); // Reloading is simpler but slower
            } else {
                alert('Error deleting user. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error deleting user. Please try again.');
        });
}

// Add User Modal
function openAddUserModal() {
    document.getElementById('addUserForm').reset();

    const overlay = document.getElementById('addUserModalOverlay');
    overlay.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeAddUserModal() {
    const overlay = document.getElementById('addUserModalOverlay');
    overlay.classList.remove('active');
    document.body.style.overflow = '';
    document.getElementById('addUserForm').reset();
}

function saveNewUser(event) {
    event.preventDefault();

    const formData = {
        firstName: document.getElementById('addFirstName').value,
        lastName: document.getElementById('addLastName').value,
        email: document.getElementById('addEmail').value,
        password: document.getElementById('addPassword').value,
        phoneNumber: document.getElementById('addPhone').value,
        country: document.getElementById('addCountry').value,
        role: document.getElementById('addRole').value
    };

    // TODO: Add CSRF tokens to this fetch request
    fetch('/admin/users/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // [csrfHeader]: csrfToken  // <-- You will need to add this
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                alert('User added successfully!');
                closeAddUserModal();
                location.reload();
            } else {
                response.json().then(data => {
                    alert(data.message || 'Error adding user. Please try again.');
                }).catch(() => {
                    alert('Error adding user. Please try again.');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error adding user. Please try again.');
        });
}

// Search/Filter Functionality
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
        // Only show "no results" if there are rows to hide
        noResults.style.display = 'block';
    } else {
        noResults.style.display = 'none';
    }
}

// Global Event Listeners

// Close modals on Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeViewUserModal();
        closeEditUserModal();
        closeDeleteUserModal();
        closeAddUserModal();
    }
});

// Close modals when clicking on the overlay
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(event) {
        // Check if the click is on the overlay itself, not a child (the modal)
        if (event.target === overlay) {
            // Find which modal this overlay belongs to and call its close function
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