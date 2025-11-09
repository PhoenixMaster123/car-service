const themeToggles = document.querySelectorAll(".theme-toggle");

function applyTheme(theme) {
    document.body.classList.toggle("light", theme === "light");
    localStorage.setItem("theme", theme);
}

themeToggles.forEach(toggle => {
    toggle.addEventListener("click", () => {
        const newTheme = document.body.classList.contains("light") ? "dark" : "light";
        applyTheme(newTheme);
    });
});

window.addEventListener("DOMContentLoaded", () => {
    const theme = localStorage.getItem("theme") || "dark";
    applyTheme(theme);
});