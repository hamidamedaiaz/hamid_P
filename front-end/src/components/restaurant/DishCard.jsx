import React, { useState } from 'react';
import {DishType} from '../../utils/types.js';
import { useCart } from '../../context/CartContext.jsx';
import './DishCard.css';

export default function DishCard({dish}) {
    const { addToCart } = useCart();
    const [adding, setAdding] = useState(false);
    const [added, setAdded] = useState(false);

    async function handleAddToCart() {
        if (!dish.available) return;

        setAdding(true);
        const success = await addToCart(dish.id, 1);
        setAdding(false);

        if (success) {
            setAdded(true);
            setTimeout(() => setAdded(false), 2000);
        }
    }

    return (
        <div className={`dish-card ${!dish.available ? 'unavailable' : ''}`}>
            <h4 className="dish-name">{dish.name}</h4>
            <p className="dish-description">{dish.description}</p>
            <div className="dish-info">
                <span className="dish-category">{dish.category}</span>
                <span className="dish-price">{dish.price?.toFixed(2)} €</span>
            </div>
            {dish.dietTypes && dish.dietTypes.length > 0 && (
                <p className="dish-diet">🌿 {dish.dietTypes.join(', ')}</p>
            )}
            {!dish.available ? (
                <span className="dish-unavailable">Non disponible</span>
            ) : (
                <button
                    onClick={handleAddToCart}
                    className={`btn-add-to-cart ${added ? 'added' : ''}`}
                    disabled={adding}
                >
                    {adding ? 'Ajout...' : added ? '✓ Ajouté' : '+ Ajouter au panier'}
                </button>
            )}
        </div>
    );
}

DishCard.propTypes = {
    dish: DishType.isRequired,
};