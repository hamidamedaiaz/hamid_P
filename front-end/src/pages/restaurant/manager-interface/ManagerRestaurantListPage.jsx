import React from "react";
import Header from "../../../components/common/Header.jsx";
import Footer from "../../../components/common/Footer.jsx";
import RestaurantList from "../../../components/restaurant/RestaurantList.jsx";
import "./ManagerRestaurantListPage.css";
import {useNavigate} from "react-router-dom";

export default function ManagerRestaurantListPage() {
    const navigate = useNavigate();

    return (
        <div>
            <Header title="Restaurants Page - Managers Interface"/>
            <RestaurantList isManager={true}/>

            <div style={{padding: "16px", textAlign: "center"}}>
                <button onClick={() => navigate(`/manager/restaurants/add`)}>
                    Add a new restaurant!
                </button>
            </div>

            <Footer/>
        </div>
    );
}
