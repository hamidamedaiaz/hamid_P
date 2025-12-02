import React from 'react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext.jsx';
import { ArrowLeftIcon, ShoppingCartIcon } from '@heroicons/react/24/outline';

export default function Header({ title, showCart = true, showBackButton = false, backTo = '/' }) {
    const navigate = useNavigate();
    const { cartItemsCount } = useCart();

    return (
        <header className="sticky top-0 z-40 w-full bg-white border-b border-neutral-200 shadow-apple">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    {/* Left section - Logo/Back button + Title */}
                    <div className="flex items-center gap-3">
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
                        <h1 className="text-lg sm:text-xl font-semibold text-neutral-900">
                            {title}
                        </h1>
                    </div>

                    {/* Right section - Cart button */}
                    <div className="flex items-center">
                        {showCart && (
                            <button
                                onClick={() => navigate('/cart')}
                                className="relative inline-flex items-center gap-2 px-3 py-2
                                         text-neutral-700 hover:text-primary-600 hover:bg-neutral-50
                                         rounded-apple transition-all duration-200"
                                aria-label="Panier"
                            >
                                <ShoppingCartIcon className="w-6 h-6" />
                                {cartItemsCount > 0 && (
                                    <span className="absolute -top-1 -right-1 flex items-center justify-center
                                                   w-5 h-5 text-xs font-semibold text-white bg-danger
                                                   rounded-full animate-scale-in">
                                        {cartItemsCount}
                                    </span>
                                )}
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </header>
    );
}

Header.propTypes = {
    title: PropTypes.string.isRequired,
    showCart: PropTypes.bool,
    showBackButton: PropTypes.bool,
    backTo: PropTypes.string,
};
