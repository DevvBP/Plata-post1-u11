package com.universidad.cleancode.model;

// Envuelve un monto monetario. Garantiza que no sea negativo y centraliza el formateo.
public record Dinero(double monto) {

    public Dinero {
        if (monto < 0) throw new IllegalArgumentException("El monto no puede ser negativo: " + monto);
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(this.monto + otro.monto);
    }

    public Dinero restar(Dinero otro) {
        double resultado = this.monto - otro.monto;
        return new Dinero(Math.max(resultado, 0));
    }

    public Dinero multiplicar(double factor) {
        return new Dinero(this.monto * factor);
    }

    public String formatear() {
        return String.format("$%.2f", monto);
    }

    public static Dinero cero() {
        return new Dinero(0.0);
    }
}
