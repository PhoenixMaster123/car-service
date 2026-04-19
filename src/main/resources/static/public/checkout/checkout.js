document.addEventListener("DOMContentLoaded", async function() {

    const paymentForm = document.getElementById('paymentForm');
    const submitBtn = document.getElementById('payBtn');
    const processingOverlay = document.getElementById('processingOverlay');
    const messageContainer = document.getElementById('payment-message');

    // If no payment form exists (dev mode), skip Stripe initialization
    if (!paymentForm) return;

    const publicKey = paymentForm.getAttribute('data-stripe-key');
    if (!publicKey || publicKey.startsWith('dummy') || publicKey === '${STRIPE_PUBLIC_KEY}') {
        console.warn("Stripe Public Key is missing or invalid. Dev mode active.");
        showMessage("Stripe not configured. Development mode active.");
        return;
    }

    const stripe = Stripe(publicKey);
    let elements;

    function getCsrf() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        return {
            token: tokenMeta ? tokenMeta.getAttribute('content') : null,
            header: headerMeta ? headerMeta.getAttribute('content') : null
        };
    }

    async function initialize() {
        try {
            let totalText = '';
            const totalEl = document.querySelector('.neon-text') || document.querySelector('#payBtn span');
            if (totalEl) totalText = totalEl.textContent || totalEl.innerText || '';

            totalText = totalText.replace(/[^0-9.,]/g, '').replace(',', '.');
            const totalFloat = parseFloat(totalText) || 0.0;
            const amount = Math.round(totalFloat * 100);

            const csrf = getCsrf();
            const requestOptions = {
                method: "POST",
                headers: Object.assign({
                    "Content-Type": "application/json"
                }, csrf.token ? { [csrf.header]: csrf.token } : {}),
                body: JSON.stringify({
                    amount: amount,
                    currency: 'EUR',
                    paymentMethod: 'card',
                    description: 'Haris Car Care Booking'
                }),
            };

            const response = await fetch("/payment/api/create-payment-intent", requestOptions);

            if (!response.ok) {
                throw new Error(`Server error: ${response.status}`);
            }

            const { clientSecret } = await response.json();

            const appearance = {
                theme: 'night',
                variables: {
                    colorPrimary: '#00ffff',
                    colorBackground: '#2a2a2a',
                    colorText: '#ffffff',
                    colorDanger: '#ff5e5e',
                    fontFamily: '"Poppins", sans-serif',
                    spacingUnit: '5px',
                    borderRadius: '8px',
                    actionColor: '#00ffff'
                },
                rules: {
                    '.Tab': {
                        border: '1px solid #444',
                        backgroundColor: '#1a1a1a',
                    },
                    '.Tab:hover': {
                        border: '1px solid #666',
                    },
                    '.Tab--selected': {
                        border: '1px solid #00ffff',
                        backgroundColor: 'rgba(0, 255, 255, 0.1)',
                        color: '#00ffff'
                    }
                }
            };

            elements = stripe.elements({ appearance, clientSecret });

            const paymentElement = elements.create("payment", {
                layout: "tabs",
            });
            paymentElement.mount("#payment-element");

        } catch (error) {
            console.error("Initialization Error:", error);
            showMessage("Failed to load payment system. Please refresh the page.");
        }
    }

    paymentForm.addEventListener("submit", async function(e) {
        e.preventDefault();
        setLoading(true);

        const { error } = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: window.location.origin + "/process-payment",
            },
        });

        if (error) {
            if (error.type === "card_error" || error.type === "validation_error") {
                showMessage(error.message);
            } else {
                showMessage("An unexpected error occurred.");
            }
        }

        setLoading(false);
    });

    function showMessage(messageText) {
        if (messageContainer) {
            messageContainer.classList.remove("hidden");
            messageContainer.style.display = "block";
            messageContainer.textContent = messageText;
        } else {
            alert(messageText);
        }
    }

    function setLoading(isLoading) {
        if (isLoading) {
            submitBtn.disabled = true;
            if (processingOverlay) processingOverlay.classList.add('active');
        } else {
            submitBtn.disabled = false;
            if (processingOverlay) processingOverlay.classList.remove('active');
        }
    }

    await initialize();
});