import PropTypes from 'prop-types';

export const DishType = PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    description: PropTypes.string,
    price: PropTypes.number,
    category: PropTypes.string,
    dietTypes: PropTypes.arrayOf(PropTypes.string),
    available: PropTypes.bool,
});

export const TimeSlotType = PropTypes.shape({
    id: PropTypes.string.isRequired,
    restaurantId: PropTypes.string.isRequired,
    start: PropTypes.string.isRequired,
    end: PropTypes.string.isRequired,
    maxCapacity: PropTypes.number,
    reservedCount: PropTypes.number,
    isAvailable: PropTypes.bool,
});


export const RestaurantType = PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    address: PropTypes.string.isRequired,
    openingTime: PropTypes.string,
    closingTime: PropTypes.string,
    isOpen: PropTypes.bool.isRequired,
    dishes: PropTypes.arrayOf(DishType),
});
