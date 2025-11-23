import React from "react";
import { updateRestaurant } from "../../../services/restaurantService.js";
import "./Form.css";
import { RestaurantType } from "../../../utils/types.js";
import PropTypes from "prop-types";

export default function UpdateRestaurantForm({ restaurant, onUpdated }) {

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const updated = {
            name: formData.get("name"),
            address: formData.get("address"),
            restaurantType: formData.get("restaurantType"),
            openingTime: formData.get("opening"),
            closingTime: formData.get("closing"),
        };

        updateRestaurant(restaurant.id, updated)
            .then(res => {
                console.log("Restaurant updated:", res);
                if (onUpdated) onUpdated();
            })
            .catch(err => console.error("Error updating restaurant:", err));
    };

    return (
        <form onSubmit={handleSubmit}>
            <h3>Update Restaurant</h3>

            <label>
                <p>Name:</p>
                <input type="text" name="name" defaultValue={restaurant.name} required/>
            </label>

            <label>
                <p>Address:</p>
                <input type="text" name="address" defaultValue={restaurant.address} required/>
            </label>

            <label>
                <p>Restaurant Type:</p>
                <select name="restaurantType" defaultValue={restaurant.restaurantType}>
                    <option value="RESTAURANT">Restaurant</option>
                    <option value="CAFETERIA">Cafeteria</option>
                    <option value="FOOD_TRUCK">Food Truck</option>
                    <option value="FAST_FOOD">Fast Food</option>
                    <option value="CROUS">Crous</option>
                </select>
            </label>

            <label>
                <p>Opening Time:</p>
                <input type="time" name="opening" defaultValue={restaurant.openingTime} required/>
            </label>

            <label>
                <p>Closing Time:</p>
                <input type="time" name="closing" defaultValue={restaurant.closingTime} required/>
            </label>

            <button type="submit">Save</button>
        </form>
    );
}

UpdateRestaurantForm.propTypes = {
    restaurant: RestaurantType.isRequired,
    onUpdated: PropTypes.func,
};
