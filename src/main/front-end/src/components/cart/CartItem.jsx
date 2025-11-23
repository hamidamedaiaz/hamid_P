import { useState } from 'react';

const CartItem = ({ item, onUpdate, onRemove }) => {
    const [quantity, setQuantity] = useState(item.quantity);
    const [loading, setLoading] = useState(false);

    const handleQuantityChange = async (newQuantity) => {
        if (newQuantity < 1) return;

        setLoading(true);
        setQuantity(newQuantity);

        try {
            await onUpdate(item.dishId, newQuantity);
        } catch (error) {
            console.error('Erreur lors de la mise à jour:', error);
            setQuantity(item.quantity); // Restaurer l'ancienne quantité
            alert('Erreur lors de la mise à jour de la quantité');
        } finally {
            setLoading(false);
        }
    };

    const handleRemove = async () => {
        if (!confirm(`Supprimer "${item.dishName}" du panier ?`)) {
            return;
        }

        setLoading(true);
        try {
            await onRemove(item.dishId);
        } catch (error) {
            console.error('Erreur lors de la suppression:', error);
            alert('Erreur lors de la suppression');
            setLoading(false);
        }
    };

    const subtotal = item.price * quantity;

    return (
        <div className="cart-item" style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '15px',
            border: '1px solid #ddd',
            borderRadius: '8px',
            marginBottom: '10px',
            backgroundColor: loading ? '#f5f5f5' : 'white'
        }}>
            <div style={{ flex: 2 }}>
                <h3 style={{ margin: '0 0 5px 0', fontSize: '18px' }}>{item.dishName}</h3>
                <p style={{ margin: 0, color: '#666' }}>{item.price.toFixed(2)} €</p>
            </div>

            <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                flex: 1,
                justifyContent: 'center'
            }}>
                <button
                    onClick={() => handleQuantityChange(quantity - 1)}
                    disabled={loading || quantity <= 1}
                    style={{
                        width: '30px',
                        height: '30px',
                        borderRadius: '50%',
                        border: '1px solid #ddd',
                        backgroundColor: 'white',
                        cursor: 'pointer',
                        fontSize: '18px'
                    }}
                >
                    -
                </button>
                <span style={{
                    minWidth: '30px',
                    textAlign: 'center',
                    fontWeight: 'bold',
                    fontSize: '16px'
                }}>
                    {quantity}
                </span>
                <button
                    onClick={() => handleQuantityChange(quantity + 1)}
                    disabled={loading}
                    style={{
                        width: '30px',
                        height: '30px',
                        borderRadius: '50%',
                        border: '1px solid #ddd',
                        backgroundColor: 'white',
                        cursor: 'pointer',
                        fontSize: '18px'
                    }}
                >
                    +
                </button>
            </div>

            <div style={{
                flex: 1,
                textAlign: 'right',
                fontWeight: 'bold',
                fontSize: '18px'
            }}>
                {subtotal.toFixed(2)} €
            </div>

            <button
                onClick={handleRemove}
                disabled={loading}
                style={{
                    marginLeft: '15px',
                    padding: '8px 15px',
                    backgroundColor: '#ff4444',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer',
                    fontSize: '14px'
                }}
            >
                 Supprimer
            </button>
        </div>
    );
};

export default CartItem;

