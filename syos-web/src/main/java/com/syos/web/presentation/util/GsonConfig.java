package com.syos.web.presentation.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson configuration utility
 * Configures Gson to handle LocalDate and LocalDateTime serialization/deserialization
 */
public class GsonConfig {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                // 🆕 IMPORTANT - Make fields accessible
                .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
                // LocalDate support
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                // LocalDateTime support
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                // 🆕 IMPORTANT - Be lenient with malformed JSON
                .setLenient()
                // 🆕 IMPORTANT - Don't fail on missing fields
                .serializeNulls()
                .create();
    }

    public static Gson getGson() {
        return GSON;
    }

    /* ==========================================
       LocalDate Adapter (Serializer + Deserializer)
       ========================================== */

    /**
     * Custom adapter for LocalDate (handles both serialization and deserialization)
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
            if (localDate == null) {
                return null;
            }
            return new JsonPrimitive(localDate.format(formatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            try {
                return LocalDate.parse(json.getAsString(), formatter);
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse LocalDate: " + json.getAsString(), e);
            }
        }
    }

    /* ==========================================
       LocalDateTime Adapter (Serializer + Deserializer)
       ========================================== */

    /**
     * Custom adapter for LocalDateTime (handles both serialization and deserialization)
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            if (localDateTime == null) {
                return null;
            }
            return new JsonPrimitive(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            try {
                return LocalDateTime.parse(json.getAsString(), formatter);
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse LocalDateTime: " + json.getAsString(), e);
            }
        }
    }
}