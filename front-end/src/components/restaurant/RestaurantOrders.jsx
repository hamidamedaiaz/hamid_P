import React, { useEffect, useState } from "react";
import { fetchRestaurantOrders } from "../../services/restaurantService.js";
import "./RestaurantOrders.css";

export default function RestaurantOrders({ restaurantId }) {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadOrders();
    }, [restaurantId]);

    const loadOrders = async () => {
        try {
            setLoading(true);
            const response = await fetchRestaurantOrders(restaurantId);
            setOrders(response.orders || []);
            setError(null);
        } catch (err) {
            console.error("Error loading orders:", err);
            setError("Failed to load orders");
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "N/A";
        const date = new Date(dateString);
        return date.toLocaleString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusBadge = (status) => {
        const statusClasses = {
            PAID: "status-paid",
            CONFIRMED: "status-confirmed",
            PREPARING: "status-preparing",
            DELIVERED: "status-delivered"
        };
        return statusClasses[status] || "status-default";
    };

    const getStatusLabel = (status) => {
        const labels = {
            PAID: "Payée",
            CONFIRMED: "Confirmée",
            PREPARING: "En préparation",
            DELIVERED: "Livrée"
        };
        return labels[status] || status;
    };

    if (loading) {
        return <div className="loading">Chargement des commandes...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    if (orders.length === 0) {
        return <div className="no-orders">Aucune commande pour le moment</div>;
    }

    return (
        <div className="restaurant-orders">
            <h3>Commandes du restaurant ({orders.length})</h3>

            <div className="orders-list">
                {orders.map((order) => (
                    <div key={order.orderId} className="order-card">
                        <div className="order-header">
                            <div className="order-info">
                                <span className="order-id">Commande #{order.orderId.substring(0, 8)}</span>
                                <span className={`order-status ${getStatusBadge(order.status)}`}>
                                    {getStatusLabel(order.status)}
                                </span>
                            </div>
                            <div className="order-customer">
                                <strong>Client:</strong> {order.customerName}
                            </div>
                        </div>

                        <div className="order-details">
                            <div className="order-dates">
                                <div>
                                    <strong>Commandé le:</strong> {formatDate(order.orderDateTime)}
                                </div>
                                <div>
                                    <strong>Livraison prévue:</strong> {formatDate(order.deliveryTime)}
                                </div>
                            </div>

                            <div className="order-items">
                                <strong>Articles:</strong>
                                <ul>
                                    {order.items.map((item, index) => (
                                        <li key={index}>
                                            {item.quantity}x {item.name} - {item.totalPrice}€
                                        </li>
                                    ))}
                                </ul>
                            </div>

                            <div className="order-total">
                                <strong>Total:</strong> {order.totalAmount}€
                                {order.isPaid && <span className="paid-badge">✓ Payé</span>}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

