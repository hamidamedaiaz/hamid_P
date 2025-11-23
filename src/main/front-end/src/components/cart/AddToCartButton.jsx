import { useState } from 'react';
import cartService from '../../services/cartService';
import { useUser } from '../../context/UserContext';

const AddToCartButton = ({ dishId, dishName, onSuccess, onError }) => {
    const { userId } = useUser();
    const [added, setAdded] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleAddToCart = async () => {
        try {
            setLoading(true);
            console.log('Ajout au panier - userId:', userId, 'dishId:', dishId);
            const response = await cartService.addDishToCart(userId, dishId, 1);
            console.log(' Réponse du backend:', response);

            // Vérifier si la réponse existe et si success n'est pas false
            if (response && response.success !== false && response.cartId) {
                setAdded(true);
                setTimeout(() => setAdded(false), 2000);

                if (onSuccess) {
                    onSuccess(response);
                }
            } else {
                console.error(' Ajout échoué - success:', response?.success, 'cartId:', response?.cartId);
                throw new Error('Échec de l\'ajout au panier - Vérifiez que le service Order & Payment peut accéder aux plats');
            }
        } catch (error) {
            console.error(' Erreur lors de l\'ajout au panier:', error);
            console.error(' Détails:', error.response?.data || error.message);
            if (onError) {
                onError(error);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <button
            onClick={handleAddToCart}
            disabled={loading || added}
            className={`add-to-cart-btn ${added ? 'success' : ''}`}
            style={{
                padding: '10px 20px',
                backgroundColor: added ? '#4CAF50' : '#FF6B35',
                color: 'white',
                border: 'none',
                borderRadius: '5px',
                cursor: loading ? 'wait' : 'pointer',
                transition: 'all 0.3s ease',
                fontSize: '14px',
                fontWeight: 'bold'
            }}
        >
            {loading ? ' Ajout...' : added ? '✓ Ajouté !' : ' Ajouter au panier'}
        </button>
    );
};

export default AddToCartButton;
