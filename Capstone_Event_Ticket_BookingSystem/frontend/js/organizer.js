const EVENT_API_URL = 'http://localhost:8082/api/events';

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (!token || localStorage.getItem('userRole') !== 'ORGANIZER') {
        globalThis.location.href = 'index.html';
        return;
    }
    document.getElementById('user-display-name').innerText = localStorage.getItem('userEmail');
    fetchMyEvents();
});

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}

// Tab Switching
function switchTab(tab) {
    const myEventsSec = document.getElementById('my-events-section');
    const createSec = document.getElementById('create-section');
    const tabMyEvents = document.getElementById('tab-my-events');
    const tabCreate = document.getElementById('tab-create');

    if (tab === 'my-events') {
        myEventsSec.classList.remove('hidden');
        createSec.classList.add('hidden');
        tabMyEvents.className = 'tab-btn active-tab';
        tabCreate.className = 'tab-btn inactive-tab';
        fetchMyEvents(); 
    } else {
        myEventsSec.classList.add('hidden');
        createSec.classList.remove('hidden');
        tabCreate.className = 'tab-btn active-tab';
        tabMyEvents.className = 'tab-btn inactive-tab';
    }
}

// Fetch & show My Events
async function fetchMyEvents() {
    try {
        const response = await fetch(`${EVENT_API_URL}/my-events`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` }
        });
        
        if (response.ok) {
            const events = await response.json();
            renderEvents(events);
        }
    } catch (error) {
        console.error("Failed to load events", error);
    }
}

function renderEvents(events) {
    const grid = document.getElementById('events-grid');
    const noEventsMsg = document.getElementById('no-events-msg');
    grid.innerHTML = '';

    if (events.length === 0) {
        noEventsMsg.classList.remove('hidden');
        return;
    }
    
    noEventsMsg.classList.add('hidden');
    
    events.forEach(event => {
        const card = document.createElement('div');
        card.className = "event-card"; // Replaced inline CSS
        
        const eventJson = encodeURIComponent(JSON.stringify(event));

        // Replaced inline CSS with badge classes
        let statusBadge = event.status === 'ACTIVE' 
            ? `<span class="badge badge-active">ACTIVE</span>` 
            : `<span class="badge badge-cancelled">CANCELLED</span>`;
        
        let actionButtons = '';
        if (event.status === 'ACTIVE') {
            actionButtons = `
                <div class="card-actions">
                    <button onclick="openUpdateModal('${eventJson}')" class="btn-edit">Edit</button>
                    <button onclick="cancelEvent(${event.id})" class="btn-cancel">Cancel</button>
                </div>
            `;
        }

        // Replaced all inline CSS with classes
        card.innerHTML = `
            <div class="card-header">
                <h3 class="card-title">${event.name}</h3>
                ${statusBadge}
            </div>
            <p class="card-text">${event.venue}</p>
            <p class="card-text-sm">${new Date(event.eventDateTime).toLocaleString()}</p>
            
            <button onclick="openDetailsModal('${eventJson}')" class="btn primary-btn btn-view-details">View Details</button>
            ${actionButtons}
        `;
        grid.appendChild(card);
    });
}

// Create Event Logic
document.getElementById('create-event-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const eventData = {
        title: document.getElementById('event-title').value,
        description: document.getElementById('event-desc').value,
        eventDate: document.getElementById('event-date').value, 
        location: document.getElementById('event-location').value,
        price: Number.parseFloat(document.getElementById('event-price').value),
        totalTickets: Number.parseInt(document.getElementById('event-tickets').value)
    };

    try {
        const response = await fetch(`${EVENT_API_URL}/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            },
            body: JSON.stringify(eventData)
        });

        if (response.ok) {
            document.getElementById('event-success').innerText = "Event published successfully!";
            document.getElementById('event-success').classList.remove('hidden');
            document.getElementById('create-event-form').reset();
            setTimeout(() => switchTab('my-events'), 1500); 
        }
    } catch (error) {
        console.error("Error creating event:", error);
    }
});

// Update Event Modal Logic
function openUpdateModal(eventJsonEncoded) {
    const event = JSON.parse(decodeURIComponent(eventJsonEncoded));
    
    document.getElementById('update-event-id').value = event.id;
    document.getElementById('upd-title').value = event.name;
    document.getElementById('upd-desc').value = event.description;
    document.getElementById('upd-tickets').value = event.totalSeats;

    const dateObj = new Date(event.eventDateTime);
    document.getElementById('upd-date').value = dateObj.toISOString().slice(0, 16); 
    
    document.getElementById('upd-location').value = event.venue;
    document.getElementById('upd-price').value = event.ticketPrice;
    
    document.getElementById('update-modal').classList.remove('hidden');
}

function closeUpdateModal() {
    document.getElementById('update-modal').classList.add('hidden');
}

// Submit Update Logic
document.getElementById('update-event-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const eventId = document.getElementById('update-event-id').value;
    
    const updateData = {
        title: document.getElementById('upd-title').value,
        description: document.getElementById('upd-desc').value,
        eventDate: document.getElementById('upd-date').value,
        location: document.getElementById('upd-location').value,
        price: Number.parseFloat(document.getElementById('upd-price').value),
        totalTickets: Number.parseInt(document.getElementById('upd-tickets').value)
    };

    try {
        const response = await fetch(`${EVENT_API_URL}/update/${eventId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            },
            body: JSON.stringify(updateData)
        });

        if (response.ok) {
            closeUpdateModal();
            fetchMyEvents(); 
        } else {
            const text = await response.text();
            let errorMessage = "Forbidden: Your token was rejected by the Event Service.";
            if (text) {
                try {
                    const errorData = JSON.parse(text);
                    errorMessage = errorData.error || errorMessage;
                } catch (e) { console.error("Could not parse error JSON"); }
            }
            document.getElementById('upd-error').innerText = errorMessage;
            document.getElementById('upd-error').classList.remove('hidden');
        }
    } catch (error) {
        console.error("Error updating event:", error);
    }
});

// Event Details Logic
function openDetailsModal(eventJsonEncoded) {
    const event = JSON.parse(decodeURIComponent(eventJsonEncoded));
    
    document.getElementById('det-title').innerText = event.name;
    document.getElementById('det-desc').innerText = event.description;
    document.getElementById('det-date').innerText = new Date(event.eventDateTime).toLocaleString();
    document.getElementById('det-venue').innerText = event.venue;
    document.getElementById('det-price').innerText = event.ticketPrice;
    document.getElementById('det-avail').innerText = event.availableSeats;
    document.getElementById('det-total').innerText = event.totalSeats;

    const statusEl = document.getElementById('det-status');
    
    if (event.status === 'ACTIVE') {
        statusEl.innerText = "ACTIVE";
        statusEl.className = "badge badge-rounded badge-active";
    } else {
        statusEl.innerText = "CANCELLED";
        statusEl.className = "badge badge-rounded badge-cancelled";
    }

    document.getElementById('details-modal').classList.remove('hidden');
}

function closeDetailsModal() {
    document.getElementById('details-modal').classList.add('hidden');
}

// Cancel Event 
async function cancelEvent(eventId) {
    if (!confirm("Are you sure you want to cancel this event? This action cannot be undone.")) {
        return;
    }

    try {
        const response = await fetch(`${EVENT_API_URL}/cancel/${eventId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            }
        });

        if (response.ok) {
            alert("Event successfully cancelled.");
            fetchMyEvents(); 
        } else {
            const errorData = await response.json();
            alert("Failed to cancel event: " + (errorData.error || "Unknown error"));
        }
    } catch (error) {
        console.error("Error cancelling event:", error);
        alert("Server connection failed.");
    }
}