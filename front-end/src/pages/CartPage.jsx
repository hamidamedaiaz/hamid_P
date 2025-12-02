import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { fetchCart } from '../services/cartService.js';
import UserLayout from '../components/layouts/UserLayout.jsx';
import PageContainer from '../components/common/PageContainer.jsx';
import PageTitle from '../components/common/PageTitle.jsx';
import ContentSection from '../components/common/ContentSection.jsx';
import LoadingSpinner from '../components/common/LoadingSpinner.jsx';
import TimeSlotSelector from '../components/order/TimeSlotSelector.jsx';
import {
    ShoppingBagIcon,
    TrashIcon,
    MinusIcon,
    PlusIcon,
    XMarkIcon
} from '@heroicons/react/24/outline';

export default function CartPage() {
    const navigate = useNavigate();
    const { loading, removeItem, updateQuantity, emptyCart } = useCart();
    const [detailedCart, setDetailedCart] = useState(null);
    const [slotSelected, setSlotSelected] = useState(false);

    useEffect(() => {
        loadDetailedCart();
    }, []);

    async function loadDetailedCart() {
        try {
            const data = await fetchCart();
            setDetailedCart(data);
        } catch (err) {
            console.error('Error loading cart:', err);
        }
    }

    // ...existing handlers...
    async function handleRemoveItem(dishId) {
        if (confirm('Retirer cet article du panier ?')) {
            await removeItem(dishId);
            await loadDetailedCart();
        }
    }

    async function handleUpdateQuantity(dishId, newQuantity) {
        if (newQuantity < 1) {
            await handleRemoveItem(dishId);
        } else {
            await updateQuantity(dishId, newQuantity);
            await loadDetailedCart();
        }
    }

    async function handleClearCart() {
        if (confirm('Vider complètement le panier ?')) {
            await emptyCart();
            setDetailedCart(null);
        }
    }

    function handleProceedToCheckout() {
        if (!slotSelected) {
            alert('Veuillez sélectionner un créneau de livraison');
            return;
        }
        navigate('/checkout');
    }

    if (loading) return <LoadingSpinner message="Chargement du panier..." />;

    // Empty cart state
    if (!detailedCart || !detailedCart.items || detailedCart.items.length === 0) {
        return (
            <UserLayout showBackButton={true} backTo="/restaurants" showActionBar={false}>
                <div className="flex-1 flex items-center justify-center px-4">
                    <div className="text-center max-w-md">
                        <div className="inline-flex items-center justify-center w-20 h-20 bg-neutral-100
                                      rounded-full mb-6">
                            <ShoppingBagIcon className="w-10 h-10 text-neutral-400" />
                        </div>
                        <h2 className="text-2xl font-bold text-neutral-900 mb-2">
                            Votre panier est vide
                        </h2>
                        <p className="text-neutral-600 mb-6">
                            Ajoutez des plats pour commencer votre commande
                        </p>
                        <button
                            onClick={() => navigate('/restaurants')}
                            className="px-6 py-3 bg-primary-500 text-white font-semibold rounded-apple
                                     hover:bg-primary-600 transition-all duration-200 shadow-apple
                                     active:scale-95"
                        >
                            Voir les restaurants
                        </button>
                    </div>
                </div>
            </UserLayout>
        );
    }

    return (
        <UserLayout showBackButton={true} backTo="/restaurants" showActionBar={false}>
            <PageContainer>
                <PageTitle>Mon Panier</PageTitle>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Cart Items Section */}
                    <div className="lg:col-span-2 space-y-4">
                        <div className="flex items-center justify-between">
                            <h2 className="text-2xl font-bold text-neutral-900">
                                Articles ({detailedCart.items.length})
                            </h2>
                            <button
                                onClick={handleClearCart}
                                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium
                                         text-danger hover:text-danger-dark hover:bg-danger-light/50
                                         rounded-apple transition-all duration-200"
                            >
                                <TrashIcon className="w-4 h-4" />
                                Vider le panier
                            </button>
                        </div>

                        {/* Cart Items List */}
                        <div className="space-y-3">
                            {detailedCart.items.map(item => (
                                <ContentSection key={item.dishId} className="hover:shadow-apple-lg transition-shadow">
                                    <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                                        {/* Item Info */}
                                        <div className="flex-1 min-w-0">
                                            <h3 className="text-lg font-semibold text-neutral-900 mb-1">
                                                {item.dishName}
                                            </h3>
                                            <p className="text-sm text-neutral-600">
                                                Prix unitaire: <span className="font-medium">{item.unitPrice.toFixed(2)} €</span>
                                            </p>
                                        </div>

                                        {/* Quantity Controls */}
                                        <div className="flex items-center gap-3">
                                            <div className="flex items-center gap-2 bg-neutral-100 rounded-apple p-1">
                                                <button
                                                    onClick={() => handleUpdateQuantity(item.dishId, item.quantity - 1)}
                                                    className="p-2 hover:bg-white rounded-lg transition-colors
                                                             text-neutral-700 hover:text-primary-600"
                                                    aria-label="Diminuer la quantité"
                                                >
                                                    <MinusIcon className="w-4 h-4" />
                                                </button>
                                                <span className="text-neutral-900 font-semibold min-w-[2rem] text-center">
                                                    {item.quantity}
                                                </span>
                                                <button
                                                    onClick={() => handleUpdateQuantity(item.dishId, item.quantity + 1)}
                                                    className="p-2 hover:bg-white rounded-lg transition-colors
                                                             text-neutral-700 hover:text-primary-600"
                                                    aria-label="Augmenter la quantité"
                                                >
                                                    <PlusIcon className="w-4 h-4" />
                                                </button>
                                            </div>

                                            <button
                                                onClick={() => handleRemoveItem(item.dishId)}
                                                className="p-2 text-danger hover:bg-danger-light rounded-apple
                                                         transition-all duration-200"
                                                aria-label="Retirer l'article"
                                            >
                                                <XMarkIcon className="w-5 h-5" />
                                            </button>
                                        </div>
                                    </div>

                                    {/* Subtotal */}
                                    <div className="mt-3 pt-3 border-t border-neutral-100">
                                        <div className="flex justify-between items-center">
                                            <span className="text-sm text-neutral-600">Sous-total</span>
                                            <span className="text-lg font-bold text-neutral-900">
                                                {(item.unitPrice * item.quantity).toFixed(2)} €
                                            </span>
                                        </div>
                                    </div>
                                </ContentSection>
                            ))}
                        </div>
                    </div>

                    {/* Order Summary Section */}
                    <div className="lg:col-span-1">
                        <div className="sticky top-20 space-y-4">
                            {/* Summary */}
                            <ContentSection title="Résumé de la commande">
                                <div className="space-y-3 mb-4">
                                    <div className="flex justify-between items-center text-neutral-600">
                                        <span>Sous-total</span>
                                        <span className="font-medium">{detailedCart.totalAmount?.toFixed(2)} €</span>
                                    </div>
                                    <div className="flex justify-between items-center text-neutral-600">
                                        <span>Frais de service</span>
                                        <span className="font-medium">0,00 €</span>
                                    </div>
                                </div>

                                <div className="pt-4 border-t-2 border-neutral-200">
                                    <div className="flex justify-between items-center">
                                        <span className="text-lg font-bold text-neutral-900">Total</span>
                                        <span className="text-2xl font-bold text-primary-600">
                                            {detailedCart.totalAmount?.toFixed(2)} €
                                        </span>
                                    </div>
                                </div>
                            </ContentSection>

                            {/* Time Slot Selector */}
                            {detailedCart.restaurantId && (
                                <ContentSection title="Créneau de livraison">
                                    <TimeSlotSelector
                                        restaurantId={detailedCart.restaurantId}
                                        onSlotSelected={(slot) => {
                                            setSlotSelected(true);
                                            console.log('Créneau sélectionné:', slot);
                                        }}
                                    />
                                </ContentSection>
                            )}

                            {/* Checkout Button */}
                            <button
                                onClick={handleProceedToCheckout}
                                disabled={!slotSelected}
                                className="w-full px-6 py-4 bg-primary-500 text-white font-semibold
                                         rounded-apple hover:bg-primary-600 focus:outline-none focus:ring-2
                                         focus:ring-primary-500 focus:ring-offset-2 transition-all duration-200
                                         disabled:opacity-50 disabled:cursor-not-allowed shadow-apple
                                         hover:shadow-apple-lg active:scale-95
                                         disabled:hover:shadow-apple disabled:active:scale-100"
                            >
                                {slotSelected ? 'Passer la commande' : 'Sélectionnez un créneau'}
                            </button>

                            {!slotSelected && (
                                <p className="text-sm text-neutral-600 text-center">
                                    Veuillez sélectionner un créneau de livraison pour continuer
                                </p>
                            )}
                        </div>
                    </div>
                </div>
            </PageContainer>
        </UserLayout>
    );
}
