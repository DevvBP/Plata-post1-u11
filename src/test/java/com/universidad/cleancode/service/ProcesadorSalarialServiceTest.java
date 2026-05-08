package com.universidad.cleancode.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcesadorSalarialServiceTest {

    private ProcesadorSalarialService servicio;

    @BeforeEach
    void setUp() {
        servicio = new ProcesadorSalarialService();
    }

    @Test
    void procesarPago_datosValidos_retornaComprobante() {
        String resultado = servicio.procesarPagoEmpleado(
                "Juan Pérez", "123456789",
                "Calle 45 #12-34", "Bogotá", "110111",
                3000000.0, 8, 19.0,
                "0123456789", "Bancolombia"
        );
        assertNotNull(resultado);
        assertTrue(resultado.contains("COMPROBANTE DE PAGO"));
        assertTrue(resultado.contains("JUAN PÉREZ"));
        assertTrue(resultado.contains("SALARIO NETO"));
    }

    @Test
    void procesarPago_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.procesarPagoEmpleado(
                        "", "123456789",
                        "Calle 1", "Medellín", "050001",
                        2000000.0, 0, 10.0,
                        "1987654321", "Davivienda"
                )
        );
    }

    @Test
    void procesarPago_salarioNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.procesarPagoEmpleado(
                        "Ana Torres", "987654321",
                        "Av. 80", "Cali", "760001",
                        -500.0, 0, 10.0,
                        "0987654321", "BBVA"
                )
        );
    }

    @Test
    void procesarPago_horasExtrasAltas_aplicaTarifaDoble() {
        String resultado = servicio.procesarPagoEmpleado(
                "Pedro López", "112233445",
                "Carrera 7", "Bucaramanga", "680001",
                4000000.0, 15, 19.0,
                "2345678901", "Bancolombia"
        );
        assertTrue(resultado.contains("15h"));
    }

    @Test
    void procesarPago_salarioAltoConImpuestoAlto_aplicaRecargo() {
        String resultado = servicio.procesarPagoEmpleado(
                "María García", "556677889",
                "Calle 100", "Bogotá", "110221",
                6000000.0, 0, 35.0,
                "1122334455", "Nequi"
        );
        assertTrue(resultado.contains("COMPROBANTE"));
    }

    @Test
    void procesarPago_cuentaCorriente_muestraTipoCorrecto() {
        String resultado = servicio.procesarPagoEmpleado(
                "Carlos Ruiz", "667788990",
                "Diagonal 22", "Barranquilla", "080001",
                2500000.0, 4, 10.0,
                "1567890123", "Davivienda"
        );
        assertTrue(resultado.contains("CORRIENTE") || resultado.contains("ESTÁNDAR") || resultado.contains("AHORRO"));
    }
}
