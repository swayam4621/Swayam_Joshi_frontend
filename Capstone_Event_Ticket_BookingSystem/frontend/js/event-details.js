const EVENT_API_URL = 'http://localhost:8082/api/events';

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (!token || localStorage.getItem('userRole') !== 'CUSTOMER') {
        globalThis.location.href = 'index.html';
        return;
    }

    loadEventDetails();
});

async function loadEventDetails() {
    const urlParams = new URLSearchParams(globalThis.location.search);
    const eventId = urlParams.get('id');

    const loadingDiv = document.getElementById('details-loading');
    const contentDiv = document.getElementById('event-content');

    if (!eventId) {
        loadingDiv.textContent = "Event not found.";
        return;
    }

    try {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${EVENT_API_URL}/${eventId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            if (response.status === 404) throw new Error("Event not found");
            throw new Error("Failed to load details");
        }

        const event = await response.json();

        document.getElementById('detail-title').textContent = event.name;
        document.getElementById('detail-location').textContent = event.venue;
        document.getElementById('detail-price').textContent = event.ticketPrice;
        document.getElementById('detail-seats').textContent = event.availableSeats;
        document.getElementById('detail-description').textContent = event.description || "No description provided by the organizer.";
        
        const dateOptions = { weekday: 'short', month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' };
        document.getElementById('detail-date').textContent = new Date(event.eventDateTime).toLocaleString('en-US', dateOptions);

        //display image
        const defaultImg = 'https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=1200';
        document.getElementById('detail-image').src = event.imageUrl || defaultImg;

        const statusEl = document.getElementById('detail-status');
        if (event.status === 'ACTIVE') {
            statusEl.textContent = "ACTIVE";
            statusEl.className = "badge badge-rounded badge-active";
        } else {
            statusEl.textContent = "CANCELLED";
            statusEl.className = "badge badge-rounded badge-cancelled";
        }

        //hide loading show content
        loadingDiv.classList.add('hidden');
        contentDiv.classList.remove('hidden');

        //Booking button logic
        const bookBtn = document.getElementById('book-ticket-btn');
        if (event.status === 'CANCELLED_BY_ORGANIZER') {
            bookBtn.textContent = "Event Cancelled";
            bookBtn.disabled = true;
            bookBtn.classList.add('btn-disabled');
        } else if (event.availableSeats <= 0) {
            bookBtn.textContent = "Sold Out";
            bookBtn.disabled = true;
            bookBtn.classList.add('btn-disabled');
        } else {
            bookBtn.addEventListener('click', () => {
                alert(`Proceeding to checkout for ${event.name}!`);
            });
        }

    } catch (error) {
        console.error(error);
        const loadingDiv = document.getElementById('details-loading');
        loadingDiv.textContent = `Could not load event details. ${error.message}`;
        loadingDiv.className = 'error-msg';
    }
}

// --- MOCK PAYMENT LOGIC ---
const BOOKING_API_URL = 'http://localhost:8082/api/bookings';

let currentEventPrice = 0;
let currentEventId = null;

function openPaymentModal(eventId, eventName, ticketPrice) {
    currentEventId = eventId;
    currentEventPrice = ticketPrice;
    
    document.getElementById('payment-event-name').textContent = eventName;
    document.getElementById('ticket-quantity').value = "1";
    calculateTotal();
    
    document.getElementById('payment-modal').classList.remove('hidden');
}

function closePaymentModal() {
    document.getElementById('payment-modal').classList.add('hidden');
    document.getElementById('payment-form').reset();
    document.getElementById('payment-error').classList.add('hidden');
    document.getElementById('payment-success').classList.add('hidden');
    resetPayButton();
}

function calculateTotal() {
    const qty = parseInt(document.getElementById('ticket-quantity').value);
    const total = qty * currentEventPrice;
    document.getElementById('payment-total').textContent = total.toFixed(2);
}

document.getElementById('book-ticket-btn').addEventListener('click', () => {
    const title = document.getElementById('detail-title').textContent;
    const price = parseFloat(document.getElementById('detail-price').textContent);
    
    const urlParams = new URLSearchParams(globalThis.location.search);
    const eventId = urlParams.get('id');

    openPaymentModal(eventId, title, price);
});

//Handle the fake payment submission
document.getElementById('payment-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const qty = parseInt(document.getElementById('ticket-quantity').value);
    const totalAmount = qty * currentEventPrice;
    const errorDiv = document.getElementById('payment-error');
    const successDiv = document.getElementById('payment-success');
    
    //fake loading spinner
    const btnText = document.getElementById('pay-btn-text');
    const spinner = document.getElementById('pay-spinner');
    const payBtn = document.getElementById('pay-btn');
    
    btnText.textContent = "Processing...";
    spinner.classList.remove('hidden');
    payBtn.disabled = true;
    errorDiv.classList.add('hidden');

    //simulating a 2 second bank processing delay
    setTimeout(async () => {
        try {
            const token = localStorage.getItem('jwtToken');
            
            const bookingRequest = {
                eventId: currentEventId,
                numberOfTickets: qty,
                totalAmount: totalAmount
            };

            const response = await fetch(`${BOOKING_API_URL}/create`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(bookingRequest)
            });

           const responseText = await response.text(); 
            let responseData = {};
            
            if (responseText) {
                try {
                    responseData = JSON.parse(responseText);
                } catch (parseError) {
                    console.warn("Backend didn't send JSON. Raw response:", responseText);
                }
            }

            // Handle Errors
            if (!response.ok) {
                throw new Error(responseData.error || responseText || "Payment failed or seats unavailable.");
            }

            // Success!
            document.getElementById('payment-form').style.display = 'none';
            successDiv.textContent = "Booking Confirmed! Redirecting...";
            successDiv.classList.remove('hidden');

            setTimeout(() => {
                globalThis.location.href = 'index.html'; 
            }, 2000);

        } catch (error) {
            errorDiv.textContent = error.message;
            errorDiv.classList.remove('hidden');
            resetPayButton();
        }
    }, 2000);
});

function resetPayButton() {
    document.getElementById('pay-btn-text').textContent = "Pay Now";
    document.getElementById('pay-spinner').classList.add('hidden');
    document.getElementById('pay-btn').disabled = false;
}

const paymentModal = document.getElementById('payment-modal');
if (paymentModal) {
    paymentModal.addEventListener('click', (e) => {
        if (e.target === paymentModal) closePaymentModal();
    });
}

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}