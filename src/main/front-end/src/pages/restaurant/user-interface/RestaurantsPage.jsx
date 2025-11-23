import React from 'react';
import RestaurantList from '../../../components/restaurant/RestaurantList.jsx';
import "./RestaurantsPage.css";
import Header from "../../../components/common/Header.jsx";

export default function RestaurantsPage() {
    return (
        <div className="restaurants-page">
            <Header title="Restaurants Page - User Interface" />

            <main className="main-content">
                <RestaurantList />
            </main>
        </div>
    );
}
