# MercaSENA · Patrón Adapter (Estructural)

**Dominio:** Integración con una pasarela de pagos antigua

## Contexto

MercaSENA define su propia interfaz de pagos, `ProcesadorPagos`, que recibe una referencia y un monto en **pesos** (`BigDecimal`). El banco aliado, en cambio, entrega una librería antigua, `PasarelaLegacy`, que **no se puede modificar**. Esa librería recibe una orden en **XML** con el monto en **centavos** y responde con un código numérico. El patrón **Adapter** traduce entre los dos mundos.

Clases principales:

- `ProcesadorPagos`: interfaz objetivo (*target*) que usa MercaSENA.
- `PasarelaLegacy`: clase adaptada (*adaptee*). Es código de un tercero y **no se modifica**.
- `AdaptadorPasarelaLegacy`: el adaptador.
- `ResultadoPago` y `EstadoPago`: respuesta en el idioma de MercaSENA.

## Reglas de negocio

1. **Conversión exacta a centavos.** El monto en pesos se convierte a centavos sin perder decimales, redondeando *half up* si viene con más de dos decimales. Ejemplo: `$1.999,99` equivale a `199999` centavos.
2. **Traducción de códigos.** `0` es `APROBADO`, `1` es `RECHAZADO_FONDOS` y cualquier otro código es `ERROR`.
3. La referencia no puede ser nula ni estar en blanco, y el monto debe ser mayor que 0. Si no se cumple, se lanza `IllegalArgumentException`.
4. **Seguridad.** La referencia la escribe el usuario y puede traer caracteres especiales de XML (`<`, `>`, `&`, `"`, `'`). Esos caracteres deben **escaparse**. Nunca debe ser posible alterar la estructura de la orden XML, por ejemplo inyectando otra etiqueta `<monto>`.
5. La pasarela rechaza por fondos los pagos superiores a $5.000.000.

## Tu misión

Este proyecto implementa el patrón **Adapter** dentro de la tienda en línea **MercaSENA**. El código *compila*, pero tiene **errores sembrados**: algunos los detectan las pruebas que ya existen y otros **no**. Tu trabajo es encontrarlos todos, demostrarlos con pruebas y corregirlos con ayuda de la IA, sin perder el control técnico.

> La fuente de verdad son las **reglas de negocio** de este README. La IA no las conoce si no se las das.

## Protocolo de trabajo (obligatorio)

1. **Prepara el repositorio.** Crea un repositorio en GitHub con este proyecto y haz el primer commit sin cambios: `git commit -m "chore: estado inicial"`.
2. **Predice sin IA (20 min).** Lee este README y el código. Escribe tus hipótesis en `BITACORA_IA.md`, sección 1.
3. **Ejecuta las pruebas.** Corre `mvn test` o las pruebas desde IntelliJ / Eclipse. Anota cuáles fallan.
4. **Demuestra cada error con una prueba.** Por cada error que encuentres, incluidos los que las pruebas actuales no detectan, escribe primero una prueba que **falle**. Haz commit: `test: demuestra <error>`.
5. **Consulta al jurado de IAs.** Envía el mismo prompt a ChatGPT, Gemini y DeepSeek. Usa el prompt de 5 piezas: contexto, tarea, restricciones, formato y criterio de verificación. Registra en la bitácora en qué coincidieron y en qué no.
6. **Corrige.** Aplica la corrección que **tú** entiendes y puedes explicar. La prueba debe pasar. Haz commit: `fix: corrige <error>`.
7. **Refactoriza con IA.** Pide a la IA una mejora de legibilidad que no cambie el comportamiento. Todas las pruebas deben seguir en verde. Haz commit: `refactor: ...`.
8. **Revisa la seguridad.** Pregúntate qué podría hacer un usuario malicioso con este código y anota tu respuesta en la bitácora.
9. **Prepara la defensa.** El instructor te pedirá explicar, **sin IA**, cualquier línea que hayas cambiado.

## Cómo ejecutar

```bash
mvn test
```

Requisitos: JDK 17 o superior y Maven 3.9 o superior, o el Maven que trae IntelliJ IDEA o Eclipse.

## Qué NO hacer

- No borres ni debilites pruebas para que pasen.
- No pegues el proyecto completo en una IA sin explicarle las reglas de negocio.
- No aceptes una corrección que no sabes explicar.

## Referencia del patrón

- https://refactoring.guru/es/design-patterns/adapter
