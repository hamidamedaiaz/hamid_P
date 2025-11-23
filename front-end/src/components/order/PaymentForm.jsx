import React, {useState} from 'react';
import PropTypes from 'prop-types';
import './PaymentForm.css';

export default function PaymentForm({onPay}) {
    const [cardNumber, setCardNumber] = useState('');
    const [expiry, setExpiry] = useState('');
    const [cvv, setCvv] = useState('');

    function handleSubmit(e) {
        e.preventDefault();
        onPay({cardNumber, expiry, cvv});
    }

    return (
        <form className="payment-form" onSubmit={handleSubmit}>
            <input
                type="text"
                placeholder="Card Number"
                value={cardNumber}
                onChange={e => setCardNumber(e.target.value)}
                required
            />
            <input
                type="text"
                placeholder="MM/YY"
                value={expiry}
                onChange={e => setExpiry(e.target.value)}
                required
            />
            <input
                type="text"
                placeholder="CVV"
                value={cvv}
                onChange={e => setCvv(e.target.value)}
                required
            />
            <button type="submit">Pay</button>
        </form>
    );
}

PaymentForm.propTypes = {
    onPay: PropTypes.func.isRequired
};
