import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import restaurantService from '../services/restaurantService';
import AddToCartButton from '../components/cart/AddToCartButton';
import Loading from '../components/common/Loading';

const RestaurantDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [restaurant, setRestaurant] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [notification, setNotification] = useState('');

    useEffect(() => {
        loadRestaurant();
    }, [id]);

    const loadRestaurant = async () => {
        setLoading(true);
        try {
            const data = await restaurantService.getRestaurantById(id);
            // Charger le menu séparément
            const menu = await restaurantService.fetchMenu(id);
            setRestaurant({
                ...data,
                menu: menu || []
            });
        } catch (err) {
            console.error('Erreur lors du chargement du restaurant:', err);
            setError('Impossible de charger les détails du restaurant');
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCartSuccess = (response) => {
        setNotification(`✓ Plat ajouté au panier ! (${response.totalItems} articles)`);
        setTimeout(() => setNotification(''), 3000);
    };

    const handleAddToCartError = (error) => {
        setNotification(' Erreur lors de l\'ajout au panier');
        setTimeout(() => setNotification(''), 3000);
    };

    if (loading) {
        return <Loading message="Chargement du restaurant..." />;
    }

    if (error || !restaurant) {
        return (
            <div style={{ textAlign: 'center', padding: '50px' }}>
                <p style={{ color: 'red', fontSize: '18px' }}> {error}</p>
                <button
                    onClick={() => navigate('/restaurants')}
                    style={{
                        marginTop: '20px',
                        padding: '12px 24px',
                        backgroundColor: '#FF6B35',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    Retour aux restaurants
                </button>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
            {/* Notification */}
            {notification && (
                <div style={{
                    position: 'fixed',
                    top: '20px',
                    right: '20px',
                    padding: '15px 25px',
                    backgroundColor: notification.includes('✓') ? '#4CAF50' : '#f44336',
                    color: 'white',
                    borderRadius: '5px',
                    boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                    zIndex: 1000,
                    animation: 'slideIn 0.3s ease'
                }}>
                    {notification}
                </div>
            )}

            {/* Header */}
            <div style={{ marginBottom: '30px' }}>
                <button
                    onClick={() => navigate('/restaurants')}
                    style={{
                        padding: '8px 16px',
                        backgroundColor: '#f5f5f5',
                        border: '1px solid #ddd',
                        borderRadius: '5px',
                        cursor: 'pointer',
                        marginBottom: '20px'
                    }}
                >
                    ← Retour aux restaurants
                </button>

                <h1 style={{ fontSize: '36px', marginBottom: '10px' }}>
                    {restaurant.name}
                </h1>

                {restaurant.description && (
                    <p style={{ fontSize: '16px', color: '#666', marginBottom: '15px' }}>
                        {restaurant.description}
                    </p>
                )}

                <div style={{ display: 'flex', gap: '15px', fontSize: '14px', color: '#666' }}>
                    {restaurant.cuisine && (
                        <span>{restaurant.cuisine}</span>
                    )}
                    {restaurant.address && (
                        <span> {restaurant.address}</span>
                    )}
                    {restaurant.phone && (
                        <span>{restaurant.phone}</span>
                    )}
                </div>
            </div>

            {/* Menu */}
            <div>
                <h2 style={{ fontSize: '28px', marginBottom: '20px' }}>
                     Menu
                </h2>

                {restaurant.menu && restaurant.menu.length > 0 ? (
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
                        gap: '20px'
                    }}>
                        {restaurant.menu.map((dish) => (
                            <div
                                key={dish.id}
                                style={{
                                    border: '1px solid #ddd',
                                    borderRadius: '10px',
                                    padding: '20px',
                                    backgroundColor: 'white',
                                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                                    transition: 'transform 0.2s ease, box-shadow 0.2s ease'
                                }}
                                onMouseEnter={(e) => {
                                    e.currentTarget.style.transform = 'translateY(-5px)';
                                    e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)';
                                }}
                                onMouseLeave={(e) => {
                                    e.currentTarget.style.transform = 'translateY(0)';
                                    e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
                                }}
                            >
                                <h3 style={{
                                    fontSize: '20px',
                                    marginBottom: '10px',
                                    color: '#333'
                                }}>
                                    {dish.name}
                                </h3>

                                {dish.description && (
                                    <p style={{
                                        fontSize: '14px',
                                        color: '#666',
                                        marginBottom: '15px',
                                        minHeight: '40px'
                                    }}>
                                        {dish.description}
                                    </p>
                                )}

                                {dish.category && (
                                    <div style={{
                                        display: 'inline-block',
                                        padding: '4px 12px',
                                        backgroundColor: '#e3f2fd',
                                        color: '#1976d2',
                                        borderRadius: '15px',
                                        fontSize: '12px',
                                        marginBottom: '10px'
                                    }}>
                                        {dish.category}
                                    </div>
                                )}

                                {dish.tags && dish.tags.length > 0 && (
                                    <div style={{ marginBottom: '10px' }}>
                                        {dish.tags.map((tag, index) => (
                                            <span
                                                key={index}
                                                style={{
                                                    display: 'inline-block',
                                                    padding: '2px 8px',
                                                    backgroundColor: '#f5f5f5',
                                                    borderRadius: '10px',
                                                    fontSize: '11px',
                                                    marginRight: '5px',
                                                    marginBottom: '5px',
                                                    color: '#666'
                                                }}
                                            >
                                                {tag}
                                            </span>
                                        ))}
                                    </div>
                                )}

                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginTop: '15px',
                                    paddingTop: '15px',
                                    borderTop: '1px solid #eee'
                                }}>
                                    <span style={{
                                        fontSize: '24px',
                                        fontWeight: 'bold',
                                        color: '#FF6B35'
                                    }}>
                                        {dish.price.toFixed(2)} €
                                    </span>

                                    <AddToCartButton
                                        dishId={dish.id}
                                        onSuccess={handleAddToCartSuccess}
                                        onError={handleAddToCartError}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div style={{
                        textAlign: 'center',
                        padding: '40px',
                        backgroundColor: '#f9f9f9',
                        borderRadius: '10px'
                    }}>
                        <p style={{ color: '#666', fontSize: '16px' }}>
                            Aucun plat disponible pour le moment
                        </p>
                    </div>
                )}
            </div>

            {/* Floating Cart Button */}
            <button
                onClick={() => navigate('/cart')}
                style={{
                    position: 'fixed',
                    bottom: '30px',
                    right: '30px',
                    width: '60px',
                    height: '60px',
                    borderRadius: '50%',
                    backgroundColor: '#FF6B35',
                    color: 'white',
                    border: 'none',
                    fontSize: '24px',
                    cursor: 'pointer',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.3)',
                    transition: 'transform 0.2s ease',
                    zIndex: 999
                }}
                onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
                onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
            >

            </button>
        </div>
    );
};

export default RestaurantDetailPage;
