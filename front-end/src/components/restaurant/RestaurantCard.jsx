import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RestaurantType } from '../../utils/types.js';
import PropTypes from 'prop-types';
import { deleteRestaurant } from '../../services/restaurantService.js';
import {
  MapPinIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  TrashIcon,
  ChevronRightIcon,
} from '@heroicons/react/24/outline';
import {
  CheckCircleIcon as CheckCircleSolid,
  XCircleIcon as XCircleSolid,
} from '@heroicons/react/24/solid';
import notificationService from '../../utils/notificationService.jsx';
import { uiLogger } from '../../utils/logger.js';
import errorHandler from '../../utils/errorHandler.js';

export default function RestaurantCard({ restaurant, isManager }) {
    const navigate = useNavigate();
    const [isDeleting, setIsDeleting] = useState(false);

    const handleNavigate = () => {
        uiLogger.userAction('Restaurant card clicked', {
            restaurantId: restaurant.id,
            restaurantName: restaurant.name
        });

        if (isManager) {
            navigate(`/manager/restaurants/${restaurant.id}`);
        } else {
            navigate(`/restaurants/${restaurant.id}`);
        }
    };

    const handleDelete = async (e) => {
        e.stopPropagation();

        uiLogger.userAction('Delete restaurant initiated', {
            restaurantId: restaurant.id
        });

        // Confirmation avec le système de notification professionnel
        notificationService.confirmAction(
            `Êtes-vous sûr de vouloir supprimer "${restaurant.name}" ?`,
            async () => {
                try {
                    setIsDeleting(true);
                    await deleteRestaurant(restaurant.id);

                    notificationService.success(
                        `Le restaurant "${restaurant.name}" a été supprimé avec succès`
                    );

                    uiLogger.info('Restaurant deleted successfully', {
                        restaurantId: restaurant.id
                    });

                    // Recharger la page ou actualiser la liste
                    window.location.reload();
                } catch (error) {
                    errorHandler.handleApiError(
                        error,
                        `/api/restaurants/${restaurant.id}`,
                        'DELETE'
                    );
                } finally {
                    setIsDeleting(false);
                }
            }
        );
    };

    // Formater les horaires
    const formatTime = (time) => {
        if (!time) return 'N/A';
        if (typeof time === 'string') {
            return time.substring(0, 5);
        }
        return time;
    };

    const availableDishCount = restaurant.dishes
        ? restaurant.dishes.filter(dish => dish.available).length
        : 0;

    const StatusBadge = ({ isOpen }) => (
        <div className={`
            inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold
            transition-all duration-200
            ${isOpen 
                ? 'bg-success-light text-success-dark' 
                : 'bg-danger-light text-danger-dark'
            }
        `}>
            {isOpen ? (
                <>
                    <CheckCircleSolid className="w-4 h-4" />
                    <span>Ouvert</span>
                </>
            ) : (
                <>
                    <XCircleSolid className="w-4 h-4" />
                    <span>Fermé</span>
                </>
            )}
        </div>
    );

    return (
        <div className="group relative bg-white rounded-apple-lg border border-neutral-200 shadow-apple hover:shadow-apple-lg
                        transition-all duration-300 ease-out hover:-translate-y-1 overflow-hidden">
            {/* Barre de statut en haut */}
            <div className={`h-1 w-full ${restaurant.isOpen ? 'bg-success' : 'bg-danger'}`} />

            {/* Badge de statut */}
            <div className="absolute top-4 right-4 z-10">
                <StatusBadge isOpen={restaurant.isOpen} />
            </div>

            {/* Contenu principal cliquable */}
            <button
                type="button"
                className="w-full text-left p-6 focus:outline-none focus:ring-2 focus:ring-primary-500
                           focus:ring-offset-2 rounded-apple-lg transition-colors hover:bg-neutral-50"
                onClick={handleNavigate}
                disabled={isDeleting}
            >
                {/* Nom du restaurant */}
                <div className="mb-4 pr-24">
                    <h3 className="text-xl font-semibold text-neutral-900 mb-1 group-hover:text-primary-600
                                   transition-colors leading-tight">
                        {restaurant.name}
                    </h3>
                </div>

                {/* Informations */}
                <div className="space-y-3">
                    {/* Adresse */}
                    <div className="flex items-start gap-2 text-neutral-600">
                        <MapPinIcon className="w-5 h-5 mt-0.5 flex-shrink-0 text-neutral-400" />
                        <span className="text-sm leading-relaxed">{restaurant.address}</span>
                    </div>

                    {/* Horaires */}
                    <div className="flex items-center gap-2 text-neutral-600">
                        <ClockIcon className="w-5 h-5 flex-shrink-0 text-neutral-400" />
                        <span className="text-sm font-medium">
                            {formatTime(restaurant.openingTime)} - {formatTime(restaurant.closingTime)}
                        </span>
                    </div>

                    {/* Statistiques */}
                    <div className="flex items-center gap-4 pt-3 border-t border-neutral-100">
                        <div className="flex items-center gap-2">
                            <div className="flex items-center justify-center w-8 h-8 bg-primary-50 rounded-lg">
                                <svg className="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                          d="M3 3h18v18H3V3zm3 3v12m3-12v12m3-12v12m3-12v12m3-12v12" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-xs text-neutral-500 font-medium">Plats disponibles</p>
                                <p className="text-sm font-semibold text-neutral-900">{availableDishCount}</p>
                            </div>
                        </div>

                        {restaurant.restaurantType && (
                            <div className="flex-1 text-right">
                                <span className="inline-block px-3 py-1 text-xs font-medium text-neutral-600
                                               bg-neutral-100 rounded-full">
                                    {restaurant.restaurantType}
                                </span>
                            </div>
                        )}
                    </div>
                </div>

                {/* Indicateur de navigation */}
                <div className="mt-4 flex items-center text-primary-600 text-sm font-medium
                               opacity-0 group-hover:opacity-100 transition-opacity">
                    <span>Voir le menu</span>
                    <ChevronRightIcon className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
                </div>
            </button>

            {/* Actions pour manager */}
            {isManager && (
                <div className="px-6 pb-4 pt-2 border-t border-neutral-100 bg-neutral-50">
                    <button
                        type="button"
                        className="inline-flex items-center gap-2 px-4 py-2 text-sm font-semibold text-white
                                 bg-danger rounded-apple hover:bg-danger-dark focus:outline-none focus:ring-2
                                 focus:ring-danger focus:ring-offset-2 transition-all duration-200
                                 disabled:opacity-50 disabled:cursor-not-allowed shadow-apple hover:shadow-apple-lg
                                 active:scale-95"
                        onClick={handleDelete}
                        disabled={isDeleting}
                    >
                        <TrashIcon className="w-4 h-4" />
                        {isDeleting ? 'Suppression...' : 'Supprimer'}
                    </button>
                </div>
            )}
        </div>
    );
}

RestaurantCard.propTypes = {
    restaurant: RestaurantType.isRequired,
    isManager: PropTypes.bool,
};
