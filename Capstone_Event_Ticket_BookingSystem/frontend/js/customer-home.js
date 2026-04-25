const EVENT_API_URL = 'http://localhost:8082/api/events';
let currentSubTab = 'upcoming';

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const role = localStorage.getItem('userRole');

    if (!token || role !== 'CUSTOMER') {
        alert("Please log in as a customer to view this page.");
        globalThis.location.href = 'index.html';
        return;
    }

    const userEmail = localStorage.getItem('userEmail');
    document.getElementById('user-greeting').textContent = `Hello, ${userEmail.split('@')[0]}`;

    fetchAllEvents();
});

async function fetchAllEvents() {
    const gridId = `${currentSubTab}-grid`;
    const container = document.getElementById(gridId);
    const template = document.getElementById('customer-event-card-template');
    const statusMsg = document.getElementById('status-msg');

    container.innerHTML = ''; 

    statusMsg.textContent = 'Loading events...';
    statusMsg.classList.remove('hidden', 'error-msg');
    statusMsg.classList.add('status-message');

    try {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`${EVENT_API_URL}?filter=${currentSubTab}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`, 
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error("Failed to fetch events");
        
        const events = await response.json();

        if (events.length === 0) {
            statusMsg.textContent = `No ${currentSubTab} events available right now.`;
            return;
        }

        statusMsg.classList.add('hidden');

        events.forEach(event => {
            if (event.status === 'CANCELLED_BY_ORGANIZER') return; 

            const cardClone = template.content.cloneNode(true);

            cardClone.querySelector('.card-title').textContent = event.name;
            cardClone.querySelector('.date-text span').textContent = new Date(event.eventDateTime).toLocaleString();
            cardClone.querySelector('.venue-text span').textContent = event.venue;
            cardClone.querySelector('.price-text span').textContent = event.ticketPrice;
            
            const linkBtn = cardClone.querySelector('.details-link');
            linkBtn.href = `event-details.html?id=${event.id}`;
            
            if (currentSubTab === 'past') {
                linkBtn.textContent = 'View Past Event';
                linkBtn.classList.add('btn-past-event'); 
            }

            container.appendChild(cardClone);
        });

    } catch (error) {
        console.error('Error loading events:', error);
        statusMsg.textContent = 'Error loading events. Please try again later.';
        statusMsg.classList.add('error-msg');
        statusMsg.classList.remove('hidden', 'status-message');
    }
}
function switchSubTab(subTabName) {
    document.getElementById('upcoming-grid').classList.add('hidden');
    document.getElementById('past-grid').classList.add('hidden');
    
    document.getElementById('subtab-upcoming').classList.replace('active-tab', 'inactive-tab');
    document.getElementById('subtab-past').classList.replace('active-tab', 'inactive-tab');

    document.getElementById(`${subTabName}-grid`).classList.remove('hidden');
    document.getElementById(`subtab-${subTabName}`).classList.replace('inactive-tab', 'active-tab');

    currentSubTab = subTabName;
    fetchAllEvents(); 
}

function logout() {
    localStorage.clear();
    globalThis.location.href = 'index.html';
}