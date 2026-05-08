package com.universidad.cleancode.service;

import com.universidad.cleancode.model.DatosEmpleado;
import org.springframework.stereotype.Component;

// Responsabilidad única: validar datos de entrada del empleado antes de procesar pagos.
@Component
public class ValidadorEmpleados {

    public void validarDatosEmpleado(DatosEmpleado empleado) {
        // La validación estructural ya ocurre en el record; aquí van reglas de negocio adicionales.
        if (empleado == null) {
            throw new IllegalArgumentException("Los datos del empleado no pueden ser nulos");
        }
    }

    public void validarParametrosSalariales(double salarioBase, int horasExtras, double porcentajeImpuesto) {
        validarSalarioBase(salarioBase);
        validarHorasExtras(horasExtras);
        validarPorcentajeImpuesto(porcentajeImpuesto);
    }

    private void validarSalarioBase(double salarioBase) {
        if (salarioBase <= 0) {
            throw new IllegalArgumentException("El salario base debe ser un valor positivo");
        }
    }

    private void validarHorasExtras(int horasExtras) {
        if (horasExtras < 0) {
            throw new IllegalArgumentException("Las horas extras no pueden ser negativas");
        }
    }

    private void validarPorcentajeImpuesto(double porcentajeImpuesto) {
        if (porcentajeImpuesto < 0 || porcentajeImpuesto > 100) {
            throw new IllegalArgumentException("Porcentaje de impuesto fuera de rango: " + porcentajeImpuesto);
        }
    }
}
