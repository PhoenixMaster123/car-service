document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.getElementById("mobile-menu");
    const navLinks = document.getElementById("nav-links");

    if (menuToggle && navLinks) {
        menuToggle.addEventListener("click", () => {
            navLinks.classList.toggle("active");
            menuToggle.classList.toggle("active");
        });
    }

    const dropdowns = document.querySelectorAll('.dropdown .dropbtn');
    if (dropdowns.length > 0) {
        dropdowns.forEach(dropbtn => {
            dropbtn.addEventListener('click', function(e) {
                if (window.innerWidth <= 900) {
                    e.preventDefault();
                    const dropdownContent = this.nextElementSibling;

                    if (dropdownContent) {
                        const isVisible = dropdownContent.style.display === 'block';

                        if (!isVisible) {
                            document.querySelectorAll('.nav-links .dropdown-content').forEach(dc => {
                                dc.style.display = 'none';
                            });
                        }
                        dropdownContent.style.display = isVisible ? 'none' : 'block';
                    }
                }
            });
        });
    }
});