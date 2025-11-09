const menuToggle = document.getElementById("mobile-menu");
const navLinks = document.getElementById("nav-links");

menuToggle.addEventListener("click", () => {
    navLinks.classList.toggle("active");
    menuToggle.classList.toggle("active");
});

document.querySelectorAll('.dropdown .dropbtn').forEach(dropbtn => {
    dropbtn.addEventListener('click', function(e) {
        if (window.innerWidth <= 900) {
            e.preventDefault();
            const dropdownContent = this.nextElementSibling;
            const isVisible = dropdownContent.style.display === 'block';

            if (!isVisible) {
                document.querySelectorAll('.nav-links .dropdown-content').forEach(dc => {
                    dc.style.display = 'none';
                });
            }
            dropdownContent.style.display = isVisible ? 'none' : 'block';
        }
    });
});