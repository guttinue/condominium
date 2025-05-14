package com.condominium.util;

public class InputValidator {
    // garanta que strin é difrerente de null
    public static String requireNonEmpty(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " não pode ficar em branco.");
        }
        return value.trim();
    }

    // conversão de data
    public static java.time.LocalDate parseDate(String s) {
        try {
            return java.time.LocalDate.parse(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("Data inválida. Use YYYY-MM-DD.");
        }
    }

    // conversão de hora
    public static java.time.LocalTime parseTime(String s) {
        try {
            return java.time.LocalTime.parse(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("Hora inválida. Use HH:MM.");
        }
    }
}
