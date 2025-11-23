import React, {useState} from 'react';
import './FilterBar.css';

export default function FilterBar({onFilter}) {
    const [filters, setFilters] = useState({
        cuisineType: '',
        restaurantType: '',
        isOpen: ''
    });

    function handleChange(e) {
        const {name, value} = e.target;
        setFilters(prev => ({...prev, [name]: value}));
    }

    function handleSubmit(e) {
        e.preventDefault(); // empêche le refresh de page
        onFilter(filters);  // envoie les filtres au parent
    }

    return (
        <form className="filter-bar" onSubmit={handleSubmit}>
            <div>
                <p>Cuisine type:</p>
                <select name="cuisineType" value={filters.cuisineType} onClick={handleChange} onChange={handleChange}>
                    <option value="">All</option>
                    <option value="NONE">NONE</option>
                    <option value="VEGETARIAN">VEGETARIAN</option>
                    <option value="VEGAN">VEGAN</option>
                    <option value="GLUTEN_FREE">GLUTEN FREE</option>
                    <option value="LACTOSE_FREE">LACTOSE FREE</option>
                    <option value="HALAL">HALAL</option>
                    <option value="KOSHER">KOSHER</option>
                </select>
            </div>
            <div>
                <p>Restaurant type:</p>
                <select name="restaurantType" value={filters.restaurantType} onClick={handleChange}
                        onChange={handleChange}>
                    <option value="">All</option>
                    <option value="CROUS">CROUS</option>
                    <option value="RESTAURANT">RESTAURANT</option>
                    <option value="FOOD_TRUCK">FOOD TRUCK</option>
                    <option value="CAFETERIA">CAFETERIA</option>
                    <option value="FAST_FOOD">FAST FOOD</option>
                </select>
            </div>
            <div>
                <p>Status:</p>
                <select name="isOpen" value={filters.isOpen} onClick={handleChange} onChange={handleChange}>
                    <option value="">All</option>
                    <option value="true">Open</option>
                    <option value="false">Closed</option>
                </select>
            </div>

            <button type="submit">Filter</button>
        </form>
    );
}
