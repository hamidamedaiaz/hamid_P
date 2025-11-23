import React from 'react';
import {DishType} from '../../utils/types.js';
import './DishCard.css';

export default function DishCard({dish}) {
    return (
        <div className="dish-card">
            <h4 className="dish-name">{dish.name}</h4>
            <p className="dish-description">{dish.description}</p>
            <div className="dish-info">
                <span className="dish-category">{dish.category}</span>
                <span className="dish-price">{dish.price} €</span>
            </div>
            {dish.dietTypes && dish.dietTypes.length > 0 && (
                <p className="dish-diet">Diet: {dish.dietTypes.join(', ')}</p>
            )}
            {!dish.available && <span className="dish-unavailable">Unavailable</span>}
        </div>
    );
}

DishCard.propTypes = {
    dish: DishType.isRequired,
};