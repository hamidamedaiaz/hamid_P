import './Footer.css';

const Footer = () => {
    return (
        <footer className="footer">
            <div className="footer-container">
                <div className="footer-section">
                    <h3> SophiaTech Eats</h3>
                    <p>Commandez vos repas préférés sur le campus</p>
                </div>

                <div className="footer-section">
                    <h4>Liens rapides</h4>
                    <ul>
                        <li><a href="/restaurants">Restaurants</a></li>
                        <li><a href="/cart">Mon panier</a></li>
                    </ul>
                </div>

                <div className="footer-section">
                    <h4>Contact</h4>
                    <p>teamP</p>
                    <p> </p>
                </div>

                <div className="footer-section">
                    <h4>macha macha </h4>
                    <p></p>
                    <p></p>
                </div>
            </div>

            <div className="footer-bottom">
                <p> 2025 SophiaTech Eats </p>
            </div>
        </footer>
    );
};

export default Footer;

