package fr.unice.polytech.sophiatecheats.application.usecases.user.order;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.ValidateDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Tests du Use Case - Passer une Commande (Cart → Order)")
class PlaceOrderUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private ValidateDeliverySlotUseCase validateDeliverySlotUseCase;

    private PlaceOrderUseCase useCase;

    private UUID userId;
    private UUID restaurantId;
    private UUID deliverySlotId;
    private User testUser;
    private Restaurant testRestaurant;
    private Dish testDish;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new PlaceOrderUseCase(userRepository, restaurantRepository, orderRepository, cartRepository, timeSlotRepository, validateDeliverySlotUseCase);

        // Initialisation des données de test
        userId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        deliverySlotId = UUID.randomUUID();

        // Création d'un utilisateur avec 50€ de crédit
        testUser = new User("marcel@example.com", "Marcel Dupont");
        testUser.setStudentCredit(BigDecimal.valueOf(50.00));

        // Création d'un restaurant et d'un plat
        testRestaurant = new Restaurant("Le Tacos du Campus", "Tacos et burgers");
        testDish = Dish.builder()
                .name("Tacos 3 viandes")
                .description("Délicieux tacos")
                .price(BigDecimal.valueOf(8.50))
                .build();
        testRestaurant.addDish(testDish);

        // Création d'un Cart avec 2 tacos
        testCart = new Cart(userId);
        testCart.addDish(testDish, 2, testRestaurant.getId()); // 2 * 8.50 = 17.00€
    }

    /**
     * Helper method to setup a valid delivery slot for the cart
     */
    private void setupValidDeliverySlot() {
        testCart.setDeliverySlot(deliverySlotId);

        fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot mockTimeSlot =
                mock(fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot.class);
        when(timeSlotRepository.findById(deliverySlotId)).thenReturn(Optional.of(mockTimeSlot));
        when(mockTimeSlot.getRestaurantId()).thenReturn(restaurantId);
        when(mockTimeSlot.isAvailable()).thenReturn(true);
        when(mockTimeSlot.getReservedCount()).thenReturn(0);
        when(mockTimeSlot.getMaxCapacity()).thenReturn(10);
        when(mockTimeSlot.getStartTime()).thenReturn(java.time.LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));
        when(mockTimeSlot.getEndTime()).thenReturn(java.time.LocalDateTime.now().plusDays(1).withHour(12).withMinute(30));

        // Mock validateDeliverySlotUseCase to do nothing (validation passes)
        doNothing().when(validateDeliverySlotUseCase).execute(any());
    }


    /**
     * Test d'échec : crédit étudiant insuffisant.
     */
    @Test
    @DisplayName("Devrait échouer si le crédit étudiant est insuffisant")
    void should_fail_when_insufficient_student_credit() {
        // Given - Marcel a seulement 10€ mais le panier contient 17€
        testUser.setStudentCredit(BigDecimal.valueOf(10.00));

        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        // Setup valid delivery slot
        setupValidDeliverySlot();

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        // When & Then
        InsufficientCreditException exception = assertThrows(
                InsufficientCreditException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée pour crédit insuffisant"
        );

        assertTrue(exception.getMessage().contains("Crédit étudiant insuffisant"));
        assertEquals(BigDecimal.valueOf(10.00), testUser.getStudentCredit(),
                "Le crédit ne devrait pas avoir été débité");

        // Vérification qu'aucune commande n'a été sauvegardée
        verify(orderRepository, never()).save(any(Order.class));
        verify(userRepository, never()).save(any(User.class));
        verify(cartRepository, never()).delete(any(Cart.class));
    }


    /**
     * Test d'échec : aucun panier actif.
     */
    @Test
    @DisplayName("Devrait échouer si aucun panier actif n'existe")
    void should_fail_when_no_active_cart() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée si aucun panier actif"
        );

        assertTrue(exception.getMessage().contains("Aucun panier actif trouvé"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : panier vide.
     */
    @Test
    @DisplayName("Devrait échouer si le panier est vide")
    void should_fail_when_cart_is_empty() {
        // Given
        Cart emptyCart = new Cart(userId);

        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(emptyCart));

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée si le panier est vide"
        );

        assertTrue(exception.getMessage().contains("Le panier est vide"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : utilisateur a déjà une commande active.
     */
    @Test
    @DisplayName("Devrait échouer si l'utilisateur a déjà une commande active")
    void should_fail_when_user_has_active_order() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        // Mock : l'utilisateur a déjà une commande active
        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée si une commande est déjà active"
        );

        assertTrue(exception.getMessage().contains("Vous avez déjà une commande en cours"));

        // Vérification qu'on n'a pas continué le traitement
        verify(userRepository, never()).findById(any());
        verify(cartRepository, never()).findActiveCartByUserId(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : utilisateur introuvable.
     */
    @Test
    @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
    void should_fail_when_user_not_found() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée si l'utilisateur n'existe pas"
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(restaurantRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : restaurant introuvable.
     */
    @Test
    @DisplayName("Devrait échouer si le restaurant n'existe pas")
    void should_fail_when_restaurant_not_found() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
                userId,
                restaurantId,
                PaymentMethod.STUDENT_CREDIT,
                deliverySlotId
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> useCase.execute(request),
                "Une exception devrait être levée si le restaurant n'existe pas"
        );

        assertTrue(exception.getMessage().contains("Restaurant not found"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : requête invalide (null).
     */
    @Test
    @DisplayName("Devrait échouer si la requête est nulle")
    void should_fail_when_request_is_null() {
        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null),
                "Une exception devrait être levée si la requête est nulle"
        );

        verify(userRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }


}
