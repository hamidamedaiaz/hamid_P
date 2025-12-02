import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { fetchDeliverySlots } from '../../services/customerService.js';
import { selectDeliverySlotForCart } from '../../services/cartService.js';
import './TimeSlotSelector.css';

export default function TimeSlotSelector({ restaurantId, onSlotSelected }) {
    const [slots, setSlots] = useState([]);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (restaurantId) {
            loadSlots();
        }
    }, [restaurantId]);

    async function loadSlots() {
        try {
            setLoading(true);
            const today = new Date().toISOString().split('T')[0];
            const data = await fetchDeliverySlots(restaurantId, today);
            setSlots(data.filter(slot => slot.available));
            setError(null);
        } catch (err) {
            console.error('Error loading delivery slots:', err);
            setError('Impossible de charger les créneaux de livraison');
        } finally {
            setLoading(false);
        }
    }

    async function handleSelectSlot(slot) {
        try {
            setLoading(true);
            console.log('🎯 Attempting to select slot:', {
                id: slot.id,
                idType: typeof slot.id,
                startTime: slot.startTime,
                endTime: slot.endTime,
                fullSlotObject: slot
            });
            await selectDeliverySlotForCart(slot.id);
            setSelectedSlot(slot);
            setError(null);

            if (onSlotSelected) {
                onSlotSelected(slot);
            }
        } catch (err) {
            console.error('Error selecting slot:', err);
            setError(err.message || 'Erreur lors de la sélection du créneau');
        } finally {
            setLoading(false);
        }
    }

    function formatTime(dateTime) {
        const date = new Date(dateTime);
        return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    }

    if (loading && slots.length === 0) {
        return <div className="time-slot-selector loading">Chargement des créneaux...</div>;
    }

    if (error) {
        return <div className="time-slot-selector error">{error}</div>;
    }

    if (slots.length === 0) {
        return (
            <div className="time-slot-selector empty">
                <p>Aucun créneau disponible pour aujourd'hui</p>
                <button onClick={loadSlots} className="btn-retry">Réessayer</button>
            </div>
        );
    }

    return (
        <div className="time-slot-selector">
            <h3>Choisissez un créneau de livraison</h3>
            <div className="slots-grid">
                {slots.map(slot => (
                    <button
                        key={slot.id}
                        className={`slot-card ${selectedSlot?.id === slot.id ? 'selected' : ''}`}
                        onClick={() => handleSelectSlot(slot)}
                        disabled={loading}
                    >
                        <div className="slot-time">
                            {formatTime(slot.startTime)} - {formatTime(slot.endTime)}
                        </div>
                        <div className="slot-capacity">
                            {slot.maxCapacity - slot.reservedCount} places restantes
                        </div>
                    </button>
                ))}
            </div>
            {selectedSlot && (
                <div className="selected-slot-info">
                    ✓ Créneau sélectionné: {formatTime(selectedSlot.startTime)} - {formatTime(selectedSlot.endTime)}
                </div>
            )}
        </div>
    );
}

TimeSlotSelector.propTypes = {
    restaurantId: PropTypes.string.isRequired,
    onSlotSelected: PropTypes.func
};

