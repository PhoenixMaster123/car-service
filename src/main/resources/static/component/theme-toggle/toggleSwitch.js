const themeToggles = document.querySelectorAll(".theme-toggle");

function applyTheme(theme) {
    if (theme === "light") {
        document.body.classList.add("light");
    } else {
        document.body.classList.remove("light");
    }
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