import React from "react";
import { updateDishInMenu } from "../../../services/restaurantService.js";
import {DishType} from "../../../utils/types.js";
import './Form.css';

export default function UpdateDishForm({ restaurantId, dish }) {

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const updatedDish = {
            name: formData.get("name"),
            description: formData.get("description"),
            category: formData.get("category"),
            price: Number.parseFloat(formData.get("price")),
            dietTypes: formData.getAll("dietTypes"),
            available: formData.get("available") === "on"
        };

        updateDishInMenu(restaurantId, dish.id, updatedDish)
            .then(res => {
                console.log("Dish updated", res);
            })
            .catch(err => console.error(err));
    };

    return (
        <form onSubmit={handleSubmit}>
            <h3>Update Dish</h3>

            <label>Name:
                <input type="text" name="name" defaultValue={dish.name} required/>
            </label>

            <label>Description:
                <textarea name="description" defaultValue={dish.description} required/>
            </label>

            <label>Category:
                <select name="category" defaultValue={dish.category} required>
                    <option value="STARTER">Starter</option>
                    <option value="MAIN_COURSE">Main Course</option>
                    <option value="DESSERT">Dessert</option>
                    <option value="BEVERAGE">Beverage</option>
                </select>
            </label>

            <label>Price (€):
                <input type="number" name="price" step="0.01" defaultValue={dish.price} required />
            </label>

            <label>Diet Types:
                <select name="dietTypes" defaultValue={dish.dietTypes} multiple>
                    <option value="NONE">NONE</option>
                    <option value="VEGETARIAN">Vegetarian</option>
                    <option value="VEGAN">Vegan</option>
                    <option value="GLUTEN_FREE">Gluten Free</option>
                    <option value="LACTOSE_FREE">Lactose Free</option>
                    <option value="HALAL">Halal</option>
                    <option value="KOSHER">Kosher</option>
                </select>
            </label>

            <label>Available:
                <input type="checkbox" name="available" defaultValue={dish.available}/>
            </label>

            <button type="submit">Save</button>
        </form>
    );
}

UpdateDishForm.propTypes = {
    restaurantId: String,
    dish: DishType,
}