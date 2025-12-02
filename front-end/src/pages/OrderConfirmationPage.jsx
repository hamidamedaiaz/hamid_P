import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchOrder } from '../services/orderService.js';
import Header from '../components/common/Header.jsx';
import Footer from '../components/common/Footer.jsx';
import LoadingSpinner from '../components/common/LoadingSpinner.jsx';
import './OrderConfirmationPage.css';

export default function OrderConfirmationPage() {
    const { orderId } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (orderId) {
            loadOrder();
        }
    }, [orderId]);

    async function loadOrder() {
        try {
            const data = await fetchOrder(orderId);
            setOrder(data);
        } catch (err) {
            console.error('Error loading order:', err);
            setError('Impossible de charger les détails de la commande');
        } finally {
            setLoading(false);
        }
    }

    function formatTime(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    }

    function formatDate(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        return date.toLocaleDateString('fr-FR');
    }

    if (loading) return <LoadingSpinner />;

    if (error || !order) {
        return (
            <div className="order-confirmation-page">
                <Header title="Confirmation" />
                <div className="confirmation-error">
                    <h2>Erreur</h2>
                    <p>{error || 'Commande introuvable'}</p>
                    <button onClick={() => navigate('/restaurants')} className="btn-primary">
                        Retour aux restaurants
                    </button>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="order-confirmation-page">
            <Header title="Confirmation de commande" />

            <div className="confirmation-container">
                <div className="success-icon">
                    <div className="checkmark">✓</div>
                </div>

                <h1>Commande confirmée !</h1>
                <p className="confirmation-message">
                    Merci pour votre commande. Un email de confirmation a été envoyé.
                </p>

                <div className="order-details">
                    <div className="detail-card">
                        <h2>Détails de la commande</h2>
                        <div className="detail-row">
                            <span className="label">Numéro de commande</span>
                            <span className="value">{order.id}</span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Statut</span>
                            <span className={`status ${order.status?.toLowerCase()}`}>
                                {order.status}
                            </span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Restaurant</span>
                            <span className="value">{order.restaurant?.name || 'N/A'}</span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Total</span>
                            <span className="value total-amount">
                                {order.totalAmount?.toFixed(2)} €
                            </span>
                        </div>
                    </div>

                    {order.deliverySlot && (
                        <div className="detail-card">
                            <h2>Livraison</h2>
                            <div className="delivery-slot-info">
                                <div className="slot-icon">🚚</div>
                                <div>
                                    <p className="slot-date">{formatDate(order.deliverySlot.startTime)}</p>
                                    <p className="slot-time">
                                        {formatTime(order.deliverySlot.startTime)} - {formatTime(order.deliverySlot.endTime)}
                                    </p>
                                </div>
                            </div>
                        </div>
                    )}

                    {order.items && order.items.length > 0 && (
                        <div className="detail-card">
                            <h2>Articles commandés</h2>
                            <div className="order-items">
                                {order.items.map((item, index) => (
                                    <div key={index} className="order-item">
                                        <span className="item-name">
                                            {item.dishName || item.name} x{item.quantity}
                                        </span>
                                        <span className="item-price">
                                            {((item.unitPrice || item.price) * item.quantity).toFixed(2)} €
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                <div className="action-buttons">
                    <button onClick={() => navigate('/restaurants')} className="btn-secondary">
                        Commander à nouveau
                    </button>
                    <button onClick={() => navigate('/')} className="btn-primary">
                        Retour à l'accueil
                    </button>
                </div>
            </div>

            <Footer />
        </div>
    );
}

