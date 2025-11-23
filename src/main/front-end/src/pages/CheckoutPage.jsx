import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import cartService from '../services/cartService';
import orderService from '../services/orderService';
import DeliverySlotSelector from '../components/order/DeliverySlotSelector';
import PaymentForm from '../components/order/PaymentForm';
import Loading from '../components/common/Loading';

const CheckoutPage = () => {
    const [currentStep, setCurrentStep] = useState(1);
    const [cart, setCart] = useState(null);
    const [orderId, setOrderId] = useState(null);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { userId } = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        loadCart();
    }, [userId]);

    const loadCart = async () => {
        try {
            const cartData = await cartService.getCart(userId);
            if (!cartData || !cartData.items || cartData.items.length === 0) {
                alert('Votre panier est vide');
                navigate('/cart');
                return;
            }
            setCart(cartData);
        } catch (err) {
            console.error('Erreur lors du chargement du panier:', err);
            setError('Impossible de charger votre panier');
        }
    };

    const handleCreateOrder = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await orderService.createOrder(userId);
            setOrderId(response.orderId);
            setCurrentStep(2);
        } catch (err) {
            console.error('Erreur lors de la création de la commande:', err);
            setError('Erreur lors de la création de la commande');
        } finally {
            setLoading(false);
        }
    };

    const handleSelectDeliverySlot = async () => {
        if (!selectedSlot) {
            alert('Veuillez sélectionner un créneau de livraison');
            return;
        }

        setLoading(true);
        setError(null);
        try {
            await orderService.selectDeliverySlot(orderId, selectedSlot);
            setCurrentStep(3);
        } catch (err) {
            console.error('Erreur lors de la sélection du créneau:', err);
            setError('Erreur lors de la sélection du créneau');
        } finally {
            setLoading(false);
        }
    };

    const handlePayment = async (paymentData) => {
        setLoading(true);
        setError(null);
        try {
            // Étape 1: Traiter le paiement
            await orderService.processPayment(orderId, paymentData);

            // Étape 2: Confirmer la commande
            await orderService.confirmOrder(orderId);

            // Étape 3: Rediriger vers la page de confirmation
            navigate(`/order-confirmation?orderId=${orderId}`);
        } catch (err) {
            console.error('Erreur lors du paiement:', err);
            setError('Erreur lors du paiement. Veuillez réessayer.');
        } finally {
            setLoading(false);
        }
    };

    if (!cart) {
        return <Loading message="Chargement..." />;
    }

    return (
        <div style={{ maxWidth: '900px', margin: '0 auto', padding: '20px' }}>
            <h1 style={{ marginBottom: '30px', fontSize: '32px' }}> Finaliser ma commande</h1>

            {/* Progress Steps */}
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                marginBottom: '40px',
                position: 'relative'
            }}>
                <div style={{
                    position: 'absolute',
                    top: '15px',
                    left: '0',
                    right: '0',
                    height: '2px',
                    backgroundColor: '#ddd',
                    zIndex: 0
                }}></div>

                {[
                    { num: 1, label: 'Récapitulatif' },
                    { num: 2, label: 'Livraison' },
                    { num: 3, label: 'Paiement' }
                ].map((step) => (
                    <div key={step.num} style={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        zIndex: 1,
                        backgroundColor: 'white'
                    }}>
                        <div style={{
                            width: '30px',
                            height: '30px',
                            borderRadius: '50%',
                            backgroundColor: currentStep >= step.num ? '#FF6B35' : '#ddd',
                            color: 'white',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: 'bold',
                            marginBottom: '5px'
                        }}>
                            {step.num}
                        </div>
                        <span style={{
                            fontSize: '12px',
                            color: currentStep >= step.num ? '#FF6B35' : '#999'
                        }}>
                            {step.label}
                        </span>
                    </div>
                ))}
            </div>

            {error && (
                <div style={{
                    padding: '15px',
                    backgroundColor: '#ffebee',
                    color: '#c62828',
                    borderRadius: '5px',
                    marginBottom: '20px'
                }}>
                     {error}
                </div>
            )}

            {/* Step 1: Récapitulatif */}
            {currentStep === 1 && (
                <div>
                    <h2 style={{ marginBottom: '20px', fontSize: '24px' }}>
                         Récapitulatif de votre commande
                    </h2>

                    <div style={{
                        backgroundColor: '#f9f9f9',
                        padding: '20px',
                        borderRadius: '8px',
                        marginBottom: '20px'
                    }}>
                        {cart.items.map((item) => (
                            <div key={item.dishId} style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                padding: '10px 0',
                                borderBottom: '1px solid #ddd'
                            }}>
                                <div>
                                    <strong>{item.dishName}</strong>
                                    <span style={{ color: '#666', marginLeft: '10px' }}>
                                        x{item.quantity}
                                    </span>
                                </div>
                                <div style={{ fontWeight: 'bold' }}>
                                    {(item.price * item.quantity).toFixed(2)} €
                                </div>
                            </div>
                        ))}

                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            paddingTop: '15px',
                            fontSize: '20px',
                            fontWeight: 'bold'
                        }}>
                            <span>TOTAL</span>
                            <span style={{ color: '#FF6B35' }}>
                                {cart.totalAmount.toFixed(2)} €
                            </span>
                        </div>
                    </div>

                    <button
                        onClick={handleCreateOrder}
                        disabled={loading}
                        style={{
                            width: '100%',
                            padding: '15px',
                            backgroundColor: loading ? '#ccc' : '#4CAF50',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            fontSize: '18px',
                            fontWeight: 'bold',
                            cursor: loading ? 'wait' : 'pointer'
                        }}
                    >
                        {loading ? ' Création...' : '✓ Continuer vers la livraison'}
                    </button>
                </div>
            )}

            {/* Step 2: Sélection du créneau */}
            {currentStep === 2 && (
                <div>
                    <h2 style={{ marginBottom: '20px', fontSize: '24px' }}>
                        Choisissez votre créneau de livraison
                    </h2>

                    <DeliverySlotSelector
                        selectedSlot={selectedSlot}
                        onSelect={setSelectedSlot}
                    />

                    <div style={{
                        display: 'flex',
                        gap: '10px',
                        marginTop: '30px'
                    }}>
                        <button
                            onClick={() => setCurrentStep(1)}
                            style={{
                                flex: 1,
                                padding: '15px',
                                backgroundColor: '#ddd',
                                color: '#333',
                                border: 'none',
                                borderRadius: '5px',
                                fontSize: '16px',
                                cursor: 'pointer'
                            }}
                        >
                            ← Retour
                        </button>

                        <button
                            onClick={handleSelectDeliverySlot}
                            disabled={!selectedSlot || loading}
                            style={{
                                flex: 2,
                                padding: '15px',
                                backgroundColor: (!selectedSlot || loading) ? '#ccc' : '#4CAF50',
                                color: 'white',
                                border: 'none',
                                borderRadius: '5px',
                                fontSize: '18px',
                                fontWeight: 'bold',
                                cursor: (!selectedSlot || loading) ? 'not-allowed' : 'pointer'
                            }}
                        >
                            {loading ? '⏳ Validation...' : '✓ Continuer vers le paiement'}
                        </button>
                    </div>
                </div>
            )}

            {/* Step 3: Paiement */}
            {currentStep === 3 && (
                <div>
                    <h2 style={{ marginBottom: '20px', fontSize: '24px' }}>
                         Paiement
                    </h2>

                    <PaymentForm
                        amount={cart.totalAmount}
                        onPaymentSubmit={handlePayment}
                        loading={loading}
                    />

                    <button
                        onClick={() => setCurrentStep(2)}
                        disabled={loading}
                        style={{
                            width: '100%',
                            padding: '12px',
                            backgroundColor: '#ddd',
                            color: '#333',
                            border: 'none',
                            borderRadius: '5px',
                            fontSize: '16px',
                            cursor: loading ? 'not-allowed' : 'pointer',
                            marginTop: '20px'
                        }}
                    >
                        ← Modifier le créneau
                    </button>
                </div>
            )}
        </div>
    );
};

export default CheckoutPage;
