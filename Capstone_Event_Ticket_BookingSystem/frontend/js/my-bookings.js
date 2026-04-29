const BOOKING_API_URL = 'http://localhost:8082/api/bookings';
const EVENT_API_URL = 'http://localhost:8082/api/events';

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');
    
    if (!token || role !== 'CUSTOMER') {
        globalThis.location.href = 'index.html';
        return;
    }
    
    const email = localStorage.getItem('userEmail');
    if(email) {
        document.getElementById('user-display-name').textContent = `Hello, ${email.split('@')[0]}`;
    }
    
    fetchMyBookings();

    document.getElementById('logout-btn').addEventListener('click', logout);
    document.getElementById('close-qr-btn').addEventListener('click', closeQrModal);
    
    const qrModal = document.getElementById('qr-modal');
    qrModal.addEventListener('click', (e) => {
        if (e.target === qrModal) {
            closeQrModal();
        }
    });
});

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}

async function fetchMyBookings() {
    try {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${BOOKING_API_URL}/my-bookings`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to load bookings");

        const bookings = await response.json();
        const grid = document.getElementById('bookings-grid');
        grid.innerHTML = ''; // Clear loading states if any

        if (bookings.length === 0) {
            document.getElementById('no-bookings-msg').classList.remove('hidden');
        } else {
            document.getElementById('no-bookings-msg').classList.add('hidden');
            
            const template = document.getElementById('booking-card-template');
            
            for (const booking of bookings) {
                try {
                    const eventRes = await fetch(`${EVENT_API_URL}/${booking.eventId}`);
                    if (!eventRes.ok) continue;
                    const eventData = await eventRes.json();
                    
                    const eventName = eventData.name;
                    const eventDate = new Date(eventData.eventDateTime).toLocaleString();

                    const secureQrData = `EVENT-${booking.eventId}-TICKET-${booking.id}-${booking.userEmail}`;
                    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(secureQrData)}`;

                    const cardClone = template.content.cloneNode(true);
                    
                    cardClone.querySelector('.booking-event-name').textContent = eventName;
                    cardClone.querySelector('.booking-date-val').textContent = eventDate;
                    cardClone.querySelector('.booking-qty-val').textContent = booking.numberOfTickets;
                    cardClone.querySelector('.booking-price-val').textContent = booking.totalAmount.toFixed(2);
                    
                    const viewBtn = cardClone.querySelector('.btn-view-qr');
                    viewBtn.addEventListener('click', () => {
                        openQrModal(eventName, eventDate, booking.id, booking.numberOfTickets, qrUrl);
                    });

                    grid.appendChild(cardClone);
                } catch (err) {
                    console.error("Failed to load details for event ID:", booking.eventId, err);
                    const cardClone = template.content.cloneNode(true);
                    cardClone.querySelector('.booking-event-name').textContent = 'Failed to load event details';
                    cardClone.querySelector('.booking-date-val').textContent = 'N/A';
                    cardClone.querySelector('.booking-qty-val').textContent = booking.numberOfTickets;
                    cardClone.querySelector('.booking-price-val').textContent = booking.totalAmount.toFixed(2);
                    
                    const viewBtn = cardClone.querySelector('.btn-view-qr');
                    viewBtn.disabled = true;
                    viewBtn.textContent = 'Unavailable';

                    grid.appendChild(cardClone);
                }
            }
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

function openQrModal(eventName, eventDate, ticketId, qty, qrUrl) {
    document.getElementById('qr-event-name').textContent = `Entry Pass - ${eventName}`;
    document.getElementById('qr-event-date').textContent = eventDate;
    document.getElementById('qr-ticket-id').textContent = ticketId;
    document.getElementById('qr-ticket-qty').textContent = `Admit ${qty} Person(s)`;
    
    document.getElementById('qr-code-img').src = qrUrl;
    document.getElementById('qr-modal').classList.remove('hidden');
}

function closeQrModal() {
    document.getElementById('qr-modal').classList.add('hidden');
    setTimeout(() => {
        const qrImg = document.getElementById('qr-code-img');
        if(qrImg) qrImg.src = '';
    }, 200); 
}