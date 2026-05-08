package com.universidad.cleancode.model;

// Encapsula la información bancaria del empleado de forma cohesiva.
public record DatosBancarios(String numeroCuenta, String banco) {

    private static final String PREFIJO_AHORRO    = "0";
    private static final String PREFIJO_CORRIENTE1 = "1";
    private static final String PREFIJO_CORRIENTE2 = "2";

    public DatosBancarios {
        if (numeroCuenta == null || numeroCuenta.isBlank())
            throw new IllegalArgumentException("El número de cuenta es requerido");
        if (banco == null || banco.isBlank())
            throw new IllegalArgumentException("El banco no puede estar vacío");
    }

    public String tipoCuenta() {
        if (numeroCuenta.startsWith(PREFIJO_AHORRO)) return "AHORRO";
        if (numeroCuenta.startsWith(PREFIJO_CORRIENTE1) || numeroCuenta.startsWith(PREFIJO_CORRIENTE2)) return "CORRIENTE";
        return "ESTÁNDAR";
    }

    public String descripcionTransferencia() {
        return "[BANCO:" + banco.toUpperCase() + "] Transferencia " + tipoCuenta() + " → Cta:" + numeroCuenta;
    }
}
