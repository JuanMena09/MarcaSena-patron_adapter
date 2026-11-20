# Entrega y pasos finales — MercaSENA Patrón Adapter

## Estado del trabajo

- Proyecto original conservado en el commit inicial.
- Se añadieron pruebas de estados, conversión, XML, límites y validaciones.
- Se corrigieron estados, conversión de montos y escape de XML.
- Se refactorizó el adaptador para separar conversión, construcción del XML y escape.
- La bitácora está completada con los resultados que se comprobaron y marca explícitamente las consultas externas que faltan.

## Pruebas

El entorno de preparación no tiene Maven instalado, así que no se pudo ejecutar la suite JUnit completa aquí. Sí se compiló el código Java de producción y se ejecutó una comprobación manual que verificó: estado aprobado, conversión de 1999.99 a 199999 centavos, redondeo 1.995 a 200, escape de intento de inyección XML, preservación del monto real y rechazo de un pago alto.

**Antes de entregar, ejecuta Maven en IntelliJ:** panel Maven → Lifecycle → `test`. Guarda captura con todos los tests en verde y anota el resultado real en la bitácora.

## Consultar Gemini y DeepSeek

Usa el prompt común de la sección 3 de `BITACORA_IA.md` en las dos herramientas. Pega las respuestas reales en la bitácora; no se inventaron.

## Abrir y conservar el historial

El ZIP contiene la carpeta del repositorio y `.git` para conservar los commits. Extrae el ZIP y abre la carpeta `MercaSENA-Patron-Adapter` en IntelliJ.

## Subir a GitHub (si el repositorio remoto ya está creado)

Desde la terminal dentro de la carpeta del proyecto:

```bash
git remote add origin URL_DE_TU_REPOSITORIO
git branch -M main
git push -u origin main
```

Si `origin` ya existe, revisa primero:

```bash
git remote -v
```

No publiques credenciales ni tokens en la bitácora.
