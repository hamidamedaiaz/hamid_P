import React, { useState } from 'react';
import {
    FunnelIcon,
    XMarkIcon,
    MagnifyingGlassIcon
} from '@heroicons/react/24/outline';

export default function FilterBar({ onFilter }) {
    const [filters, setFilters] = useState({
        isOpen: '',
        cuisineType: '',
        restaurantType: '',
        dietType: '',
        priceRange: '',
        searchTerm: '',
    });

    const [isExpanded, setIsExpanded] = useState(false);

    function handleChange(e) {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
    }

    function handleSubmit(e) {
        e.preventDefault();
        onFilter(filters);
    }

    function handleReset() {
        const emptyFilters = {
            isOpen: '',
            cuisineType: '',
            restaurantType: '',
            dietType: '',
            priceRange: '',
            searchTerm: '',
        };
        setFilters(emptyFilters);
        onFilter(emptyFilters);
    }

    const activeFiltersCount = Object.values(filters).filter(v => v !== '').length;

    return (
        <div className="bg-white rounded-apple-lg border border-neutral-200 shadow-apple mb-6">
            {/* Header - Always visible */}
            <div className="flex items-center justify-between p-4 border-b border-neutral-100">
                <div className="flex items-center gap-3">
                    <FunnelIcon className="w-5 h-5 text-neutral-600" />
                    <h3 className="font-semibold text-neutral-900">Filtres</h3>
                    {activeFiltersCount > 0 && (
                        <span className="inline-flex items-center justify-center px-2.5 py-0.5
                                       bg-primary-500 text-white text-xs font-semibold rounded-full">
                            {activeFiltersCount}
                        </span>
                    )}
                </div>
                <button
                    type="button"
                    onClick={() => setIsExpanded(!isExpanded)}
                    className="text-sm font-medium text-primary-600 hover:text-primary-700
                             transition-colors"
                >
                    {isExpanded ? 'Masquer' : 'Afficher'}
                </button>
            </div>

            {/* Filters Content */}
            {isExpanded && (
                <form onSubmit={handleSubmit} className="p-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
                        {/* Search */}
                        <div className="lg:col-span-3">
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                <MagnifyingGlassIcon className="w-4 h-4 inline mr-1" />
                                Rechercher
                            </label>
                            <input
                                type="text"
                                name="searchTerm"
                                value={filters.searchTerm}
                                onChange={handleChange}
                                placeholder="Nom du restaurant, plat..."
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            />
                        </div>

                        {/* Availability */}
                        <div>
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                Disponibilité
                            </label>
                            <select
                                name="isOpen"
                                value={filters.isOpen}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            >
                                <option value="">Tous</option>
                                <option value="true">Ouvert maintenant</option>
                                <option value="false">Fermé</option>
                            </select>
                        </div>

                        {/* Restaurant Type */}
                        <div>
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                Type d'établissement
                            </label>
                            <select
                                name="restaurantType"
                                value={filters.restaurantType}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            >
                                <option value="">Tous</option>
                                <option value="CROUS">CROUS</option>
                                <option value="RESTAURANT">Restaurant</option>
                                <option value="FOOD_TRUCK">Food Truck</option>
                                <option value="CAFETERIA">Cafétéria</option>
                                <option value="FAST_FOOD">Fast Food</option>
                            </select>
                        </div>

                        {/* Cuisine Type */}
                        <div>
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                Type de cuisine
                            </label>
                            <select
                                name="cuisineType"
                                value={filters.cuisineType}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            >
                                <option value="">Toutes</option>
                                <option value="FRENCH">Française</option>
                                <option value="ITALIAN">Italienne</option>
                                <option value="ASIAN">Asiatique</option>
                                <option value="MEDITERRANEAN">Méditerranéenne</option>
                                <option value="JAPANESE">Japonaise</option>
                                <option value="MEXICAN">Mexicaine</option>
                                <option value="AMERICAN">Américaine</option>
                                <option value="MIDDLE_EASTERN">Moyen-Orient</option>
                            </select>
                        </div>

                        {/* Diet Type */}
                        <div>
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                Régime alimentaire
                            </label>
                            <select
                                name="dietType"
                                value={filters.dietType}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            >
                                <option value="">Tous</option>
                                <option value="VEGETARIAN">Végétarien</option>
                                <option value="VEGAN">Vegan</option>
                                <option value="GLUTEN_FREE">Sans gluten</option>
                                <option value="LACTOSE_FREE">Sans lactose</option>
                                <option value="HALAL">Halal</option>
                                <option value="KOSHER">Casher</option>
                                <option value="ORGANIC">Bio</option>
                            </select>
                        </div>

                        {/* Price Range */}
                        <div>
                            <label className="block text-sm font-medium text-neutral-700 mb-2">
                                Gamme de prix
                            </label>
                            <select
                                name="priceRange"
                                value={filters.priceRange}
                                onChange={handleChange}
                                className="w-full px-4 py-2.5 text-sm bg-white border border-neutral-300
                                         rounded-apple focus:outline-none focus:ring-2 focus:ring-primary-500
                                         focus:border-transparent transition-all"
                            >
                                <option value="">Toutes</option>
                                <option value="LOW">€ - Économique (0-10€)</option>
                                <option value="MEDIUM">€€ - Moyen (10-20€)</option>
                                <option value="HIGH">€€€ - Élevé (20€+)</option>
                            </select>
                        </div>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex flex-col sm:flex-row gap-3 pt-4 border-t border-neutral-100">
                        <button
                            type="submit"
                            className="flex-1 inline-flex items-center justify-center gap-2 px-6 py-2.5
                                     bg-primary-500 text-white font-semibold rounded-apple
                                     hover:bg-primary-600 focus:outline-none focus:ring-2
                                     focus:ring-primary-500 focus:ring-offset-2
                                     transition-all duration-200 active:scale-95 shadow-apple"
                        >
                            <FunnelIcon className="w-5 h-5" />
                            Appliquer les filtres
                        </button>
                        <button
                            type="button"
                            onClick={handleReset}
                            className="flex-1 inline-flex items-center justify-center gap-2 px-6 py-2.5
                                     bg-neutral-100 text-neutral-900 font-semibold rounded-apple
                                     hover:bg-neutral-200 focus:outline-none focus:ring-2
                                     focus:ring-neutral-400 focus:ring-offset-2
                                     transition-all duration-200 active:scale-95"
                        >
                            <XMarkIcon className="w-5 h-5" />
                            Réinitialiser
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}
