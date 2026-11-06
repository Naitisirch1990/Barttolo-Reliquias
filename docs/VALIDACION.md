# Validación de Barttolo Reliquias 0.1.0

Fecha: 6 de noviembre de 2026. Minecraft 1.20.1, Forge 47.4.10, Java 17.

## Resultados ejecutados

- Compilación local con Gradle 8.8: **BUILD SUCCESSFUL**. JAR reobfuscado por `reobfJar`; se comprobó que las llamadas vanilla usan nombres SRG de producción y que el JAR no contiene las clases GameTest.
- [GitHub Actions, ejecución 42282725936](https://github.com/Naitisirch1990/Barttolo-Reliquias/actions/runs/42282725936), commit `367e5768efd48835c027ba55aa31a725139f3aa9`: compilación correcta y **All 2 required tests passed** en un servidor de pruebas Forge real.
- `consumptionPersistenceRewards`: consumo real de cada uno de los cinco objetos hasta 10/10, conservación de la undécima unidad, salud idempotente, independencia entre jugadores, guardado/carga NBT y clonación. Primera misión, repetición, inventario lleno, entrega pendiente, reintento, catálogo de 23 misiones, tres objetos por jefe y bloqueo de su repetición. Restricción de comandos sin permiso.
- `combatEffectsAndRegeneration`: multiplicadores sobre eventos Forge reales de daño, exclusión de `generic_kill`, duración inicial y renovación de efectos perjudiciales, reaplicación del mismo objeto de efecto sin reducción doble, exclusión de efectos beneficiosos. Fénix cura 15 puntos en 100 ticks saturados a nivel 10; no cura con hambre insuficiente ni con `naturalRegeneration=false`.
- `tools/VerifyMissionBridge.java`: ejecutó con Nashorn el script real que se distribuye, contra el JAR exacto de Misiones suministrado. Se insertó una sola llamada de recompensa en la rama de victoria. El analizador ASM verificó la pila de bytecode; un objetivo incompatible fue rechazado.
- Texturas: cinco PNG RGBA originales de 32×32, modelos JSON y archivos de idioma presentes. Vista de los PNG inspeccionada visualmente en `reliquias-preview.png`.

SHA-256 del JAR compatible de Misiones:

```text
e78bd31848784b03c5d90af053c1d0e20c819ed367c773c808eadd21bd4a9ade
```

## Límites de lo comprobado

Las pruebas GameTest usan dos jugadores simulados de Forge en un servidor real; no sustituyen una sesión con dos clientes conectados. Se verificaron NBT y clonación, pero no se ejecutó un ciclo completo de cierre/reinicio con jugadores humanos ni un corte eléctrico durante la escritura. Inventario y registro se guardan juntos mediante PlayerDataStorage vanilla.

Queda pendiente revisar los tooltips y las texturas dentro del inventario de un cliente Minecraft, verificar el intercambio entre dos clientes y jugar una misión completa con el conjunto real de mods de RingWorld. La vista PNG entregada no es una captura de Minecraft. La integración binaria con Misiones pasó la prueba del transformador, pero la campaña completa con sus dependencias no se ejecutó aquí.

El JAR actual de Misiones contiene 13 misiones de Overworld. Esas 13 se conectan automáticamente. Los IDs y la API de las diez misiones futuras de Nether/End y del jefe están preparados, pero sus productores de victoria aún deben implementarse. No se crea el jefe.
