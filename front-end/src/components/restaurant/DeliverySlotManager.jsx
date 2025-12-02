import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { fetchDeliverySlots, createSlots } from '../../services/restaurantService.js';
import LoadingSpinner from '../common/LoadingSpinner.jsx';
import './DeliverySlotManager.css';

export default function DeliverySlotManager({ restaurantId }) {
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [formData, setFormData] = useState({
        date: new Date().toISOString().split('T')[0],
        start: '11:00',
        end: '14:00',
        maxCapacityPerSlot: 10
    });

    useEffect(() => {
        loadSlots();
    }, [restaurantId]);

    async function loadSlots() {
        try {
            setLoading(true);
            setError(null);
            const data = await fetchDeliverySlots(restaurantId);
            setSlots(data || []);
        } catch (err) {
            console.error('Error loading delivery slots:', err);
            setError('Impossible de charger les créneaux de livraison');
        } finally {
            setLoading(false);
        }
    }

    async function handleCreateSlots(e) {
        e.preventDefault();

        try {
            setLoading(true);
            setError(null);

            await createSlots(restaurantId, formData);

            // Réinitialiser le formulaire
            setFormData({
                date: new Date().toISOString().split('T')[0],
                start: '11:00',
                end: '14:00',
                maxCapacityPerSlot: 10
            });
            setShowForm(false);

            // Recharger les créneaux
            await loadSlots();

            alert('Créneaux créés avec succès !');
        } catch (err) {
            console.error('Error creating slots:', err);
            setError('Erreur lors de la création des créneaux');
        } finally {
            setLoading(false);
        }
    }

    function handleInputChange(e) {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'maxCapacityPerSlot' ? parseInt(value) : value
        }));
    }

    function formatDateTime(dateTime) {
        const date = new Date(dateTime);
        return date.toLocaleString('fr-FR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    if (loading && slots.length === 0) {
        return <LoadingSpinner message="Chargement des créneaux..." />;
    }

    return (
        <div className="delivery-slot-manager">
            <div className="manager-header">
                <h3 className="section-title">Gestion des créneaux de livraison</h3>
                <button
                    onClick={() => setShowForm(!showForm)}
                    className="btn btn-primary"
                    disabled={loading}
                >
                    {showForm ? 'Annuler' : '+ Créer des créneaux'}
                </button>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                    <button onClick={loadSlots} className="btn-retry">Réessayer</button>
                </div>
            )}

            {showForm && (
                <div className="slot-form-container">
                    <h4>Créer des créneaux de livraison</h4>
                    <form onSubmit={handleCreateSlots} className="slot-form">
                        <div className="form-group">
                            <label htmlFor="date">Date</label>
                            <input
                                type="date"
                                id="date"
                                name="date"
                                value={formData.date}
                                onChange={handleInputChange}
                                required
                                min={new Date().toISOString().split('T')[0]}
                            />
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="start">Heure de début</label>
                                <input
                                    type="time"
                                    id="start"
                                    name="start"
                                    value={formData.start}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="end">Heure de fin</label>
                                <input
                                    type="time"
                                    id="end"
                                    name="end"
                                    value={formData.end}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="maxCapacityPerSlot">Capacité maximale par créneau</label>
                            <input
                                type="number"
                                id="maxCapacityPerSlot"
                                name="maxCapacityPerSlot"
                                value={formData.maxCapacityPerSlot}
                                onChange={handleInputChange}
                                required
                                min="1"
                                max="100"
                            />
                            <small className="form-help">
                                Le système créera des créneaux d'une heure entre les heures de début et de fin
                            </small>
                        </div>

                        <div className="form-actions">
                            <button type="submit" className="btn btn-primary" disabled={loading}>
                                {loading ? 'Création...' : 'Créer les créneaux'}
                            </button>
                            <button
                                type="button"
                                onClick={() => setShowForm(false)}
                                className="btn btn-secondary"
                                disabled={loading}
                            >
                                Annuler
                            </button>
                        </div>
                    </form>
                </div>
            )}

            <div className="slots-list">
                <div className="slots-list-header">
                    <h4>Créneaux existants ({slots.length})</h4>
                    <button onClick={loadSlots} className="btn-refresh" disabled={loading}>
                        🔄 Actualiser
                    </button>
                </div>

                {slots.length === 0 ? (
                    <div className="empty-state">
                        <p>Aucun créneau de livraison n'a été créé.</p>
                        <p className="text-muted">Créez vos premiers créneaux pour permettre aux clients de commander.</p>
                    </div>
                ) : (
                    <div className="slots-grid">
                        {slots.map(slot => (
                            <div key={slot.id} className={`slot-card ${!slot.available ? 'unavailable' : ''}`}>
                                <div className="slot-header">
                                    <span className={`slot-status ${slot.available ? 'available' : 'unavailable'}`}>
                                        {slot.available ? '✓ Disponible' : '✗ Indisponible'}
                                    </span>
                                </div>

                                <div className="slot-time">
                                    <strong>Début :</strong> {formatDateTime(slot.startTime)}
                                </div>
                                <div className="slot-time">
                                    <strong>Fin :</strong> {formatDateTime(slot.endTime)}
                                </div>

                                <div className="slot-capacity">
                                    <div className="capacity-bar">
                                        <div
                                            className="capacity-fill"
                                            style={{
                                                width: `${(slot.reservedCount / slot.maxCapacity) * 100}%`
                                            }}
                                        />
                                    </div>
                                    <span className="capacity-text">
                                        {slot.reservedCount} / {slot.maxCapacity} réservations
                                    </span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

DeliverySlotManager.propTypes = {
    restaurantId: PropTypes.string.isRequired
};

