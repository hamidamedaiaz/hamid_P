import React from "react";

import RestaurantList from "../../../components/restaurant/RestaurantList.jsx";
import Header from "../../../components/common/Header.jsx";
import Footer from "../../../components/common/Footer.jsx"
import "../../../styles/Global.css"
import AddRestaurantForm from "../../../components/restaurant/form/AddRestaurantForm.jsx";

export default function ManagerAddRestaurantPage() {

    return (
        <div>
            <Header title="Add Restaurante Page"/>
            <AddRestaurantForm/>
            <RestaurantList isManager={true}/>
            <Footer/>
        </div>
    );
}
