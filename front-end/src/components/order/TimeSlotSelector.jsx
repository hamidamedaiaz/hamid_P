import React from 'react';
import PropTypes from 'prop-types';
import {TimeSlotType} from '../../utils/types.js';
import './TimeSlotSelector.css';
import {fetchDeliverySlots} from "../../services/restaurantService.js";

export default function TimeSlotSelector({restaurantId, selectedSlot}) {
    const slots = fetchDeliverySlots(restaurantId);
    return (
        <div className="time-slot-selector">
            <h3>Select a time slot</h3>
            <ul>
                {slots.map(slot => (
                    <li
                        key={slot.id}
                        className={selectedSlot?.id === slot.id ? "selected" : ""}
                        onClick={() => onSelectSlot(slot)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={e => {
                            if (e.key === 'Enter' || e.key === ' ') onSelectSlot(slot);
                        }}
                    >
                        {slot.start} - {slot.end}
                    </li>
                ))}
            </ul>
        </div>
    );
}

TimeSlotSelector.propTypes = {
    restaurantId: PropTypes.string.isRequired,
    selectedSlot: TimeSlotType,
};
