const EVENT_API_URL = 'http://localhost:8082/api/events';


document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (!token || localStorage.getItem('userRole') !== 'ORGANIZER') {
        globalThis.location.href = 'index.html';
        return;
    }
    document.getElementById('user-display-name').innerText = localStorage.getItem('userEmail');
    
    // Load events immediately on dashboard load
    fetchMyEvents();
});

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}

//Tab Switching
function switchTab(tab) {
    const myEventsSec = document.getElementById('my-events-section');
    const createSec = document.getElementById('create-section');
    const tabMyEvents = document.getElementById('tab-my-events');
    const tabCreate = document.getElementById('tab-create');

    if (tab === 'my-events') {
        myEventsSec.classList.remove('hidden');
        createSec.classList.add('hidden');
        tabMyEvents.style.color = 'var(--primary-teal)';
        tabMyEvents.style.fontWeight = 'bold';
        tabCreate.style.color = '#888';
        tabCreate.style.fontWeight = 'normal';
        fetchMyEvents(); 
    } else {
        myEventsSec.classList.add('hidden');
        createSec.classList.remove('hidden');
        tabCreate.style.color = 'var(--primary-teal)';
        tabCreate.style.fontWeight = 'bold';
        tabMyEvents.style.color = '#888';
        tabMyEvents.style.fontWeight = 'normal';
    }
}

//Fetch & show My Events
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
        //card creating
        const card = document.createElement('div');
        card.style.cssText = "border: 1px solid #ddd; border-radius: 8px; padding: 15px; background: #fafafa;";
        
        const eventJson = encodeURIComponent(JSON.stringify(event));
        
        card.innerHTML = `
            <h3 style="margin-bottom: 5px; color: #333;">${event.name}</h3>
            <p style="font-size: 13px; color: #666; margin-bottom: 10px;">${event.venue} • ₹${event.ticketPrice}</p>
            <p style="font-size: 12px; color: #888; margin-bottom: 15px;">Date: ${new Date(event.eventDateTime).toLocaleString()}</p>
            <button onclick="openUpdateModal('${eventJson}')" class="nav-btn-outline" style="width: 100%; font-size: 13px;">Update Event</button>
        `;
        grid.appendChild(card);
    });
}

//Create Event Logic
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

//Update Event Modal Logic
function openUpdateModal(eventJsonEncoded) {
    const event = JSON.parse(decodeURIComponent(eventJsonEncoded));
    
    //pre fill the form with existing data
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

//Submit Update Logic
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