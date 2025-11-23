import React from 'react';
import RestaurantList from '../../../components/restaurant/RestaurantList.jsx';
import "./RestaurantsPage.css";
import Header from "../../../components/common/Header.jsx";
import Footer from "../../../components/common/Footer.jsx";

export default function RestaurantsPage() {
    return (
        <div className="restaurants-page">
            <Header title="Restaurants Page - User Interface"/>

            <main className="main-content">
                <RestaurantList/>
            </main>

            <Footer/>
        </div>
    );
}
