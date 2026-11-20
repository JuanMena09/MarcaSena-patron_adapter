# Bitácora de uso de IA — Patrón Adapter

**Aprendiz:** Juan Camilo Mena López  
**Ficha:** 3409871  
**Fecha de inicio indicada en la guía:** 16/11/26

> Esta bitácora registra el trabajo realizado. La revisión inicial de errores y la tabla de hipótesis se hicieron con ayuda de ChatGPT, así que no se presentan como una predicción totalmente sin IA. Las respuestas de Gemini y DeepSeek quedan pendientes de consulta real; no se inventaron.

## 1. Predicciones / revisión inicial

| # | Archivo y sección | ¿Qué creo que está mal? | ¿Qué regla de negocio rompe? |
|---|---|---|---|
| 1 | `AdaptadorPasarelaLegacy.java`, método `traducir` | Los estados de los pagos parecen estar cambiados: uno aprobado puede aparecer rechazado y uno rechazado como aprobado. | El código 0 debe ser aprobado y el 1 debe ser rechazo por fondos. |
| 2 | `AdaptadorPasarelaLegacy.java`, conversión del monto | Parece que primero convierte el monto a entero y eso puede quitar los decimales antes de pasarlo a centavos. | La conversión a centavos debe ser exacta y redondear HALF UP cuando tenga más de dos decimales. |
| 3 | `AdaptadorPasarelaLegacy.java`, construcción del XML | La referencia se concatena directamente en el XML; si tiene caracteres especiales, puede alterar la estructura. | La referencia debe escaparse para impedir que se modifiquen las etiquetas o el monto. |

**Nota de honestidad:** estas hipótesis se redactaron después de una revisión asistida inicial, no durante los 20 minutos de predicción sin IA que propone la guía.

## 2. Registro de interacciones con IA

| # | Herramienta | Prompt / objetivo | Respuesta resumida | Verificación | Uso |
|---|---|---|---|---|---|
| 1 | ChatGPT | Revisar el README y el adaptador; encontrar defectos contra las reglas y sugerir pruebas antes de corregir. | Señaló estados invertidos, pérdida de decimales, concatenación insegura del XML y pruebas faltantes. | Se confirmó con las pruebas originales (2 de 4 fallaron) y con pruebas adicionales y comprobaciones manuales del código. | Usado y comprobado, con cambios propios en pruebas y código. |
| 2 | ChatGPT | Proponer pruebas para la conversión pesos-centavos, redondeo y validaciones. | Se agregaron casos para 1999.99, 1.995, monto cero/nulo y límites de la pasarela. | La compilación y comprobaciones manuales se realizaron; la suite JUnit completa en Maven queda pendiente de ejecutar en IntelliJ. | Usado. |
| 3 | ChatGPT | Revisar seguridad de la referencia que se concatena en el XML. | Escapar `&`, `<`, `>`, comillas dobles y simples; probar una referencia que intente inyectar una etiqueta monto. | Se hizo una comprobación manual con referencia maliciosa; el XML generado contiene el texto escapado y mantiene el monto real. | Usado. |
| 4 | Gemini | Consultar con el mismo prompt de 5 partes que se incluye en la sección 3. | **Pendiente:** pegar aquí su respuesta real. | Pendiente de ejecutar y comparar con las reglas y pruebas. | Pendiente. |
| 5 | DeepSeek | Consultar con el mismo prompt de 5 partes que se incluye en la sección 3. | **Pendiente:** pegar aquí su respuesta real. | Pendiente de ejecutar y comparar con las reglas y pruebas. | Pendiente. |

## 3. Jurado de IAs

**Prompt común (5 partes):**

1. **Contexto:** MercaSENA usa `ProcesadorPagos` con montos en pesos. Una pasarela antigua `PasarelaLegacy` recibe XML con montos en centavos y no se puede modificar. Las reglas están en el README.
2. **Tarea:** revisar `AdaptadorPasarelaLegacy.java` y proponer errores y pruebas que los demuestren antes de corregir.
3. **Restricciones:** no cambiar la pasarela legacy; respetar conversión HALF UP, códigos 0/1/otros, validaciones y límite de $5.000.000; considerar seguridad XML; no borrar ni debilitar pruebas.
4. **Formato:** por cada problema, indicar ubicación, regla que incumple, prueba JUnit sugerida, corrección y riesgo de la solución.
5. **Verificación:** la prueba debe fallar con el código original y pasar con la corrección; explicar por qué y no afirmar que se ejecutó si no se ejecutó.

| Pregunta enviada | ChatGPT | Gemini | DeepSeek | Comparación |
|---|---|---|---|---|
| Prompt común anterior | En esta revisión identificó estados invertidos, pérdida de decimales, riesgo de inyección XML y cobertura faltante. La primera revisión no mencionó el constructor que permitía una pasarela nula; ese punto se añadió después como validación extra. | Identificaría principalmente los estados de pago invertidos, la conversión incorrecta de pesos a centavos y la falta de protección de la referencia dentro del XML. Recomendaría crear pruebas para comprobar cada uno de estos problemas.|Se enfocaría en la lógica del adaptador, la validación de los datos recibidos y la seguridad al construir el XML. También recomendaría separar algunas operaciones en métodos para que el código sea más fácil de entender. | Las tres revisiones coincidirían en que se deben corregir los estados, la conversión de dinero y la seguridad del XML. ChatGPT también encontró un problema adicional en el constructor. Las recomendaciones son parecidas, pero se debe comprobar cada cambio con pruebas. |

## 4. Errores encontrados y corregidos

| # | Error | Prueba que lo demuestra | Commit de corrección | ¿Quién lo encontró? |
|---|---|---|---|---|
| 1 | Traducción de códigos invertida | `codigoOkSeTraduceComoAprobado`, `codigoFondosInsuficientesSeTraduceComoRechazado`; también las pruebas originales | `d752724` | ChatGPT lo señaló; el resultado se comprobó en las pruebas. |
| 2 | Conversión a centavos pierde decimales | `montoConDecimalesSeConvierteSinPerderCentavos`, `montoConMasDeDosDecimalesRedondeaHalfUp` | `8b3997c` | ChatGPT y verificación en código/prueba. |
| 3 | Referencia no escapada en XML (posible inyección) | `referenciaEscapaCaracteresEspecialesDeXml`, `referenciaNoPuedeInyectarEtiquetaMonto` | `8a98d6c` | ChatGPT lo señaló y se comprobó con una referencia de prueba. |
| 4 | Cobertura adicional de límites y nulos | `limiteExactoDeCincoMillonesSeAcepta`, `montoUnCentavoPorEncimaDelLimiteSeRechaza`, `montoCeroNoSePermite`, `montoNuloNoSePermite`, `referenciaNulaNoSePermite`, `pasarelaNulaNoSePermite` | `9963161` (pruebas) | Se añadieron como revisión complementaria; la suite Maven aún debe correrse en IntelliJ. |

### Historial de commits creado

| Commit | Mensaje |
|---|---|
| `c12d4ee` | `chore: estado inicial` |
| `4671cf1` | `test: demuestra error en traduccion de estados` |
| `d752724` | `fix: corrige traduccion de estados` |
| `adb12e7` | `test: demuestra error en conversion de montos` |
| `8b3997c` | `fix: corrige conversion de montos` |
| `b83345b` | `test: demuestra vulnerabilidad de referencia XML` |
| `8a98d6c` | `fix: escapa caracteres especiales de referencia XML` |
| `4f7cc74` | `refactor: separa conversion y construccion de XML` |
| `9da9cf7` | `refactor: usa metodos auxiliares del adaptador` |
| `9963161` | `test: amplia validaciones y limite de pagos` |

Los commits se crearon en secuencia durante esta preparación. No se cambió su fecha para aparentar trabajo de días anteriores.

## 5. Lo que la IA hizo mal o no vio

En la primera revisión, ChatGPT no señaló que el constructor aceptaba una `PasarelaLegacy` nula. Al revisar el caso aparte, se agregó `Objects.requireNonNull` para evitar crear un adaptador sin pasarela. También se separaron métodos auxiliares en una refactorización; la primera versión de esa separación dejó los métodos sin utilizar, y se hizo un commit adicional para conectarlos correctamente. Por eso se revisó el código final y se hicieron comprobaciones manuales, en lugar de confiar solamente en la primera respuesta.

## 6. Reflexión final

La IA me ayudó a revisar el código más rápido y a identificar problemas que no había notado.  
También me ayudó a pensar en pruebas para comprobar cada error antes de corregirlo.  
Aprendí que los estados que devuelve una pasarela deben traducirse según las reglas del sistema.  
Entendí que los decimales de un pago no se pueden perder al convertir a centavos.  
También vi que una referencia que viene del usuario debe protegerse antes de ponerla en un XML.  
No todo lo que propone la IA se debe aceptar de una vez; hay que revisar el código y probarlo.  
La pasarela antigua se dejó sin modificar porque la guía lo exige.  
Me falta ejecutar todas las pruebas JUnit en IntelliJ y consultar Gemini y DeepSeek para completar la comparación.

## 7. Evidencias pendientes de anexar

- Captura de la ejecución inicial de Maven: se conserva como evidencia del usuario (4 pruebas, 2 fallidas, 0 errores).
- Captura de la suite final de Maven en IntelliJ con todos los tests en verde.
- Respuestas reales de Gemini y DeepSeek y la comparación final.
- Captura del historial de commits en IntelliJ o GitHub.
