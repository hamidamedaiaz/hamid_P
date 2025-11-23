import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Menu from '../../../components/restaurant/Menu.jsx';
import Header from '../../../components/common/Header.jsx';
import { fetchRestaurantDetails } from "../../../services/restaurantService.js";
import LoadingSpinner from "../../../components/common/LoadingSpinner.jsx";

import "./RestaurantDetailPage.css"

export default function RestaurantDetailPage() {
    const { id } = useParams();
    const [resto, setResto] = useState(null);

    useEffect(() => {
        if (id) {
            fetchRestaurantDetails(id).then(data => setResto(data));
        }
    }, [id]);

    if (!resto) return <LoadingSpinner />;

    return (
        <div className="menu-page">
            <Header title="Restaurant Details" />

            <div className="menu-content">
                <Menu restaurant={resto} />
            </div>
        </div>
    );
}
