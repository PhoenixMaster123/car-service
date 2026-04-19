function openInvoiceModal(button) {
    const modal = document.getElementById('invoiceModal');
    const overlay = document.getElementById('invoiceModalOverlay');

    const bookingId = button.getAttribute('data-booking-id');
    const service = button.getAttribute('data-service');
    const vehicle = button.getAttribute('data-vehicle');
    const date = button.getAttribute('data-date');
    const status = button.getAttribute('data-status');
    const amount = button.getAttribute('data-amount');

    // Set modal data
    document.getElementById('modalInvoiceNumber').textContent = bookingId ? bookingId.substring(0, 8).toUpperCase() : '---';
    document.getElementById('modalService').textContent = service || '---';
    document.getElementById('modalVehicle').textContent = vehicle || '---';
    document.getElementById('modalServiceDate').textContent = date || '---';
    document.getElementById('modalPaymentStatus').textContent = status || '---';
    document.getElementById('modalItemName').textContent = service || '---';
    document.getElementById('modalItemPrice').textContent = amount || '0.00';
    document.getElementById('modalTotal').textContent = amount || '0.00';

    // Update status badge
    const statusBadge = document.getElementById('modalInvoiceStatus');
    statusBadge.className = 'invoice-status-badge status-paid';
    statusBadge.innerHTML = '<i class="fa-solid fa-check-circle"></i><span>Paid</span>';

    // Set download link
    const downloadBtn = document.getElementById('modalDownloadBtn');
    downloadBtn.href = '/users/invoices/' + bookingId + '/download';

    overlay.classList.add('active');
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeInvoiceModal() {
    const modal = document.getElementById('invoiceModal');
    const overlay = document.getElementById('invoiceModalOverlay');
    overlay.classList.remove('active');
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

// Close on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeInvoiceModal();
    }
});

// Search filter
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const query = this.value.toLowerCase();
            const cards = document.querySelectorAll('.invoice-card');
            cards.forEach(function(card) {
                const text = (card.getAttribute('data-search-text') || '').toLowerCase();
                card.style.display = text.includes(query) ? '' : 'none';
            });
        });
    }
});