import React from 'react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../context/CartContext.jsx';
import { ShoppingCartIcon } from '@heroicons/react/24/outline';

/**
 * Barre d'actions utilisateur (panier, etc.)
 * À afficher uniquement dans l'interface utilisateur
 */
export default function UserActionBar() {
    const navigate = useNavigate();
    const { cartItemsCount } = useCart();

    return (
        <div className="bg-white border-b border-neutral-200 shadow-sm">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-end h-12">
                    <button
                        onClick={() => navigate('/cart')}
                        className="relative inline-flex items-center gap-2 px-3 py-2
                                 text-neutral-700 hover:text-primary-600 hover:bg-neutral-50
                                 rounded-apple transition-all duration-200"
                        aria-label="Panier"
                    >
                        <ShoppingCartIcon className="w-6 h-6" />
                        <span className="hidden sm:inline text-sm font-medium">Panier</span>
                        {cartItemsCount > 0 && (
                            <span className="absolute -top-1 -right-1 flex items-center justify-center
                                           w-5 h-5 text-xs font-semibold text-white bg-danger
                                           rounded-full animate-scale-in">
                                {cartItemsCount}
                            </span>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}

