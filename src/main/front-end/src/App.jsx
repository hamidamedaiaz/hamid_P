import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { UserProvider } from "./context/UserContext";
import { CartProvider } from "./context/CartContext";
import Navbar from "./components/common/Navbar";
import Footer from "./components/common/Footer";
import HomePage from "./pages/HomePage";
import RestaurantsPage from "./pages/restaurant/user-interface/RestaurantsPage.jsx";
import RestaurantDetailPage from "./pages/RestaurantDetailPage.jsx";
import CartPage from "./pages/CartPage.jsx";
import CheckoutPage from "./pages/CheckoutPage.jsx";
import OrderConfirmationPage from "./pages/OrderConfirmationPage.jsx";
import ManagerRestaurantListPage from "./pages/restaurant/manager-interface/ManagerRestaurantListPage.jsx";
import ManagerRestaurantDetailPage from "./pages/restaurant/manager-interface/ManagerRestaurantDetailPage.jsx";
import ManagerAddRestaurantPage from "./pages/restaurant/manager-interface/ManagerAddRestaurantPage.jsx";

export default function App() {
    return (
        <UserProvider>
            <CartProvider>
                <Router>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        minHeight: '100vh'
                    }}>
                        <Navbar />

                        <main style={{ flex: 1, backgroundColor: '#F5F5F5' }}>
                            <Routes>
                                <Route path="/" element={<HomePage />} />

                                {/* User interface - Restaurant & Order Flow */}
                                <Route path="/restaurants" element={<RestaurantsPage />} />
                                <Route path="/restaurants/:id" element={<RestaurantDetailPage />} />
                                <Route path="/cart" element={<CartPage />} />
                                <Route path="/checkout" element={<CheckoutPage />} />
                                <Route path="/order-confirmation" element={<OrderConfirmationPage />} />

                                {/* Manager interface */}
                                <Route path="/manager/restaurants" element={<ManagerRestaurantListPage />} />
                                <Route path="/manager/restaurants/:id" element={<ManagerRestaurantDetailPage />} />
                                <Route path="/manager/restaurants/add" element={<ManagerAddRestaurantPage />} />
                            </Routes>
                        </main>

                        <Footer />
                    </div>
                </Router>
            </CartProvider>
        </UserProvider>
    );
}
