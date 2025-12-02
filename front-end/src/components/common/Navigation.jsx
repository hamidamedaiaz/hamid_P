import React from 'react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';

/**
 * Composant de navigation principal
 * Contient uniquement logo/retour à gauche et titre au centre
 */
export default function Navigation({ showBackButton = false, backTo = '/', title = '' }) {
    const navigate = useNavigate();

    return (
        <nav className="sticky top-0 z-40 w-full bg-white border-b border-neutral-200 shadow-apple">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    {/* Left section - Logo ou Back button */}
                    <div className="flex items-center gap-3 min-w-[100px]">
                        {showBackButton ? (
                            <button
                                onClick={() => navigate(backTo)}
                                className="inline-flex items-center gap-2 px-3 py-2 text-sm font-medium
                                         text-neutral-700 hover:text-primary-600 hover:bg-neutral-50
                                         rounded-apple transition-all duration-200"
                                aria-label="Retour"
                            >
                                <ArrowLeftIcon className="w-5 h-5" />
                                <span className="hidden sm:inline">Retour</span>
                            </button>
                        ) : (
                            <img
                                src="/src/assets/STE-icon.png"
                                alt="Sophia Tech Eats"
                                className="h-10 w-10 cursor-pointer hover:opacity-80 transition-opacity"
                                onClick={() => navigate('/')}
                            />
                        )}
                    </div>

                    {/* Center section - Title */}
                    {title && (
                        <h1 className="text-lg sm:text-xl font-semibold text-neutral-900 text-center flex-1">
                            {title}
                        </h1>
                    )}

                    {/* Right section - Placeholder for consistency */}
                    <div className="min-w-[100px]"></div>
                </div>
            </div>
        </nav>
    );
}

Navigation.propTypes = {
    showBackButton: PropTypes.bool,
    backTo: PropTypes.string,
    title: PropTypes.string,
};
