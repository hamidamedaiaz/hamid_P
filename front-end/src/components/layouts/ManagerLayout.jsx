import React from 'react';
import PropTypes from 'prop-types';
import Navigation from '../common/Navigation.jsx';
import Footer from '../common/Footer.jsx';

/**
 * Layout pour l'interface restaurant/manager
 * Pas de barre d'actions utilisateur
 */
export default function ManagerLayout({ 
    children, 
    showBackButton = false, 
    backTo = '/',
    title = '' 
}) {
    return (
        <div className="min-h-screen flex flex-col bg-neutral-50">
            <Navigation 
                showBackButton={showBackButton} 
                backTo={backTo}
                title={title}
            />
            {children}
            <Footer />
        </div>
    );
}

ManagerLayout.propTypes = {
    children: PropTypes.node.isRequired,
    showBackButton: PropTypes.bool,
    backTo: PropTypes.string,
    title: PropTypes.string,
};

