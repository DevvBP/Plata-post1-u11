package com.universidad.cleancode.service;

import com.universidad.cleancode.model.DatosBancarios;
import com.universidad.cleancode.model.DatosEmpleado;
import com.universidad.cleancode.model.Direccion;
import com.universidad.cleancode.model.Dinero;
import org.springframework.stereotype.Service;

/**
 * Orquesta el flujo de pago: valida, calcula y genera el comprobante.
 * Cada responsabilidad está delegada a su clase correspondiente.
 */
@Service
public class ProcesadorSalarialService {

    private final ValidadorEmpleados   validador;
    private final CalculadoraImpuestos calculadora;

    public ProcesadorSalarialService(ValidadorEmpleados validador, CalculadoraImpuestos calculadora) {
        this.validador    = validador;
        this.calculadora  = calculadora;
    }

    public String procesarPagoEmpleado(DatosEmpleado empleado, Direccion direccion,
                                        double salarioBase, int horasExtras,
                                        double porcentajeImpuesto, DatosBancarios datosBancarios) {
        validar(empleado, salarioBase, horasExtras, porcentajeImpuesto);

        Dinero base      = new Dinero(salarioBase);
        Dinero bruto     = calcularBruto(base, horasExtras);
        Dinero deducciones = calculadora.aplicarDeducciones(bruto, porcentajeImpuesto);
        Dinero neto      = bruto.restar(deducciones);

        return construirComprobante(empleado, direccion, base, horasExtras,
                                    bruto, porcentajeImpuesto, deducciones, neto, datosBancarios);
    }

    // --- Métodos privados extraídos (Extract Method) ---

    private void validar(DatosEmpleado empleado, double salarioBase,
                         int horasExtras, double porcentajeImpuesto) {
        validador.validarDatosEmpleado(empleado);
        validador.validarParametrosSalariales(salarioBase, horasExtras, porcentajeImpuesto);
    }

    private Dinero calcularBruto(Dinero salarioBase, int horasExtras) {
        Dinero extras = calculadora.calcularTotalHorasExtras(horasExtras);
        return salarioBase.sumar(extras);
    }

    private String construirComprobante(DatosEmpleado empleado, Direccion direccion,
                                         Dinero base, int horasExtras, Dinero bruto,
                                         double porcentajeImpuesto, Dinero deducciones,
                                         Dinero neto, DatosBancarios datosBancarios) {
        Dinero extras          = calculadora.calcularTotalHorasExtras(horasExtras);
        Dinero impuesto        = calculadora.calcularImpuesto(bruto, porcentajeImpuesto);
        Dinero seguridadSocial = calculadora.calcularSeguridadSocial(base);
        Dinero fondoPension    = calculadora.calcularFondoPension(base);

        return "=== COMPROBANTE DE PAGO ===" + "\n"
                + empleado.resumen() + " | Dirección: " + direccion.formatear() + "\n"
                + "Salario Base: "               + base.formatear()            + "\n"
                + "Horas Extras (" + horasExtras + "h): " + extras.formatear() + "\n"
                + "Salario Bruto: "              + bruto.formatear()           + "\n"
                + "Impuesto (" + porcentajeImpuesto + "%): -" + impuesto.formatear()              + "\n"
                + "Seguridad Social (4%): -"     + seguridadSocial.formatear() + "\n"
                + "Fondo Pensión (4%): -"        + fondoPension.formatear()    + "\n"
                + "SALARIO NETO: "               + neto.formatear()            + "\n"
                + datosBancarios.descripcionTransferencia()                    + "\n"
                + "=========================";
    }
}
