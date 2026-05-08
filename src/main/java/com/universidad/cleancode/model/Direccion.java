package com.universidad.cleancode.model;

// Representa una dirección postal. Inmutable por diseño, evita primitivos dispersos.
public record Direccion(String calle, String ciudad, String codigoPostal) {

    public Direccion {
        if (calle == null || calle.isBlank()) throw new IllegalArgumentException("La calle no puede estar vacía");
        if (ciudad == null || ciudad.isBlank()) throw new IllegalArgumentException("La ciudad no puede estar vacía");
        if (codigoPostal == null || codigoPostal.isBlank()) throw new IllegalArgumentException("El código postal es requerido");
    }

    public String formatear() {
        return calle.trim().toUpperCase() + ", " + ciudad.trim().toUpperCase() + " - CP: " + codigoPostal.trim();
    }
}
