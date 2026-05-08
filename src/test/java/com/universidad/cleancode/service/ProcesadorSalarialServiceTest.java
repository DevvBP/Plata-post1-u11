package com.universidad.cleancode.service;

import com.universidad.cleancode.model.DatosBancarios;
import com.universidad.cleancode.model.DatosEmpleado;
import com.universidad.cleancode.model.Direccion;
import com.universidad.cleancode.model.Dinero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcesadorSalarialServiceTest {

    private ProcesadorSalarialService servicio;

    @BeforeEach
    void setUp() {
        ValidadorEmpleados validador    = new ValidadorEmpleados();
        CalculadoraImpuestos calculadora = new CalculadoraImpuestos();
        servicio = new ProcesadorSalarialService(validador, calculadora);
    }

    @Test
    void procesarPago_datosValidos_retornaComprobante() {
        DatosEmpleado empleado   = new DatosEmpleado("Juan Pérez", "123456789");
        Direccion direccion      = new Direccion("Calle 45 #12-34", "Bogotá", "110111");
        DatosBancarios banco     = new DatosBancarios("0123456789", "Bancolombia");

        String resultado = servicio.procesarPagoEmpleado(empleado, direccion, 3_000_000.0, 8, 19.0, banco);

        assertNotNull(resultado);
        assertTrue(resultado.contains("COMPROBANTE DE PAGO"));
        assertTrue(resultado.contains("JUAN PÉREZ"));
        assertTrue(resultado.contains("SALARIO NETO"));
    }

    @Test
    void procesarPago_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                new DatosEmpleado("", "123456789")
        );
    }

    @Test
    void procesarPago_salarioNegativo_lanzaExcepcion() {
        DatosEmpleado empleado = new DatosEmpleado("Ana Torres", "987654321");
        Direccion direccion    = new Direccion("Av. 80", "Cali", "760001");
        DatosBancarios banco   = new DatosBancarios("0987654321", "BBVA");

        assertThrows(IllegalArgumentException.class, () ->
                servicio.procesarPagoEmpleado(empleado, direccion, -500.0, 0, 10.0, banco)
        );
    }

    @Test
    void procesarPago_horasExtrasAltas_aplicaTarifaDoble() {
        DatosEmpleado empleado = new DatosEmpleado("Pedro López", "112233445");
        Direccion direccion    = new Direccion("Carrera 7", "Bucaramanga", "680001");
        DatosBancarios banco   = new DatosBancarios("2345678901", "Bancolombia");

        String resultado = servicio.procesarPagoEmpleado(empleado, direccion, 4_000_000.0, 15, 19.0, banco);

        assertTrue(resultado.contains("15h"));
    }

    @Test
    void procesarPago_salarioAltoConImpuestoAlto_retornaComprobante() {
        DatosEmpleado empleado = new DatosEmpleado("María García", "556677889");
        Direccion direccion    = new Direccion("Calle 100", "Bogotá", "110221");
        DatosBancarios banco   = new DatosBancarios("1122334455", "Nequi");

        String resultado = servicio.procesarPagoEmpleado(empleado, direccion, 6_000_000.0, 0, 35.0, banco);

        assertTrue(resultado.contains("COMPROBANTE"));
    }

    @Test
    void procesarPago_cuentaCorriente_muestraTipoCorrecto() {
        DatosEmpleado empleado = new DatosEmpleado("Carlos Ruiz", "667788990");
        Direccion direccion    = new Direccion("Diagonal 22", "Barranquilla", "080001");
        DatosBancarios banco   = new DatosBancarios("1567890123", "Davivienda");

        String resultado = servicio.procesarPagoEmpleado(empleado, direccion, 2_500_000.0, 4, 10.0, banco);

        assertTrue(resultado.contains("CORRIENTE") || resultado.contains("AHORRO") || resultado.contains("ESTÁNDAR"));
    }

    @Test
    void dinero_restar_nuncaNegativo() {
        Dinero base     = new Dinero(100.0);
        Dinero exceso   = new Dinero(500.0);
        Dinero resultado = base.restar(exceso);

        assertEquals(0.0, resultado.monto());
    }

    @Test
    void calculadora_horasExtrasDoble_aplicaCorrectamente() {
        CalculadoraImpuestos calc = new CalculadoraImpuestos();
        Dinero extras = calc.calcularTotalHorasExtras(14);

        // 12 horas normales + 2 horas dobles
        double esperado = (12 * 15_000.0) + (2 * 15_000.0 * 2);
        assertEquals(esperado, extras.monto(), 0.001);
    }

    @Test
    void direccion_formatear_retornaMayusculas() {
        Direccion dir = new Direccion("calle 45", "bogotá", "110111");
        assertTrue(dir.formatear().contains("CALLE 45"));
        assertTrue(dir.formatear().contains("BOGOTÁ"));
    }

    @Test
    void datosBancarios_cuentaAhorro_tipoCorecto() {
        DatosBancarios datos = new DatosBancarios("0123456789", "Bancolombia");
        assertEquals("AHORRO", datos.tipoCuenta());
    }
}
