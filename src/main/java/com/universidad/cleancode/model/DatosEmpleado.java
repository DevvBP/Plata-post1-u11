package com.universidad.cleancode.model;

// Agrupa los datos de identidad de un empleado. Evita métodos con 10+ parámetros primitivos.
public record DatosEmpleado(String nombre, String documento) {

    public DatosEmpleado {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del empleado no puede estar vacío");
        if (documento == null || documento.isBlank())
            throw new IllegalArgumentException("El documento es obligatorio");
        if (documento.length() < 6 || documento.length() > 15)
            throw new IllegalArgumentException("Documento con longitud inválida: " + documento);
    }

    public String nombreFormateado() {
        return nombre.trim().toUpperCase();
    }

    public String resumen() {
        return "Empleado: " + nombreFormateado() + " | Doc: " + documento;
    }
}
