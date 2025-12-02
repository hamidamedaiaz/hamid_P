import React from 'react';
import PropTypes from 'prop-types';

/**
 * Section de contenu avec style cohérent
 * Utilisé pour grouper les éléments liés
 */
export default function ContentSection({ 
    children, 
    title, 
    subtitle,
    className = '',
    noPadding = false 
}) {
    return (
        <section className={`bg-white rounded-apple-lg border border-neutral-200 shadow-apple ${noPadding ? '' : 'p-6'} ${className}`}>
            {(title || subtitle) && (
                <div className={`${noPadding ? 'p-6 pb-4' : 'mb-4'}`}>
                    {title && (
                        <h2 className="text-xl font-bold text-neutral-900 mb-1">
                            {title}
                        </h2>
                    )}
                    {subtitle && (
                        <p className="text-sm text-neutral-600">
                            {subtitle}
                        </p>
                    )}
                </div>
            )}
            <div className={noPadding ? 'p-6 pt-0' : ''}>
                {children}
            </div>
        </section>
    );
}

ContentSection.propTypes = {
    children: PropTypes.node.isRequired,
    title: PropTypes.string,
    subtitle: PropTypes.string,
    className: PropTypes.string,
    noPadding: PropTypes.bool,
};

