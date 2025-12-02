import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import { CartProvider } from "./context/CartContext.jsx";
import HomePage from "./pages/HomePage.jsx";
import RestaurantsPage from "./pages/restaurant/user-interface/RestaurantsPage.jsx";
import RestaurantDetailPage from "./pages/restaurant/user-interface/RestaurantDetailPage.jsx";
import CartPage from "./pages/CartPage.jsx";
import CheckoutPage from "./pages/CheckoutPage.jsx";
import OrderConfirmationPage from "./pages/OrderConfirmationPage.jsx";
import ManagerRestaurantListPage from "./pages/restaurant/manager-interface/ManagerRestaurantListPage.jsx";
import ManagerRestaurantDetailPage from "./pages/restaurant/manager-interface/ManagerRestaurantDetailPage.jsx";
import ManagerAddRestaurantPage from "./pages/restaurant/manager-interface/ManagerAddRestaurantPage.jsx";

export default function App() {
    return (
        <CartProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<HomePage/>}/>

                    {/* User interface */}
                    <Route path="/restaurants" element={<RestaurantsPage/>}/>
                    <Route path="/restaurants/:id" element={<RestaurantDetailPage/>}/>
                    <Route path="/cart" element={<CartPage/>}/>
                    <Route path="/checkout" element={<CheckoutPage/>}/>
                    <Route path="/order-confirmation/:orderId" element={<OrderConfirmationPage/>}/>

                    {/* Manager interface */}
                    <Route path="/manager/restaurants" element={<ManagerRestaurantListPage/>}/>
                    <Route path="/manager/restaurants/:id" element={<ManagerRestaurantDetailPage/>}/>
                    <Route path="/manager/restaurants/add" element={<ManagerAddRestaurantPage/>}/>
                </Routes>
            </Router>
        </CartProvider>
    );
}
