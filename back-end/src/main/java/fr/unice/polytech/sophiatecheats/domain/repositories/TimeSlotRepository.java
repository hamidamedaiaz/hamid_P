package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des créneaux de livraison
 */
public interface TimeSlotRepository extends Repository<TimeSlot, UUID> {

    /**
     * @param date La date du jour pour laquelle on cherche les créneaux disponibles
     * @return Liste des TimeSlot encore disponibles pour la date donnée
     */
    List<TimeSlot> findAvailableSlots(LocalDate date);

    /**
     * @param slot Le créneau à sauvegarder
     * @return Le créneau sauvegardé (potentiellement enrichi d’un ID)
     */
    @Override
    TimeSlot save(TimeSlot slot);

    /**
     * Met à jour un créneau existant.
     *
     * @param slot Le créneau à mettre à jour.
     */
    void update(TimeSlot slot);

    /**
     * Trouve un créneau par son ID et l'ID du restaurant.
     * Cette méthode permet de valider qu'un créneau appartient bien au restaurant attendu.
     *
     * @param slotId L'ID du créneau
     * @param restaurantId L'ID du restaurant
     * @return Le créneau s'il existe et appartient au restaurant, sinon vide
     */
    Optional<TimeSlot> findByIdAndRestaurantId(UUID slotId, UUID restaurantId);

    /**
     * Trouve tous les créneaux disponibles pour un restaurant et une date donnée.
     * Cette méthode optimise la requête en filtrant directement par restaurantId.
     *
     * @param restaurantId L'ID du restaurant
     * @param date La date pour laquelle chercher les créneaux
     * @return Liste des créneaux disponibles pour ce restaurant et cette date
     */
    List<TimeSlot> findAvailableSlotsByRestaurantAndDate(UUID restaurantId, LocalDate date);
}
