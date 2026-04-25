const EVENT_API_URL = 'http://localhost:8082/api/events';
let currentSubTab = 'upcoming';

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

//Tab switching 
function switchMainTab(tabId) {
    const myEventsSection = document.getElementById('my-events-section');
    const createSection = document.getElementById('create-section');
    const tabMyEvents = document.getElementById('tab-my-events');
    const tabCreate = document.getElementById('tab-create');

    myEventsSection.classList.add('hidden');
    createSection.classList.add('hidden');
    tabMyEvents.classList.replace('active-tab', 'inactive-tab');
    tabCreate.classList.replace('active-tab', 'inactive-tab');

    if (tabId === 'my-events') {
        myEventsSection.classList.remove('hidden');
        tabMyEvents.classList.replace('inactive-tab', 'active-tab');
        fetchMyEvents();
    } else if (tabId === 'create') {
        createSection.classList.remove('hidden');
        tabCreate.classList.replace('inactive-tab', 'active-tab');
    }
}

function switchSubTab(subTabName) {
    const grids = ['upcoming-grid', 'past-grid', 'cancelled-grid'];
    const subTabs = ['subtab-upcoming', 'subtab-past', 'subtab-cancelled'];

    grids.forEach(id => document.getElementById(id).classList.add('hidden'));
    subTabs.forEach(id => document.getElementById(id).classList.replace('active-tab', 'inactive-tab'));

    document.getElementById(`${subTabName}-grid`).classList.remove('hidden');
    document.getElementById(`subtab-${subTabName}`).classList.replace('inactive-tab', 'active-tab');

    currentSubTab = subTabName;
    fetchMyEvents();
}

//Fetch events
async function fetchMyEvents() {
    const token = localStorage.getItem('jwtToken');
    const gridId = `${currentSubTab}-grid`;
    const sectionTitle = currentSubTab.charAt(0).toUpperCase() + currentSubTab.slice(1);

    try {
        const response = await fetch(`${EVENT_API_URL}/my-events?filter=${currentSubTab}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to load events");

        const events = await response.json();
        const grid = document.getElementById(gridId);
        grid.innerHTML = '';

        if (events.length === 0) {
            document.getElementById('no-events-msg').classList.remove('hidden');
        } else {
            document.getElementById('no-events-msg').classList.add('hidden');
            renderEvents(events, gridId, sectionTitle);
        }
    } catch (error) {
        console.error(`Error fetching ${currentSubTab} events:`, error);
    }
}

// Render events
function renderEvents(events, gridId, sectionTitle) {
    const grid = document.getElementById(gridId);
    const template = document.getElementById('event-card-template');

    if (!grid || !template) return;

    events.forEach(event => {
        const cardClone = template.content.cloneNode(true);
        const eventJson = encodeURIComponent(JSON.stringify(event));

        cardClone.querySelector('.card-title').textContent = event.name;
        cardClone.querySelector('.date-text span').textContent = new Date(event.eventDateTime).toLocaleString();
        cardClone.querySelector('.venue-text span').textContent = event.venue;
        cardClone.querySelector('.price-text span').textContent = event.ticketPrice;
        cardClone.querySelector('.seats-text span').textContent = `${event.availableSeats} / ${event.totalSeats} Available`;

        const header = cardClone.querySelector('.card-header');
        const badge = document.createElement('span');
        badge.className = event.status === 'ACTIVE' ? 'badge badge-active' : 'badge badge-cancelled';
        badge.textContent = event.status;
        header.appendChild(badge);

        const actionsContainer = cardClone.querySelector('.card-actions');

        const viewBtn = document.createElement('button');
        viewBtn.className = 'btn primary-btn btn-view-details';
        viewBtn.textContent = 'View Details';
        viewBtn.onclick = () => openDetailsModal(eventJson);
        actionsContainer.appendChild(viewBtn);

        if (sectionTitle === 'Upcoming') {
            const btnGroup = document.createElement('div');
            btnGroup.className = 'btn-group-row';

            btnGroup.innerHTML = `
                <button onclick="openUpdateModal('${eventJson}')" class="btn-edit">Edit</button>
                <button onclick="cancelEvent(${event.id})" class="btn-cancel">Cancel</button>
            `;
            actionsContainer.appendChild(btnGroup);
        } else if (sectionTitle === 'Past') {
            const completedBadge = document.createElement('span');
            completedBadge.className = 'badge badge-completed-full';
            completedBadge.textContent = 'COMPLETED';
            actionsContainer.appendChild(completedBadge);
        }

        grid.appendChild(cardClone);
    });
}

// Create Event Logic
document.getElementById('create-event-form').addEventListener('submit', async function (e) {
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
            const successMsg = document.getElementById('event-success');
            successMsg.innerText = "Event published successfully!";
            successMsg.classList.remove('hidden');
            this.reset();
            setTimeout(() => {
                successMsg.classList.add('hidden');
                switchMainTab('my-events');
            }, 1500);
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
    document.getElementById('upd-date').value = new Date(event.eventDateTime).toISOString().slice(0, 16);
    document.getElementById('upd-location').value = event.venue;
    document.getElementById('upd-price').value = event.ticketPrice;
    document.getElementById('update-modal').classList.remove('hidden');
}

function closeUpdateModal() {
    document.getElementById('update-modal').classList.add('hidden');
}

//Update event logic
document.getElementById('update-event-form').addEventListener('submit', async function (e) {
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
            let errorMessage = "Forbidden: Your token was rejected.";
            try {
                const errorData = JSON.parse(text);
                errorMessage = errorData.error || errorMessage;
            } catch (error_) {
                console.error("Error parsing error response:", error_);
            }
            const errorEl = document.getElementById('upd-error');
            errorEl.innerText = errorMessage;
            errorEl.classList.remove('hidden');
        }
    } catch (error) {
        console.error("Error updating event:", error);
    }
});

//View details of event logic
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
    statusEl.innerText = event.status;
    statusEl.className = `badge badge-rounded ${event.status === 'ACTIVE' ? 'badge-active' : 'badge-cancelled'}`;

    document.getElementById('details-modal').classList.remove('hidden');
}

function closeDetailsModal() {
    document.getElementById('details-modal').classList.add('hidden');
}

//Cancel event logic
async function cancelEvent(eventId) {
    if (!confirm("Are you sure? This cannot be undone.")) return;
    try {
        const response = await fetch(`${EVENT_API_URL}/cancel/${eventId}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` }
        });
        if (response.ok) {
            alert("Event cancelled.");
            fetchMyEvents();
        }
    } catch (error) {
        console.error("Error cancelling event:", error);
        alert("Server connection failed.");
    }
}