import { Link } from 'react-router-dom';
import { useUser } from '../../context/UserContext';
import { useCart } from '../../context/CartContext';
import './Navbar.css';

const Navbar = () => {
    const { userName } = useUser();
    const { getCartItemCount } = useCart();
    const itemCount = getCartItemCount();

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo">
                    <span className="logo-icon"></span>
                    <span className="logo-text">SophiaTech Eats</span>
                </Link>

                <ul className="navbar-menu">
                    <li className="navbar-item">
                        <Link to="/restaurants" className="navbar-link">
                            Restaurants
                        </Link>
                    </li>
                    <li className="navbar-item">
                        <Link to="/cart" className="navbar-link navbar-link-cart" style={{ position: 'relative' }}>
                            🛒 Panier
                            {itemCount > 0 && (
                                <span style={{
                                    position: 'absolute',
                                    top: '-5px',
                                    right: '-10px',
                                    backgroundColor: '#FF6B35',
                                    color: 'white',
                                    borderRadius: '50%',
                                    width: '20px',
                                    height: '20px',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontSize: '12px',
                                    fontWeight: 'bold'
                                }}>
                                    {itemCount}
                                </span>
                            )}
                        </Link>
                    </li>
                    <li className="navbar-item">
                        <div className="navbar-user">
                             {userName}
                        </div>
                    </li>
                </ul>
            </div>
        </nav>
    );
};

export default Navbar;
