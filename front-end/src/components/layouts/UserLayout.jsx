import React from 'react';
import PropTypes from 'prop-types';
import Navigation from '../common/Navigation.jsx';
import UserActionBar from '../common/UserActionBar.jsx';
import Footer from '../common/Footer.jsx';

/**
 * Layout principal pour l'interface utilisateur
 * Inclut la navigation, la barre d'actions utilisateur, et le footer
 */
export default function UserLayout({ 
    children, 
    showBackButton = false, 
    backTo = '/',
    title = '',
    showActionBar = true 
}) {
    return (
        <div className="min-h-screen flex flex-col bg-neutral-50">
            <Navigation 
                showBackButton={showBackButton} 
                backTo={backTo}
                title={title}
            />
            {showActionBar && <UserActionBar />}
            {children}
            <Footer />
        </div>
    );
}

UserLayout.propTypes = {
    children: PropTypes.node.isRequired,
    showBackButton: PropTypes.bool,
    backTo: PropTypes.string,
    title: PropTypes.string,
    showActionBar: PropTypes.bool,
};

