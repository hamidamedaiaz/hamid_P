import React, {useEffect, useState} from 'react';
import {RestaurantType} from '../../utils/types.js';
import DishCard from './DishCard.jsx';
import {fetchMenu} from '../../services/restaurantService.js';
import './Menu.css';

export default function Menu({restaurant}) {
    const [menu, setMenu] = useState([]);

    useEffect(() => {
        if (restaurant) loadMenu();
    }, [restaurant]);

    async function loadMenu() {
        try {
            const data = await fetchMenu(restaurant.id);
            setMenu(data);
        } catch (err) {
            console.error(err.message);
        }
    }

    if (!restaurant) return <p>Select a restaurant to view its menu.</p>;

    return (
        <div className="menu">
            <h2>{restaurant.name} Menu</h2>
            <div className="menu-grid">
                {menu.map(d => (
                    <DishCard key={d.id} dish={d}/>
                ))}
            </div>
        </div>
    );
}

Menu.propTypes = {
    restaurant: RestaurantType,
};
