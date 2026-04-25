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

    if (!eventId) {
        document.getElementById('details-loading').textContent = "Event not found.";
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
        document.getElementById('detail-date').textContent = new Date(event.eventDateTime).toLocaleString();
        document.getElementById('detail-location').textContent = event.venue;
        document.getElementById('detail-price').textContent = event.ticketPrice;
        document.getElementById('detail-seats').textContent = event.availableSeats;
        document.getElementById('detail-description').textContent = event.description;

        const statusEl = document.getElementById('detail-status');
        if (event.status === 'ACTIVE') {
            statusEl.textContent = "ACTIVE";
            statusEl.className = "badge badge-rounded badge-active";
        } else {
            statusEl.textContent = "CANCELLED";
            statusEl.className = "badge badge-rounded badge-cancelled";
        }

        document.getElementById('details-loading').classList.add('hidden');
        document.getElementById('event-content').classList.remove('hidden');

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

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}