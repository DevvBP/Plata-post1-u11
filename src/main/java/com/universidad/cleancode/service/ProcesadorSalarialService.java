package com.universidad.cleancode.service;

import org.springframework.stereotype.Service;

// TODO: este servicio creció demasiado, hay que partirlo en algún momento
@Service
public class ProcesadorSalarialService {

    // Constante de horas extra según convenio interno
    private static final double VALOR_HORA_EXTRA = 15000.0;
    private static final double UMBRAL_IMPUESTO_ALTO = 5000000.0;

    /**
     * Procesa el pago completo de un empleado. Hace validaciones, cálculos,
     * formateo de dirección y simulación de envío bancario.
     * Refactorizar cuando haya tiempo.
     */
    public String procesarPagoEmpleado(String nombre, String documento,
                                        String calle, String ciudad, String codigoPostal,
                                        double salarioBase, int horasExtras,
                                        double porcentajeImpuesto, String numeroCuenta,
                                        String banco) {

        // --- BLOQUE 1: Validaciones básicas ---
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (documento.length() < 6 || documento.length() > 15) {
            throw new IllegalArgumentException("Documento con longitud inválida: " + documento);
        }
        if (salarioBase <= 0) {
            throw new IllegalArgumentException("El salario base debe ser positivo");
        }
        if (horasExtras < 0) {
            throw new IllegalArgumentException("Las horas extras no pueden ser negativas");
        }
        if (porcentajeImpuesto < 0 || porcentajeImpuesto > 100) {
            throw new IllegalArgumentException("Porcentaje de impuesto fuera de rango: " + porcentajeImpuesto);
        }
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta bancaria es requerido");
        }
        if (banco == null || banco.trim().isEmpty()) {
            throw new IllegalArgumentException("El banco no puede estar vacío");
        }
        if (calle == null || ciudad == null || codigoPostal == null) {
            throw new IllegalArgumentException("La dirección está incompleta");
        }

        // --- BLOQUE 2: Cálculo de horas extras ---
        double pagoHorasExtras = 0.0;
        if (horasExtras > 0 && horasExtras <= 12) {
            pagoHorasExtras = horasExtras * VALOR_HORA_EXTRA;
        } else if (horasExtras > 12) {
            // más de 12 horas: las primeras 12 a tarifa normal, el resto a tarifa doble
            pagoHorasExtras = (12 * VALOR_HORA_EXTRA) + ((horasExtras - 12) * VALOR_HORA_EXTRA * 2);
        }

        // --- BLOQUE 3: Cálculo del bruto ---
        double salarioBruto = salarioBase + pagoHorasExtras;

        // --- BLOQUE 4: Cálculo de impuestos (lógica anidada, difícil de seguir) ---
        double montoImpuesto = 0.0;
        if (salarioBruto > UMBRAL_IMPUESTO_ALTO) {
            if (porcentajeImpuesto > 30) {
                montoImpuesto = salarioBruto * (porcentajeImpuesto / 100.0) * 1.1; // recargo del 10%
            } else {
                montoImpuesto = salarioBruto * (porcentajeImpuesto / 100.0);
            }
        } else {
            montoImpuesto = salarioBruto * (porcentajeImpuesto / 100.0) * 0.9; // descuento del 10%
        }

        // --- BLOQUE 5: Cálculo neto y deducciones de seguridad social ---
        double seguridadSocial = salarioBase * 0.04;
        double fondoPension = salarioBase * 0.04;
        double salarioNeto = salarioBruto - montoImpuesto - seguridadSocial - fondoPension;

        if (salarioNeto < 0) {
            salarioNeto = 0; // nunca puede ser negativo
        }

        // --- BLOQUE 6: Formateo de dirección ---
        String direccionFormateada = calle.trim().toUpperCase()
                + ", " + ciudad.trim().toUpperCase()
                + " - CP: " + codigoPostal.trim();

        // --- BLOQUE 7: Construcción del resumen del empleado ---
        String resumenEmpleado = "Empleado: " + nombre.trim().toUpperCase()
                + " | Doc: " + documento
                + " | Dirección: " + direccionFormateada;

        // --- BLOQUE 8: Simulación de envío bancario ---
        String resultadoBanco;
        if (numeroCuenta.startsWith("0")) {
            // cuentas de ahorro
            resultadoBanco = "[BANCO:" + banco.toUpperCase() + "] Transferencia AHORRO → Cta:" + numeroCuenta;
        } else if (numeroCuenta.startsWith("1") || numeroCuenta.startsWith("2")) {
            // cuentas corrientes
            resultadoBanco = "[BANCO:" + banco.toUpperCase() + "] Transferencia CORRIENTE → Cta:" + numeroCuenta;
        } else {
            resultadoBanco = "[BANCO:" + banco.toUpperCase() + "] Transferencia ESTÁNDAR → Cta:" + numeroCuenta;
        }

        // --- BLOQUE 9: Construcción del comprobante final ---
        String comprobante = "=== COMPROBANTE DE PAGO ===" + "\n"
                + resumenEmpleado + "\n"
                + "Salario Base: $" + String.format("%.2f", salarioBase) + "\n"
                + "Horas Extras (" + horasExtras + "h): $" + String.format("%.2f", pagoHorasExtras) + "\n"
                + "Salario Bruto: $" + String.format("%.2f", salarioBruto) + "\n"
                + "Impuesto (" + porcentajeImpuesto + "%): -$" + String.format("%.2f", montoImpuesto) + "\n"
                + "Seguridad Social (4%): -$" + String.format("%.2f", seguridadSocial) + "\n"
                + "Fondo Pensión (4%): -$" + String.format("%.2f", fondoPension) + "\n"
                + "SALARIO NETO: $" + String.format("%.2f", salarioNeto) + "\n"
                + resultadoBanco + "\n"
                + "=========================";

        return comprobante;
    }
}
