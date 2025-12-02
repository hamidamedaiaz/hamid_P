import React from 'react';
import PropTypes from 'prop-types';

/**
 * Composant de chargement professionnel avec style Apple
 */
export default function LoadingSpinner({ 
    size = 'medium', 
    fullScreen = true, 
    message = 'Chargement...',
    variant = 'primary'
}) {
    const sizeClasses = {
        small: 'w-6 h-6 border-2',
        medium: 'w-10 h-10 border-3',
        large: 'w-14 h-14 border-4'
    };

    const variantClasses = {
        primary: 'border-primary-500 border-t-transparent',
        white: 'border-white border-t-transparent',
        neutral: 'border-neutral-400 border-t-transparent'
    };

    const spinner = (
        <div className="flex flex-col items-center justify-center gap-4 animate-fade-in">
            <div 
                className={`
                    ${sizeClasses[size]} 
                    ${variantClasses[variant]}
                    rounded-full animate-spin
                `}
                role="status"
                aria-label="Chargement en cours"
            />
            {message && (
                <p className={`
                    text-sm font-medium
                    ${variant === 'white' ? 'text-white' : 'text-neutral-700'}
                    animate-pulse
                `}>
                    {message}
                </p>
            )}
        </div>
    );

    if (fullScreen) {
        return (
            <div className="fixed inset-0 bg-white/95 backdrop-blur-sm flex items-center justify-center z-50">
                {spinner}
            </div>
        );
    }

    return (
        <div className="flex items-center justify-center p-8">
            {spinner}
        </div>
    );
}

LoadingSpinner.propTypes = {
    size: PropTypes.oneOf(['small', 'medium', 'large']),
    fullScreen: PropTypes.bool,
    message: PropTypes.string,
    variant: PropTypes.oneOf(['primary', 'white', 'neutral'])
};

