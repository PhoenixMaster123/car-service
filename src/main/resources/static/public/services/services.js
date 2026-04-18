document.addEventListener("DOMContentLoaded", function() {
    const cartBtn = document.getElementById('cart-btn');
    const closeCartBtn = document.getElementById('close-cart-btn');
    const cartModal = document.getElementById('cart-modal');
    const cartOverlay = document.getElementById('cart-overlay');

    function openCart() {
        cartModal.classList.add('active');
        cartOverlay.classList.add('active');
    }

    function closeCart() {
        cartModal.classList.remove('active');
        cartOverlay.classList.remove('active');
    }

    // Open
    if (cartBtn) {
        cartBtn.addEventListener('click', openCart);
    }

    // Close with X button
    if (closeCartBtn) {
        closeCartBtn.addEventListener('click', closeCart);
    }

    // Close with Overlay
    if (cartOverlay) {
        cartOverlay.addEventListener('click', closeCart);
    }
});