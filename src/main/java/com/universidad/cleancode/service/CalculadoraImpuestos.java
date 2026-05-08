package com.universidad.cleancode.service;

import com.universidad.cleancode.model.Dinero;
import org.springframework.stereotype.Component;

// Centraliza toda la lógica fiscal. Si cambian las reglas tributarias, solo se toca aquí.
@Component
public class CalculadoraImpuestos {

    private static final double VALOR_HORA_EXTRA        = 15_000.0;
    private static final double LIMITE_HORAS_NORMALES   = 12;
    private static final double FACTOR_HORA_DOBLE       = 2.0;

    private static final double TASA_SEGURIDAD_SOCIAL   = 0.04;
    private static final double TASA_FONDO_PENSION       = 0.04;

    private static final double UMBRAL_SALARIO_ALTO      = 5_000_000.0;
    private static final double RECARGO_IMPUESTO_ALTO    = 1.10;
    private static final double DESCUENTO_IMPUESTO_BAJO  = 0.90;
    private static final double TASA_IMPUESTO_RECARGO    = 30.0;

    public Dinero calcularTotalHorasExtras(int horasExtras) {
        if (horasExtras <= 0) return Dinero.cero();

        if (horasExtras <= LIMITE_HORAS_NORMALES) {
            return new Dinero(horasExtras * VALOR_HORA_EXTRA);
        }

        double pagoNormales = LIMITE_HORAS_NORMALES * VALOR_HORA_EXTRA;
        double pagoDobles   = (horasExtras - LIMITE_HORAS_NORMALES) * VALOR_HORA_EXTRA * FACTOR_HORA_DOBLE;
        return new Dinero(pagoNormales + pagoDobles);
    }

    public Dinero aplicarDeducciones(Dinero salarioBruto, double porcentajeImpuesto) {
        Dinero impuesto        = calcularImpuesto(salarioBruto, porcentajeImpuesto);
        Dinero seguridadSocial = salarioBruto.multiplicar(TASA_SEGURIDAD_SOCIAL);
        Dinero fondoPension    = salarioBruto.multiplicar(TASA_FONDO_PENSION);
        return impuesto.sumar(seguridadSocial).sumar(fondoPension);
    }

    public Dinero calcularImpuesto(Dinero salarioBruto, double porcentajeImpuesto) {
        double tasaBase = porcentajeImpuesto / 100.0;

        if (salarioBruto.monto() > UMBRAL_SALARIO_ALTO) {
            return aplicarImpuestoSalarioAlto(salarioBruto, tasaBase, porcentajeImpuesto);
        }
        return salarioBruto.multiplicar(tasaBase * DESCUENTO_IMPUESTO_BAJO);
    }

    private Dinero aplicarImpuestoSalarioAlto(Dinero salarioBruto, double tasaBase, double porcentajeImpuesto) {
        if (porcentajeImpuesto > TASA_IMPUESTO_RECARGO) {
            return salarioBruto.multiplicar(tasaBase * RECARGO_IMPUESTO_ALTO);
        }
        return salarioBruto.multiplicar(tasaBase);
    }

    public Dinero calcularSeguridadSocial(Dinero salarioBase) {
        return salarioBase.multiplicar(TASA_SEGURIDAD_SOCIAL);
    }

    public Dinero calcularFondoPension(Dinero salarioBase) {
        return salarioBase.multiplicar(TASA_FONDO_PENSION);
    }
}
