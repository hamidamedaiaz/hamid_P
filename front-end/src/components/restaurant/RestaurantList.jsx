import React, { useEffect, useState } from 'react';
import RestaurantCard from './RestaurantCard.jsx';
import FilterBar from './FilterBar.jsx';
import LoadingSpinner from '../common/LoadingSpinner.jsx';
import { Alert } from '../common/UI.jsx';
import PropTypes from "prop-types";
import { fetchRestaurants, filterRestaurants } from "../../services/restaurantService.js";
import { uiLogger } from '../../utils/logger.js';
import errorHandler from '../../utils/errorHandler.js';

export default function RestaurantList({ isManager = false }) {
    const [restaurants, setRestaurants] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    async function loadRestaurants(filters = {}) {
        try {
            setLoading(true);
            setError(null);

            const data = Object.keys(filters).filter(key => filters[key] !== '').length > 0
                ? await filterRestaurants(filters)
                : await fetchRestaurants();

            setRestaurants(data);
            uiLogger.info('Restaurants loaded', { count: data.length, filters });
        } catch (err) {
            const handledError = errorHandler.handle(err, { context: 'loadRestaurants' }, false);
            setError(handledError.userMessage);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadRestaurants();
    }, []);

    function handleFilter(filters) {
        uiLogger.userAction('Filters applied', filters);
        loadRestaurants(filters);
    }

    if (loading) {
        return <LoadingSpinner message="Chargement des restaurants..." fullScreen={false} />;
    }

    if (error) {
        return (
            <div className="space-y-4">
                <Alert variant="danger" dismissible onDismiss={() => setError(null)}>
                    {error}
                </Alert>
                <button
                    onClick={() => loadRestaurants()}
                    className="px-4 py-2 bg-primary-500 text-white font-semibold rounded-apple
                             hover:bg-primary-600 transition-all"
                >
                    Réessayer
                </button>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            <FilterBar onFilter={handleFilter} />

            {restaurants.length === 0 ? (
                <div className="text-center py-12">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-neutral-100
                                  rounded-full mb-4">
                        <svg className="w-8 h-8 text-neutral-400" fill="none" stroke="currentColor"
                             viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                  d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                        </svg>
                    </div>
                    <h3 className="text-lg font-semibold text-neutral-900 mb-2">
                        Aucun restaurant trouvé
                    </h3>
                    <p className="text-neutral-600">
                        Essayez de modifier vos filtres pour voir plus de résultats
                    </p>
                </div>
            ) : (
                <>
                    <div className="flex items-center justify-between">
                        <p className="text-sm text-neutral-600">
                            <span className="font-semibold text-neutral-900">{restaurants.length}</span>
                            {' '}restaurant{restaurants.length > 1 ? 's' : ''} trouvé{restaurants.length > 1 ? 's' : ''}
                        </p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {restaurants.map((r) => (
                            <RestaurantCard key={r.id} restaurant={r} isManager={isManager} />
                        ))}
                    </div>
                </>
            )}
        </div>
    );
}

RestaurantList.propTypes = {
    isManager: PropTypes.bool,
};