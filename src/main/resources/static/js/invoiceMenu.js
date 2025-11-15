function openInvoiceModal(invoiceNumber, service, vehicle, serviceDate, dueDate, status, amount) {
    const modal = document.getElementById('invoiceModal');
    const overlay = document.getElementById('invoiceModalOverlay');
    const statusBadge = document.getElementById('modalInvoiceStatus');
    const payBtn = document.getElementById('modalPayBtn');
    const downloadBtn = document.getElementById('modalDownloadBtn');

    // Set invoice data
    document.getElementById('modalInvoiceNumber').textContent = invoiceNumber;
    document.getElementById('modalService').textContent = service;
    document.getElementById('modalVehicle').textContent = vehicle;
    document.getElementById('modalServiceDate').textContent = serviceDate;
    document.getElementById('modalInvoiceDate').textContent = serviceDate;
    document.getElementById('modalDueDate').textContent = dueDate;
    document.getElementById('modalPaymentStatus').textContent = status;
    document.getElementById('modalItemName').textContent = service;
    document.getElementById('modalItemPrice').textContent = amount;
    document.getElementById('modalSubtotal').textContent = amount;

    // Calculate tax and total
    const subtotal = parseFloat(amount);
    const tax = (subtotal * 0.20).toFixed(2);
    const total = (subtotal + parseFloat(tax)).toFixed(2);
    document.getElementById('modalTax').textContent = tax;
    document.getElementById('modalTotal').textContent = total;

    // Update status badge
    statusBadge.className = 'invoice-status-badge';
    if (status === 'Paid') {
        statusBadge.classList.add('status-paid');
        statusBadge.innerHTML = '<i class="fa-solid fa-check-circle"></i><span>Paid</span>';
        payBtn.style.display = 'none';
        downloadBtn.style.display = 'inline-flex';
    } else if (status === 'Pending') {
        statusBadge.classList.add('status-pending');
        statusBadge.innerHTML = '<i class="fa-solid fa-clock"></i><span>Pending</span>';
        payBtn.style.display = 'inline-flex';
        payBtn.className = 'btn btn-primary modal-btn-pay';
        downloadBtn.style.display = 'none';
    } else {
        statusBadge.classList.add('status-overdue');
        statusBadge.innerHTML = '<i class="fa-solid fa-exclamation-triangle"></i><span>Overdue</span>';
        payBtn.style.display = 'inline-flex';
        payBtn.className = 'btn btn-danger modal-btn-pay';
        downloadBtn.style.display = 'none';
    }

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