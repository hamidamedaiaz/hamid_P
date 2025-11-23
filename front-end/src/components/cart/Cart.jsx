import React, {useContext} from 'react';
import './Cart.css';

export default function Cart() {
    const {cartItems, removeFromCart, clearCart} = useContext();

    const totalPrice = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

    if (cartItems.length === 0) {
        return <p>Your cart is empty.</p>;
    }

    return (
        <div className="cart">
            <h3>Your Cart</h3>
            {cartItems.map(item => (
                <div key={item.id} className="cart-item">
                    <span>{item.name} x {item.quantity}</span>
                    <span>
                        {item.price * item.quantity} €
                        <button
                            onClick={() => removeFromCart(item.id)}
                            style={{marginLeft: '8px', cursor: 'pointer'}}
                        >
                            Remove
                        </button>
                    </span>
                </div>
            ))}
            <div className="cart-summary">
                Total: {totalPrice.toFixed(2)} €
            </div>
            <button onClick={clearCart} style={{marginTop: '12px', cursor: 'pointer'}}>
                Clear Cart
            </button>
        </div>
    );
}
