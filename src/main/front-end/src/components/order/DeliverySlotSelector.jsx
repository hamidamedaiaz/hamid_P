import { useState } from 'react';

const DeliverySlotSelector = ({ selectedSlot, onSelect }) => {
    // Générer des créneaux horaires pour les 3 prochains jours
    const generateTimeSlots = () => {
        const slots = [];
        const today = new Date();

        for (let dayOffset = 0; dayOffset < 3; dayOffset++) {
            const date = new Date(today);
            date.setDate(date.getDate() + dayOffset);

            // Créneaux : 12h-13h, 13h-14h, 18h-19h, 19h-20h
            const times = [
                { start: '12:00', end: '13:00' },
                { start: '13:00', end: '14:00' },
                { start: '18:00', end: '19:00' },
                { start: '19:00', end: '20:00' }
            ];

            times.forEach(time => {
                const slotDateTime = new Date(date);
                const [hours, minutes] = time.start.split(':');
                slotDateTime.setHours(parseInt(hours), parseInt(minutes), 0, 0);

                // Ne pas afficher les créneaux passés pour aujourd'hui
                if (slotDateTime > new Date()) {
                    slots.push({
                        id: slotDateTime.toISOString(),
                        date: date,
                        time: time,
                        display: formatSlotDisplay(date, time)
                    });
                }
            });
        }

        return slots;
    };

    const formatSlotDisplay = (date, time) => {
        const days = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
        const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];

        const dayName = days[date.getDay()];
        const day = date.getDate();
        const month = months[date.getMonth()];

        return `${dayName} ${day} ${month} - ${time.start} à ${time.end}`;
    };

    const slots = generateTimeSlots();

    return (
        <div className="delivery-slot-selector" style={{ marginTop: '20px' }}>
            <h3 style={{ marginBottom: '15px', fontSize: '18px' }}>
                Sélectionnez un créneau de livraison
            </h3>

            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                gap: '10px'
            }}>
                {slots.map((slot) => (
                    <button
                        key={slot.id}
                        onClick={() => onSelect(slot.id)}
                        className={selectedSlot === slot.id ? 'selected' : ''}
                        style={{
                            padding: '15px',
                            border: selectedSlot === slot.id ? '2px solid #FF6B35' : '1px solid #ddd',
                            borderRadius: '8px',
                            backgroundColor: selectedSlot === slot.id ? '#FFF5F2' : 'white',
                            cursor: 'pointer',
                            textAlign: 'left',
                            transition: 'all 0.2s ease',
                            fontSize: '14px',
                            fontWeight: selectedSlot === slot.id ? 'bold' : 'normal'
                        }}
                    >
                        {slot.display}
                    </button>
                ))}
            </div>

            {!selectedSlot && (
                <p style={{
                    marginTop: '10px',
                    color: '#ff4444',
                    fontSize: '14px',
                    fontStyle: 'italic'
                }}>
                    * Veuillez sélectionner un créneau pour continuer
                </p>
            )}
        </div>
    );
};

export default DeliverySlotSelector;
