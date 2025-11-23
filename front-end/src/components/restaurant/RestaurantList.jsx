import React, {useEffect, useState} from 'react';
import RestaurantCard from './RestaurantCard.jsx';
import FilterBar from './FilterBar.jsx';
import './RestaurantList.css';
import PropTypes from "prop-types";
import {fetchRestaurants, filterRestaurants} from "../../services/restaurantService.js";

export default function RestaurantList({isManager = false}) {
    const [restaurants, setRestaurants] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    async function loadRestaurants(filters = {}) {
        try {
            setLoading(true);
            const data = Object.keys(filters).length
                ? await filterRestaurants(filters)
                : await fetchRestaurants();
            setRestaurants(data);
            console.log(data);
        } catch (err) {
            setError(err.message);
            console.log(err);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadRestaurants();
    }, []);

    if (loading) return <p>Loading restaurants...</p>;
    if (error) return <p>Error: {error}</p>;

    function handleFilter(filters) {
        console.log('Applying filters:', filters);
        loadRestaurants(filters);
    }

    return (
        <div className="restaurant-list">
            <FilterBar onFilter={handleFilter}/>
            <div className="restaurant-grid">
                {restaurants.map((r) => (
                    <RestaurantCard key={r.id} restaurant={r} isManager={isManager}/>
                ))}
            </div>
        </div>
    );
}

RestaurantList.propTypes = {
    isManager: PropTypes.bool,
};