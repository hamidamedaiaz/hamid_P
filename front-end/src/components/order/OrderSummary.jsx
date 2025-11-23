import React from 'react';
import PropTypes from 'prop-types';
import {DishType} from '../../utils/types.js';
import './OrderSummary.css';

export default function OrderSummary({items}) {
    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

    return (
        <div className="order-summary">
            <h3>Order Summary</h3>
            <ul>
                {items.map(item => (
                    <li key={item.id}>
                        {item.name} x {item.quantity} = {(item.price * item.quantity).toFixed(2)} €
                    </li>
                ))}
            </ul>
            <p>Total: {total.toFixed(2)} €</p>
        </div>
    );
}

OrderSummary.propTypes = {
    items: PropTypes.arrayOf(DishType).isRequired
};
