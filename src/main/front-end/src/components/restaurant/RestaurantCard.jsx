import React from 'react';
import { useNavigate } from 'react-router-dom';
import { RestaurantType } from '../../utils/types';
import PropTypes from "prop-types";
import { deleteRestaurant } from "../../services/restaurantService.js";
import './RestaurantCard.css';

export default function RestaurantCard({ restaurant, isManager }) {
    const navigate = useNavigate();

    const handleNavigate = () => {
        if (isManager) {
            navigate(`/manager/restaurants/${restaurant.id}`);
        } else {
            navigate(`/restaurants/${restaurant.id}`);
        }
    };

    const handleDelete = (e) => {
        e.stopPropagation();
        deleteRestaurant(restaurant.id)
            .then(() => console.log("Restaurant deleted: "+ restaurant.name));
    };

    return (
        <div className="restaurant-card">
            {/* Carte principale cliquable */}
            <button type="button" className="card-content" onClick={handleNavigate}>
                <h3 className="restaurant-name">{restaurant.name}</h3>
                <p className="restaurant-address">{restaurant.address}</p>
                <p className="restaurant-status">
                    Open: <span className={restaurant.isOpen ? "open" : "closed"}>
                        {restaurant.isOpen ? "Yes" : "No"}
                    </span>
                </p>
            </button>

            {/* Actions pour manager */}
            {isManager && (
                <div className="manager-actions">
                    <button type="button" className="delete-restaurant-button" onClick={handleDelete}>
                        Delete this restaurant
                    </button>
                </div>
            )}
        </div>
    );
}

RestaurantCard.propTypes = {
    restaurant: RestaurantType.isRequired,
    isManager: PropTypes.bool,
};
