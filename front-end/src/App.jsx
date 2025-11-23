import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import RestaurantsPage from "./pages/restaurant/user-interface/RestaurantsPage.jsx";
import RestaurantDetailPage from "./pages/restaurant/user-interface/RestaurantDetailPage.jsx";
import ManagerRestaurantListPage from "./pages/restaurant/manager-interface/ManagerRestaurantListPage.jsx";
import ManagerRestaurantDetailPage from "./pages/restaurant/manager-interface/ManagerRestaurantDetailPage.jsx";
import ManagerAddRestaurantPage from "./pages/restaurant/manager-interface/ManagerAddRestaurantPage.jsx";

export default function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<HomePage/>}/>

                {/* User interface */}
                <Route path="/restaurants" element={<RestaurantsPage/>}/>
                <Route path="/restaurants/:id" element={<RestaurantDetailPage/>}/>

                {/* Manager interface */}
                <Route path="/manager/restaurants" element={<ManagerRestaurantListPage/>}/>
                <Route path="/manager/restaurants/:id" element={<ManagerRestaurantDetailPage/>}/>
                <Route path="/manager/restaurants/add" element={<ManagerAddRestaurantPage/>}/>
            </Routes>
        </Router>
    );
}
