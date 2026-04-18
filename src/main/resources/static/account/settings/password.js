document.addEventListener("DOMContentLoaded", function() {

    // ---  PASSWORD STRENGTH METER ---
    const passwordInput = document.getElementById('newPassword');
    const strengthBar = document.getElementById('strength-bar');
    const strengthLabel = document.getElementById('strength-label');

    if (passwordInput && strengthBar && strengthLabel) {

        passwordInput.addEventListener('input', () => {
            const password = passwordInput.value;
            if (password.length === 0) {
                strengthBar.className = 'strength-bar';
                strengthLabel.textContent = '';
                return;
            }

            const strength = checkPasswordStrength(password);

            strengthBar.className = 'strength-bar ' + strength.scoreClass;
            strengthLabel.textContent = strength.label;
        });
    }

    /**
     * Calculates the strength of a password
     */
    function checkPasswordStrength(password) {
        let score = 0;

        if (password.length > 8) score++;
        if (password.match(/[a-z]/)) score++;
        if (password.match(/[A-Z]/)) score++;
        if (password.match(/[0-9]/)) score++;
        if (password.match(/[^A-Za-z0-9]/)) score++;

        if (score <= 2) {
            return { scoreClass: 'weak', label: 'Weak' };
        } else if (score <= 4) {
            return { scoreClass: 'medium', label: 'Medium' };
        } else {
            return { scoreClass: 'strong', label: 'Strong' };
        }
    }

    // --- PASSWORD TOGGLE VISIBILITY ---

    document.querySelectorAll('.password-toggle').forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const targetInput = document.getElementById(targetId);
            const icon = this.querySelector('.password-icon');

            if (targetInput && icon) {
                if (targetInput.type === 'password') {
                    targetInput.type = 'text';
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                } else {
                    targetInput.type = 'password';
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                }
            }
        });
    });
});

/** Scroll to the status message if the has error message */
document.addEventListener("DOMContentLoaded", () => {
    const successMessage = document.getElementById("status-message");
    const errorMessage = document.getElementById("error-message");

    let messageToScroll = null;
    if (successMessage && successMessage.style.display !== 'none') {
        messageToScroll = successMessage;
    } else if (errorMessage && errorMessage.style.display !== 'none') {
        messageToScroll = errorMessage;
    }

    if (messageToScroll) {
        messageToScroll.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
});