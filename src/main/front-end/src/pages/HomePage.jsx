import React from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/common/Header";

import "./HomePage.css";

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <div className="home-page" style={{ textAlign: "center" }}>
            <Header title="Sophia Tech Eats" />

            <div className="interface-choice">
                <h2>Choose your interface</h2>

                <div className="buttons-container">
                    <button
                        style={{ padding: "20px 40px", fontSize: "20px" }}
                        onClick={() => navigate("/restaurants")}
                    >
                        Users Interface
                    </button>

                    <button
                        style={{ padding: "20px 40px", fontSize: "20px" }}
                        onClick={() => navigate("/manager/restaurants")}
                    >
                        Restaurants Interface
                    </button>
                </div>
            </div>
        </div>
    );
}
