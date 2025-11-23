// Initialize Flatpickr with improved settings
flatpickr("#bookingDate", {
    enableTime: true,
    dateFormat: "d.m.Y H:i",
    minDate: "today",
    time_24hr: true,
    disableMobile: "true", // Forces the styled calendar on mobile devices
    animate: true,
    onOpen: function(selectedDates, dateStr, instance) {
        // Optional: Add a glow effect to the input when calendar opens
        instance.element.classList.add('active-glow');
    },
    onClose: function(selectedDates, dateStr, instance) {
        instance.element.classList.remove('active-glow');
    }
});

document.addEventListener("DOMContentLoaded", function() {
    // Initialize button state
    updateSubmitButton();
});

function updateSubmitButton() {
    const paymentSelect = document.getElementById('paymentMethod');
    const submitBtnText = document.getElementById('btnText');
    const submitBtnIcon = document.getElementById('btnIcon');
    const form = document.getElementById('bookingForm');

    const method = paymentSelect.value;

    if (method === 'CARD' || method === 'PAYPAL') {
        // Scenario: Online Payment -> Go to Checkout
        submitBtnText.textContent = "Proceed to Checkout";
        submitBtnIcon.className = "fa-solid fa-credit-card submit-icon";

        // Change this URL to your actual payment controller endpoint
        form.action = "/bookings/checkout";
        // Ensure we submit using GET so the controller's @GetMapping handles it
        form.method = 'get';
    } else {
        // Scenario: Cash -> Create Booking & Go to Profile
        submitBtnText.textContent = "Confirm Booking";
        submitBtnIcon.className = "fa-solid fa-check submit-icon";

        // Change this URL to your standard creation endpoint
        form.action = "/bookings/create";
        // Use POST for creating a booking
        form.method = 'post';
    }
}