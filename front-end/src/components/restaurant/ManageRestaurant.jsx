import React, {useEffect, useState} from "react";
import {fetchRestaurantDetails, removeDishFromMenu} from "../../services/restaurantService.js";

import AddDishForm from "./form/AddDishForm.jsx";
import UpdateDishForm from "./form/UpdateDishForm.jsx";
import UpdateRestaurantForm from "./form/UpdateRestaurantForm.jsx";
import LoadingSpinner from "../common/LoadingSpinner.jsx";
import RestaurantOrders from "./RestaurantOrders.jsx";
import DeliverySlotManager from "./DeliverySlotManager.jsx";

import "./ManageRestaurant.css";

export default function ManageRestaurant({restaurantId}) {
    const [restaurant, setRestaurant] = useState(null);
    const [editingDish, setEditingDish] = useState(null);

    useEffect(() => {
        loadRestaurant();
    }, []);

    const loadRestaurant = () => {
        fetchRestaurantDetails(restaurantId)
            .then(res => setRestaurant(res))
            .catch(err => console.error(err));
    };

    const handleDeleteDish = (dishId) => {
        removeDishFromMenu(restaurantId, dishId)
            .then(() => loadRestaurant())
            .catch(err => console.error(err));
    };

    if (!restaurant) return <LoadingSpinner/>;

    return (
        <div className="manage-restaurant">
            <h2 className="page-title">Manage Restaurant: {restaurant.name}</h2>

            {/* Update Restaurant */}
            <section className="section update-restaurant">
                <h3 className="section-title">Update Information</h3>
                <UpdateRestaurantForm restaurant={restaurant} onUpdated={loadRestaurant}/>
            </section>

            <hr className="divider"/>

            {/* Delivery Slots Management */}
            <section className="section delivery-slots-section">
                <DeliverySlotManager restaurantId={restaurantId} />
            </section>

            <hr className="divider"/>

            {/* Restaurant Orders */}
            <section className="section orders-section">
                <RestaurantOrders restaurantId={restaurantId} />
            </section>

            <hr className="divider"/>

            <section className="section menu-section">
                <h3 className="section-title">Menu</h3>

                <div className="menu-layout">
                    <div className="dishes-list">
                        <h4>Dishes</h4>
                        {restaurant.menu?.length === 0 && <p>No dishes yet.</p>}

                        {restaurant.menu?.map((dish) => (
                            <div key={dish.id} className="dish-item">
                                <div className="dish-info">
                                    <strong>{dish.name}</strong> — {dish.price}€
                                    <p>{dish.description}</p>
                                    <p><em>{dish.category}</em></p>
                                </div>
                                <div className="dish-actions">
                                    <button
                                        className="btn edit"
                                        onClick={() => setEditingDish(dish)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className="btn delete"
                                        onClick={() => handleDeleteDish(dish.id)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="add-dish">
                        <h4>Add a Dish</h4>
                        <AddDishForm restaurantId={restaurantId}/>
                    </div>
                </div>
            </section>

            {editingDish && (
                <section className="section edit-dish">
                    <h3 className="section-title">Edit Dish: {editingDish.name}</h3>
                    <UpdateDishForm restaurantId={restaurantId} dish={editingDish}/>
                    <button
                        className="btn close"
                        onClick={() => setEditingDish(null)}
                    >
                        Close
                    </button>
                </section>
            )}
        </div>
    );
}

ManageRestaurant.propTypes = {
    restaurantId: String,
};
