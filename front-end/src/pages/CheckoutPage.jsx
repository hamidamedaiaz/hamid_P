import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchCart, processCartPayment } from '../services/cartService.js';
import Header from '../components/common/Header.jsx';
import Footer from '../components/common/Footer.jsx';
import LoadingSpinner from '../components/common/LoadingSpinner.jsx';
import './CheckoutPage.css';

export default function CheckoutPage() {
    const navigate = useNavigate();
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const [error, setError] = useState(null);

    const [paymentData, setPaymentData] = useState({
        userId: '550e8400-e29b-41d4-a716-446655440000',
        cardNumber: '',
        cardHolder: '',
        expiryDate: '',
        cvv: ''
    });

    useEffect(() => {
        loadCart();
    }, []);

    async function loadCart() {
        try {
            const data = await fetchCart();
            if (!data || !data.items || data.items.length === 0) {
                navigate('/cart');
                return;
            }
            setCart(data);
        } catch (err) {
            setError('Erreur lors du chargement du panier');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }

    function handleInputChange(e) {
        const { name, value } = e.target;
        setPaymentData(prev => ({
            ...prev,
            [name]: value
        }));
    }

    async function handleSubmit(e) {
        e.preventDefault();

        if (!cart?.deliverySlotId) {
            alert('Veuillez sélectionner un créneau de livraison dans le panier');
            navigate('/cart');
            return;
        }

        setProcessing(true);
        setError(null);

        try {
            const orderData = {
                userId: paymentData.userId,
                deliverySlotId: cart.deliverySlotId,
                paymentMethod: 'CARD',
                cardNumber: paymentData.cardNumber.replace(/\s/g, ''),
                cardHolderName: paymentData.cardHolder,
                expiryDate: paymentData.expiryDate,
                cvv: paymentData.cvv
            };

            const result = await processCartPayment(orderData);

            // Rediriger vers la page de confirmation
            navigate(`/order-confirmation/${result.id || result.orderId}`);

        } catch (err) {
            console.error('Payment error:', err);
            setError(err.message || 'Erreur lors du paiement. Veuillez réessayer.');
        } finally {
            setProcessing(false);
        }
    }

    if (loading) return <LoadingSpinner />;

    if (!cart) {
        return (
            <div className="checkout-page">
                <Header title="Paiement" />
                <div className="checkout-empty">
                    <p>Votre panier est vide</p>
                    <button onClick={() => navigate('/restaurants')} className="btn-primary">
                        Voir les restaurants
                    </button>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="checkout-page">
            <Header title="Paiement" />

            <div className="checkout-container">
                <div className="checkout-form">
                    <h2>Informations de paiement</h2>

                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="cardHolder">Nom du titulaire</label>
                            <input
                                type="text"
                                id="cardHolder"
                                name="cardHolder"
                                value={paymentData.cardHolder}
                                onChange={handleInputChange}
                                placeholder="Jean Dupont"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="cardNumber">Numéro de carte</label>
                            <input
                                type="text"
                                id="cardNumber"
                                name="cardNumber"
                                value={paymentData.cardNumber}
                                onChange={handleInputChange}
                                placeholder="1234 5678 9012 3456"
                                maxLength="19"
                                required
                            />
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="expiryDate">Date d'expiration</label>
                                <input
                                    type="text"
                                    id="expiryDate"
                                    name="expiryDate"
                                    value={paymentData.expiryDate}
                                    onChange={handleInputChange}
                                    placeholder="MM/YY"
                                    maxLength="5"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="cvv">CVV</label>
                                <input
                                    type="text"
                                    id="cvv"
                                    name="cvv"
                                    value={paymentData.cvv}
                                    onChange={handleInputChange}
                                    placeholder="123"
                                    maxLength="3"
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn-pay"
                            disabled={processing}
                        >
                            {processing ? 'Traitement...' : `Payer ${cart.totalAmount?.toFixed(2)} €`}
                        </button>
                    </form>

                    <button onClick={() => navigate('/cart')} className="btn-back">
                        ← Retour au panier
                    </button>
                </div>

                <div className="order-summary">
                    <h2>Récapitulatif</h2>

                    <div className="summary-items">
                        {cart.items?.map(item => (
                            <div key={item.dishId} className="summary-item">
                                <span className="item-name">
                                    {item.dishName} x{item.quantity}
                                </span>
                                <span className="item-price">
                                    {(item.unitPrice * item.quantity).toFixed(2)} €
                                </span>
                            </div>
                        ))}
                    </div>

                    <div className="summary-total">
                        <span>Total</span>
                        <span className="total-amount">{cart.totalAmount?.toFixed(2)} €</span>
                    </div>

                    {cart.deliverySlotId && (
                        <div className="delivery-info">
                            ✓ Créneau de livraison sélectionné
                        </div>
                    )}
                </div>
            </div>

            <Footer />
        </div>
    );
}

