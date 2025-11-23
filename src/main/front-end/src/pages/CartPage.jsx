import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import cartService from '../services/cartService';
import CartItem from '../components/cart/CartItem';
import Loading from '../components/common/Loading';

const CartPage = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { userId } = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        loadCart();
    }, [userId]);

    const loadCart = async () => {
        setLoading(true);
        setError(null);
        try {
            const cartData = await cartService.getCart(userId);
            setCart(cartData);
        } catch (err) {
            // Si le panier n'existe pas (404), on le traite comme un panier vide
            if (err.response?.status === 404) {
                setCart({ items: [] });
            } else {
                console.error('Erreur lors du chargement du panier:', err);
                setError('Impossible de charger votre panier');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateItem = async (dishId, newQuantity) => {
        try {
            await cartService.updateCartItem(userId, dishId, newQuantity);
            await loadCart();
        } catch (error) {
            console.error('Erreur lors de la mise à jour:', error);
            throw error;
        }
    };

    const handleRemoveItem = async (dishId) => {
        try {
            await cartService.removeDishFromCart(userId, dishId);
            await loadCart();
        } catch (error) {
            console.error('Erreur lors de la suppression:', error);
            throw error;
        }
    };

    const handleClearCart = async () => {
        if (!confirm('Êtes-vous sûr de vouloir vider votre panier ?')) {
            return;
        }

        try {
            await cartService.clearCart(userId);
            await loadCart();
        } catch (error) {
            console.error('Erreur lors du vidage du panier:', error);
            alert('Erreur lors du vidage du panier');
        }
    };

    const handleCheckout = () => {
        navigate('/checkout');
    };

    if (loading) {
        return <Loading message="Chargement de votre panier..." />;
    }

    if (error) {
        return (
            <div style={{ textAlign: 'center', padding: '50px', color: 'red' }}>
                <p> {error}</p>
                <button onClick={loadCart} style={{ marginTop: '20px', padding: '10px 20px' }}>
                    Réessayer
                </button>
            </div>
        );
    }

    const isEmpty = !cart || !cart.items || cart.items.length === 0;

    // Calculer le total si non fourni par le backend
    const totalAmount = cart?.totalAmount ?? cart?.items?.reduce((sum, item) => {
        return sum + (item.price || 0) * (item.quantity || 0);
    }, 0) ?? 0;

    const totalItems = cart?.totalItems ?? cart?.items?.reduce((sum, item) => {
        return sum + (item.quantity || 0);
    }, 0) ?? 0;

    return (
        <div style={{ maxWidth: '900px', margin: '0 auto', padding: '20px' }}>
            <h1 style={{ marginBottom: '30px', fontSize: '32px' }}>🛒 Mon Panier</h1>

            {isEmpty ? (
                <div style={{
                    textAlign: 'center',
                    padding: '60px 20px',
                    backgroundColor: '#f9f9f9',
                    borderRadius: '10px'
                }}>
                    <p style={{ fontSize: '24px', marginBottom: '20px' }}></p>
                    <p style={{ fontSize: '18px', color: '#666', marginBottom: '20px' }}>
                        Votre panier est vide
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
                        Découvrir les restaurants
                    </button>
                </div>
            ) : (
                <>
                    <div style={{ marginBottom: '20px' }}>
                        {cart.items.map((item) => (
                            <CartItem
                                key={item.dishId}
                                item={item}
                                onUpdate={handleUpdateItem}
                                onRemove={handleRemoveItem}
                            />
                        ))}
                    </div>

                    <div style={{
                        backgroundColor: '#f9f9f9',
                        padding: '20px',
                        borderRadius: '8px',
                        marginTop: '30px'
                    }}>
                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            marginBottom: '20px'
                        }}>
                            <div>
                                <p style={{ fontSize: '18px', margin: '0 0 5px 0' }}>
                                    Total des articles: <strong>{totalItems}</strong>
                                </p>
                                <p style={{ fontSize: '24px', fontWeight: 'bold', margin: 0 }}>
                                    Total: <span style={{ color: '#FF6B35' }}>
                                        {totalAmount.toFixed(2)} €
                                    </span>
                                </p>
                            </div>
                        </div>

                        <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
                            <button
                                onClick={handleClearCart}
                                style={{
                                    padding: '12px 24px',
                                    backgroundColor: '#ff4444',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    fontSize: '16px',
                                    cursor: 'pointer'
                                }}
                            >
                                 Vider le panier
                            </button>

                            <button
                                onClick={handleCheckout}
                                style={{
                                    padding: '12px 30px',
                                    backgroundColor: '#4CAF50',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    fontSize: '18px',
                                    fontWeight: 'bold',
                                    cursor: 'pointer'
                                }}
                            >
                                ✓ Passer la commande
                            </button>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default CartPage;
