import React from 'react';
import PropTypes from 'prop-types';

/**
 * Composant de conteneur de page principal
 * Fournit une structure cohérente pour toutes les pages
 */
export default function PageContainer({ children, className = '' }) {
    return (
        <main className={`flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8 ${className}`}>
            {children}
        </main>
    );
}

PageContainer.propTypes = {
    children: PropTypes.node.isRequired,
    className: PropTypes.string,
};

