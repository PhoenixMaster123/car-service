const chatToggle = document.getElementById('chat-toggle');
const chatWidget = document.getElementById('chat-widget');
const chatCloseBtn = document.getElementById('chat-close-btn');
const chatForm = document.getElementById('chat-form');
const chatInput = document.getElementById('chat-input');
const chatBody = document.getElementById('chat-body');

function toggleChat() {
    chatWidget.classList.toggle('active');
    chatToggle.classList.toggle('active');
}

chatToggle.addEventListener('click', toggleChat);
chatCloseBtn.addEventListener('click', toggleChat);

chatForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const userMessage = chatInput.value.trim();
    if (userMessage === "") return;

    displayMessage(userMessage, 'user');
    chatInput.value = "";

    displayMessage("...", 'typing');

    const botResponse = await getBotResponse(userMessage);

    const typingMessage = chatBody.querySelector('.typing');
    if (typingMessage) {
        typingMessage.remove();
    }

    displayMessage(botResponse, 'bot');
});

function displayMessage(message, sender) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('chat-message', sender);

    if (sender === 'typing') {
        messageElement.innerHTML = `<p><i class="fa-solid fa-spinner fa-spin"></i></p>`; // Simple typing indicator
    } else {
        messageElement.innerHTML = `<p>${message}</p>`;
    }

    chatBody.appendChild(messageElement);
    chatBody.scrollTop = chatBody.scrollHeight;
}

async function getBotResponse(message) {

    try {
        const response = await fetch('/api/gemini/ask', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({prompt: message})
        });

        if (!response.ok) {
            throw new Error(`API Error: ${response.status} ${response.statusText}`);
        }

        const data = await response.json();

        return data.response;

    } catch (error) {
        console.error("Error fetching bot response:", error);
        return "Sorry, I couldn't connect to the AI assistant. Please try again later.";
    }
}