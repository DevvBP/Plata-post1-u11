# Laboratorio: Refactorizacion Avanzada y Clean Code
### Unidad 11 - Post-Contenido 1 - Ingenieria de Software

---

## Descripcion

Laboratorio sobre tecnicas de refactorizacion aplicadas a un servicio de nomina construido con code smells intencionales para su posterior analisis con SonarQube.

Problemas iniciales documentados:
- Large Class: una sola clase concentraba validaciones, calculos, formateo y logica bancaria
- Long Method: metodo `procesarPagoEmpleado` de 100+ lineas con multiples responsabilidades mezcladas
- Primitive Obsession: firma del metodo con 10 parametros primitivos sin cohesion

---

## Analisis Comparativo de Metricas SonarQube

| Metrica                        | ANTES (Commit 1)             | DESPUES (Commit 2)            | Diferencia     |
|--------------------------------|------------------------------|-------------------------------|----------------|
| Clases de produccion           | 2                            | 8                             | +300%          |
| Complejidad cognitiva          | Alta (metodo unico ~100 lin) | Baja (metodos de 5-10 lin)    | Reduccion ~80% |
| Complejidad ciclomatica        | ~12 en un solo metodo        | 3 o menos por metodo          | Reduccion ~75% |
| Code Smells detectados         | 4-6 (Long Method, Prim. Obs.)| 0-1                           | Reduccion ~90% |
| Long Methods                   | 1                            | 0                             | Eliminado      |
| Parametros por metodo          | 10 primitivos                | 3 Value Objects               | Reduccion 70%  |
| Tests unitarios                | 6                            | 11                            | +83%           |
| Cobertura estimada             | ~60%                         | ~85%                          | Mejora notable |
| Responsabilidad por clase      | 1 clase = todo               | 1 clase = 1 responsabilidad   | SRP cumplido   |

Capturas del dashboard disponibles en `docs/sonar_antes.png` y `docs/sonar_despues.png`.

---

## Tecnicas de Refactorizacion Aplicadas

### Extract Class
Se separaron responsabilidades del servicio original en clases dedicadas:

| Clase                  | Responsabilidad                                          |
|------------------------|----------------------------------------------------------|
| `CalculadoraImpuestos` | Logica fiscal: horas extras, impuestos y deducciones     |
| `ValidadorEmpleados`   | Validacion de datos de entrada previo al procesamiento   |

El servicio principal quedo como orquestador de menos de 50 lineas.

### Extract Method
El metodo largo se dividio en metodos privados con nombres autodescriptivos:

```java
// Antes: bloque monolitico de 100+ lineas
public String procesarPagoEmpleado(String nombre, String doc, ...) { ... }

// Despues: orquestacion clara con intencion explicita
private void validar(...)                { ... }
private Dinero calcularBruto(...)        { ... }
private String construirComprobante(...) { ... }
```

### Value Objects (Introduce Parameter Object)
Los 10 parametros primitivos se agruparon en records inmutables de Java 17:

| Record           | Reemplaza                                            |
|------------------|------------------------------------------------------|
| `DatosEmpleado`  | `String nombre, String documento`                    |
| `Direccion`      | `String calle, String ciudad, String codigoPostal`   |
| `DatosBancarios` | `String numeroCuenta, String banco`                  |
| `Dinero`         | `double salarioBase` y demas montos del proceso      |

Cada record encapsula su validacion interna y su logica de presentacion.

---

## Estructura del Proyecto (estado final)

```
src/main/java/com/universidad/cleancode/
├── App.java
├── model/
│   ├── Direccion.java
│   ├── DatosBancarios.java
│   ├── DatosEmpleado.java
│   └── Dinero.java
└── service/
    ├── ProcesadorSalarialService.java
    ├── CalculadoraImpuestos.java
    └── ValidadorEmpleados.java
```

---

## Historial de Commits

| Commit | Descripcion |
|--------|-------------|
| `feat: crear servicio inicial con code smells (Large Class y Long Method)` | Codigo con problemas para analisis inicial |
| `refactor: aplicar Extract Method, Extract Class y Value Objects para eliminar Bloaters` | Refactorizacion completa |
| `docs: agregar analisis comparativo de metricas y capturas de SonarQube` | Documentacion del proceso |

---

## Ejecucion Local

Requisitos: Java 17+, Maven 3.8+, SonarQube corriendo en `localhost:9000`.

```bash
# Compilar y correr tests
mvn clean test

# Analisis con SonarQube
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin123
```

---

## Principios Aplicados

- SRP: cada clase tiene una sola razon de cambio
- DRY: logica de formateo y validacion centralizada en los Value Objects
- Inmutabilidad: todos los Value Objects son `record` de Java 17
- Inyeccion por constructor en el servicio principal

---

*DevvBP - Ingenieria de Software - 2026*
