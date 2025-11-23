package fr.unice.polytech.sophiatecheats.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * Utilitaires pour sérialisation et désérialisation JSON.
 * Gère les types java.time et lève des exceptions en cas d'erreur.
 */
public class JaxsonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JaxsonUtils() {}

    /**
     * Convertit un objet Java en JSON.
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Convertit un JSON en objet Java.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // À améliorer : lever une exception et éviter de retourner null
        }
    }
}
