import React from "react";
import { createRestaurant } from "../../../services/restaurantService.js";
import './Form.css';

export default function AddRestaurantForm() {

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const restaurant = {
            name: formData.get("name"),
            address: formData.get("address"),
            restaurantType: formData.get("restaurantType"),
            schedule: {
                openingTime: formData.get("opening"),
                closingTime: formData.get("closing")
            }
        };

        createRestaurant(restaurant)
            .then(res => {
                console.log("Restaurant created:", res);
                e.target.reset();
            })
            .catch(err => console.error("Error:", err));
    };

    return (
        <form onSubmit={handleSubmit}>
            <h3>Add Restaurant</h3>

            <label>
                <p>Name:</p>
                <input type="text" name="name" required />
            </label>

            <label>
                <p>Address:</p>
                <input type="text" name="address" required />
            </label>

            <label>
                <p>Restaurant Type:</p>
                <select name="restaurantType" defaultValue="RESTAURANT" required>
                    <option value="RESTAURANT">Restaurant</option>
                    <option value="CAFETERIA">Cafeteria</option>
                    <option value="FOOD_TRUCK">Food Truck</option>
                    <option value="FAST_FOOD">Fast Food</option>
                    <option value="CROUS">Crous</option>
                </select>
            </label>

            <label>
                <p>Opening Time:</p>
                <input type="time" name="opening" required />
            </label>

            <label>
                <p>Closing Time:</p>
                <input type="time" name="closing" required />
            </label>

            <button type="submit">Add Restaurant</button>
        </form>
    );
}
