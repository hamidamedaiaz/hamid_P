import React from 'react';
import PropTypes from 'prop-types';

/**
 * Composant de titre de page
 * Style cohérent pour tous les titres principaux
 */
export default function PageTitle({ children, className = '' }) {
    return (
        <h1 className={`text-3xl font-bold text-neutral-900 mb-6 ${className}`}>
            {children}
        </h1>
    );
}

PageTitle.propTypes = {
    children: PropTypes.node.isRequired,
    className: PropTypes.string,
};

