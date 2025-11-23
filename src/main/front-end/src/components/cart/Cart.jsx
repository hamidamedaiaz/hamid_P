import { useCart } from "../../context/CartContext.jsx";
import CartItem from "./CartItem.jsx";
import CartSummary from "./CartSummary.jsx";
import "./Cart.css";

function Cart() {
    const { items } = useCart();

    if (items.length === 0) {
        return <h2>Votre panier est vide</h2>;
    }

    return (
        <div className="cart">
            <h1>Votre Panier</h1>

            <div className="cart-items">
                {items.map(item => (
                    <CartItem key={item.dishId} item={item} />
                ))}
            </div>

            <CartSummary items={items} />

            <button
                className="checkout-btn"
                onClick={() => (window.location.href = "/checkout")}
            >
                Passer la commande
            </button>
        </div>
    );
}

export default Cart;
