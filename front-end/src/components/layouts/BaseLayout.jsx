import React from 'react';
import PropTypes from 'prop-types';
import Navigation from '../common/Navigation.jsx';
import Footer from '../common/Footer.jsx';

/**
 * Layout de base sans barre d'actions
 * Pour la page d'accueil et autres pages publiques
 */
export default function BaseLayout({ children, title = '' }) {
    return (
        <div className="min-h-screen flex flex-col bg-neutral-50">
            <Navigation title={title} />
            {children}
            <Footer />
        </div>
    );
}

BaseLayout.propTypes = {
    children: PropTypes.node.isRequired,
    title: PropTypes.string,
};

