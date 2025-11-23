import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import orderService from '../services/orderService';
import Loading from '../components/common/Loading';

const OrderConfirmationPage = () => {
    const [searchParams] = useSearchParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const orderId = searchParams.get('orderId');

    useEffect(() => {
        if (!orderId) {
            setError('Aucun numéro de commande fourni');
            setLoading(false);
            return;
        }

        loadOrder();
    }, [orderId]);

    const loadOrder = async () => {
        setLoading(true);
        try {
            const orderData = await orderService.getOrder(orderId);
            setOrder(orderData);
        } catch (err) {
            console.error('Erreur lors du chargement de la commande:', err);
            setError('Impossible de charger les détails de votre commande');
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (isoString) => {
        if (!isoString) return 'Non défini';
        const date = new Date(isoString);
        return date.toLocaleDateString('fr-FR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusColor = (status) => {
        const colors = {
            'PENDING': '#FFA726',
            'CONFIRMED': '#4CAF50',
            'PREPARING': '#2196F3',
            'READY': '#FF6B35',
            'DELIVERED': '#4CAF50',
            'CANCELLED': '#f44336'
        };
        return colors[status] || '#666';
    };

    const getStatusLabel = (status) => {
        const labels = {
            'PENDING': 'En attente',
            'CONFIRMED': 'Confirmée',
            'PREPARING': 'En préparation',
            'READY': 'Prête',
            'DELIVERED': 'Livrée',
            'CANCELLED': 'Annulée'
        };
        return labels[status] || status;
    };

    if (loading) {
        return <Loading message="Chargement de votre commande..." />;
    }

    if (error || !order) {
        return (
            <div style={{ textAlign: 'center', padding: '50px' }}>
                <p style={{ fontSize: '20px', color: 'red', marginBottom: '20px' }}>
                    ❌ {error || 'Commande introuvable'}
                </p>
                <button
                    onClick={() => navigate('/restaurants')}
                    style={{
                        padding: '12px 24px',
                        backgroundColor: '#FF6B35',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        cursor: 'pointer'
                    }}
                >
                    Retour aux restaurants
                </button>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
            {/* Success Banner */}
            <div style={{
                backgroundColor: '#4CAF50',
                color: 'white',
                padding: '30px',
                borderRadius: '10px',
                textAlign: 'center',
                marginBottom: '30px'
            }}>
                <div style={{ fontSize: '48px', marginBottom: '10px' }}>✓</div>
                <h1 style={{ margin: '0 0 10px 0', fontSize: '28px' }}>
                    Commande confirmée !
                </h1>
                <p style={{ margin: 0, fontSize: '16px', opacity: 0.9 }}>
                    Votre commande a été enregistrée avec succès
                </p>
            </div>

            {/* Order Details */}
            <div style={{
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '10px',
                padding: '25px',
                marginBottom: '20px'
            }}>
                <h2 style={{ marginTop: 0, marginBottom: '20px', fontSize: '22px' }}>
                    📦 Détails de la commande
                </h2>

                <div style={{ marginBottom: '15px' }}>
                    <strong>Numéro de commande:</strong>
                    <div style={{
                        fontFamily: 'monospace',
                        backgroundColor: '#f5f5f5',
                        padding: '10px',
                        borderRadius: '5px',
                        marginTop: '5px',
                        fontSize: '14px'
                    }}>
                        {order.id}
                    </div>
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <strong>Statut:</strong>
                    <div style={{
                        display: 'inline-block',
                        marginLeft: '10px',
                        padding: '5px 15px',
                        backgroundColor: getStatusColor(order.status),
                        color: 'white',
                        borderRadius: '20px',
                        fontSize: '14px',
                        fontWeight: 'bold'
                    }}>
                        {getStatusLabel(order.status)}
                    </div>
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <strong>Date de commande:</strong>
                    <div style={{ marginTop: '5px', color: '#666' }}>
                        {formatDate(order.createdAt)}
                    </div>
                </div>

                {order.deliverySlot && (
                    <div style={{ marginBottom: '15px' }}>
                        <strong>Créneau de livraison:</strong>
                        <div style={{
                            marginTop: '5px',
                            padding: '10px',
                            backgroundColor: '#FFF5F2',
                            border: '1px solid #FF6B35',
                            borderRadius: '5px',
                            color: '#FF6B35',
                            fontWeight: 'bold'
                        }}>
                            {formatDate(order.deliverySlot)}
                        </div>
                    </div>
                )}
            </div>

            {/* Order Items */}
            <div style={{
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '10px',
                padding: '25px',
                marginBottom: '20px'
            }}>
                <h2 style={{ marginTop: 0, marginBottom: '20px', fontSize: '22px' }}>
                     Articles commandés
                </h2>

                {order.items && order.items.length > 0 ? (
                    <>
                        {order.items.map((item, index) => (
                            <div key={index} style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                padding: '15px 0',
                                borderBottom: index < order.items.length - 1 ? '1px solid #eee' : 'none'
                            }}>
                                <div>
                                    <div style={{ fontWeight: 'bold', marginBottom: '5px' }}>
                                        {item.dishName}
                                    </div>
                                    <div style={{ color: '#666', fontSize: '14px' }}>
                                        Quantité: {item.quantity} × {item.price.toFixed(2)} €
                                    </div>
                                </div>
                                <div style={{ fontWeight: 'bold', fontSize: '18px' }}>
                                    {(item.quantity * item.price).toFixed(2)} €
                                </div>
                            </div>
                        ))}

                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            paddingTop: '20px',
                            marginTop: '20px',
                            borderTop: '2px solid #ddd',
                            fontSize: '22px',
                            fontWeight: 'bold'
                        }}>
                            <span>TOTAL</span>
                            <span style={{ color: '#FF6B35' }}>
                                {order.totalAmount.toFixed(2)} €
                            </span>
                        </div>
                    </>
                ) : (
                    <p style={{ color: '#666', textAlign: 'center' }}>
                        Aucun article dans cette commande
                    </p>
                )}
            </div>

            {/* Payment Info */}
            {order.paymentStatus && (
                <div style={{
                    backgroundColor: '#e8f5e9',
                    border: '1px solid #4CAF50',
                    borderRadius: '10px',
                    padding: '20px',
                    marginBottom: '20px'
                }}>
                    <h3 style={{ marginTop: 0, marginBottom: '10px', fontSize: '18px' }}>Paiement
                    </h3>
                    <div style={{ color: '#4CAF50', fontWeight: 'bold' }}>
                        ✓ Paiement {order.paymentStatus === 'PAID' ? 'effectué' : order.paymentStatus}
                    </div>
                </div>
            )}

            {/* Action Buttons */}
            <div style={{
                display: 'flex',
                gap: '10px',
                marginTop: '30px'
            }}>
                <button
                    onClick={() => navigate('/restaurants')}
                    style={{
                        flex: 1,
                        padding: '15px',
                        backgroundColor: '#FF6B35',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        fontWeight: 'bold',
                        cursor: 'pointer'
                    }}
                >
                    Commander à nouveau
                </button>

                <button
                    onClick={() => window.print()}
                    style={{
                        flex: 1,
                        padding: '15px',
                        backgroundColor: '#2196F3',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        fontSize: '16px',
                        fontWeight: 'bold',
                        cursor: 'pointer'
                    }}
                >
                     Imprimer
                </button>
            </div>

            {/* Info Message */}
            <div style={{
                marginTop: '30px',
                padding: '15px',
                backgroundColor: '#e3f2fd',
                borderRadius: '5px',
                fontSize: '14px',
                color: '#1976d2'
            }}>
                 Vous recevrez une notification lorsque votre commande sera prête pour la livraison.
            </div>
        </div>
    );
};

export default OrderConfirmationPage;
