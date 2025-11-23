import React from 'react';
import './Form.css';
import {addDishToMenu} from "../../../services/restaurantService.js";

export default function AddDishForm({restaurantId}) {

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const newDish = {
            name: formData.get('name'),
            description: formData.get('description'),
            category: formData.get('category'),
            price: parseFloat(formData.get('price')),
            dietTypes: formData.getAll('dietTypes'),
            available: formData.get('available') === 'on',
        };

        addDishToMenu(restaurantId, newDish)
            .then(addedDish => {
                console.log("Dish added:", addedDish);
                e.target.reset();
            })
            .catch(err => console.error("Error adding dish:", err));
    };

    return (
        <form className="add-dish-form" onSubmit={handleSubmit}>
            <h3>Add New Dish</h3>

            <label>
                <p>Name:</p>
                <input type="text" name="name" required/>
            </label>

            <label>
                <p>Description:</p>
                <textarea name="description" required/>
            </label>

            <label>
                <p>Category:</p>
                <select name="category" required>
                    <option value="STARTER">Starter</option>
                    <option value="MAIN_COURSE">Main Course</option>
                    <option value="DESSERT">Dessert</option>
                    <option value="BEVERAGE">Beverage</option>
                </select>
            </label>

            <label>
                <p>Price (€):</p>
                <input type="number" name="price" step="0.01" required/>
            </label>

            <label>
                <p>Diet Types (hold Ctrl/Cmd to select multiple):</p>
                <select name="dietTypes" multiple>
                    <option value="NONE">NONE</option>
                    <option value="VEGETARIAN">Vegetarian</option>
                    <option value="VEGAN">Vegan</option>
                    <option value="GLUTEN_FREE">Gluten Free</option>
                    <option value="LACTOSE_FREE">Lactose Free</option>
                    <option value="HALAL">Halal</option>
                    <option value="KOSHER">Kosher</option>
                </select>
            </label>

            <label>
                <p>Available:</p>
                <input type="checkbox" name="available" defaultChecked/>
            </label>

            <button type="submit">Add Dish</button>
        </form>
    );
}
