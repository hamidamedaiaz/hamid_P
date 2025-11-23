import React from "react";
import { useParams } from "react-router-dom";
import Header from "../../../components/common/Header.jsx";
import ManageRestaurant from "../../../components/restaurant/ManageRestaurant.jsx";

import "../../../styles/Global.css"

export default function ManagerRestaurantDetailPage() {
    const { id } = useParams();

    return (
        <div>
            <Header title="Restaurant Manager" />
            <ManageRestaurant restaurantId={id} />

        </div>
    );
}
