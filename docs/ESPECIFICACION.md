# Barttolo RingWorld — Pedido de desarrollo: reliquias e integración de recompensas

## Propósito

Desarrollar el mod independiente **Barttolo Reliquias** y actualizar **Barttolo RingWorld Misiones** para que la primera victoria de cada misión entregue una reliquia aleatoria, una sola vez por jugador. El plan completo contempla 23 recompensas de misiones y otras 3 por la primera victoria válida del jefe interdimensional: **26 reliquias ganadas en total por jugador que complete toda la aventura**.

## 1. Plataforma y alcance

- Minecraft Java 1.20.1, Forge 47.4.10.
- Servidor dedicado y clientes Forge.
- Todo registro y otorgamiento se valida y modifica en el servidor.
- Mantener el sistema de reliquias como mod separado (`modid: barttoloreliquias`) y la lógica de victoria en el mod Barttolo Misiones. Usar una API/evento o puente interno estable para comunicar las victorias, evitando dependencias circulares y lógica duplicada.
- No crear todavía el jefe interdimensional. Dejar definido un punto de integración que solo acepte una señal de victoria autenticada por el servidor.
- No implementar rangos, prefijos, GUI, tienda, recetas, generación natural, recompensas con R-Coins ni drops automáticos.
- Las 24 reliquias previstas para futuros asaltos, incursiones o eventos no forman parte de esta entrega automática. Su distribución se definirá después; los administradores podrán entregar reliquias mediante comandos.

## 2. Las cinco reliquias

Cada uso mejora permanentemente al jugador en un nivel, hasta 10 niveles por tipo:

| ID | Nombre | Efecto por uso | Máximo acumulado |
|---|---|---|---|
| `corazon_titan` | Corazón de Titán | +2 puntos de salud máxima (+1 corazón) | +20 salud (+10 corazones) |
| `piel_obsidiana` | Piel de Obsidiana | −1% de daño recibido elegible | −10% |
| `esencia_guerrero` | Esencia del Guerrero | +2% de daño cuerpo a cuerpo elegible | +20% |
| `sangre_fenix` | Sangre de Fénix | +5% a la tasa de regeneración natural | +50% |
| `voluntad_guardian` | Voluntad del Guardián | −5% a la duración de efectos negativos compatibles | −50% |

Las reliquias sin consumir son objetos transferibles: se pueden guardar, soltar, intercambiar y entregar administrativamente. No quedan vinculadas al UUID del primer propietario. La mejora ya consumida es personal, permanente e intransferible. No garantizar una distribución equilibrada de los tipos.

## 3. Reglas de consumo y efectos

- Clic derecho consume exactamente una unidad únicamente después de aplicar la mejora correctamente.
- Al llegar a 10/10, informar al jugador y no consumir el objeto.
- Impedir doble activación desde ambas manos, clics repetidos y duplicaciones por desincronización.
- Evitar mejoras gratuitas en creativo: consumir una unidad también en creativo o bloquear el uso con un mensaje claro.
- Titán: modificador estable e idempotente de salud máxima; no duplicarlo al reloguear, morir, cambiar dimensión o reiniciar. No sobrescribir modificadores de otros mods.
- Obsidiana: reducir una sola vez el daño elegible; excluir vacío, fuera del mundo, eliminaciones administrativas y fuentes de bypass definidas.
- Guerrero: aumentar solo golpes directos cuerpo a cuerpo del jugador; excluir proyectiles, explosiones, espinas, magia y daño ambiental. Documentar la interacción con críticos, encantamientos y cooldown.
- Fénix: acelerar la regeneración natural real sin ignorar hambre/saturación ni la gamerule `naturalRegeneration`; no curar cada tick ni alterar reglas globales.
- Guardián: reducir una sola vez la duración inicial de efectos perjudiciales compatibles; mínimo de un tick. Excluir efectos instantáneos, beneficiosos, permanentes o incompatibles. Preservar amplificador y demás propiedades.
- Si una mecánica no puede implementarse de forma segura en Forge 1.20.1, documentar el límite técnico y no afirmar que funciona.

## 4. Texturas y presentación

Crear cinco texturas originales en pixel art, preferentemente 32×32, con siluetas distinguibles en inventario, mano y cofres:

1. Corazón de Titán: escudo dorado con corazón rubí luminoso.
2. Piel de Obsidiana: escudo facetado oscuro, vetas violetas y núcleo protector.
3. Esencia del Guerrero: insignia carmesí y acero, espada y runas.
4. Sangre de Fénix: escudo ámbar con ave o pluma de fuego.
5. Voluntad del Guardián: emblema azul profundo y oro, ojo/escudo arcano.

Incluir nombres con colores propios y traducciones `es_es` y `en_us`. El tooltip en español debe mostrar ambientación breve, efecto por uso, progreso personal actual N/10, beneficio acumulado, aviso «Se consume al utilizarse» e instrucción de clic derecho. El progreso corresponde al jugador que observa el objeto, nunca al NBT del objeto ni a su propietario anterior. Sincronizar los cinco niveles desde el servidor al cliente al entrar, reaparecer, cambiar dimensión y tras cada activación o cambio administrativo. Si aún no llega la sincronización, mostrar «Sincronizando progreso…».

## 5. Recompensas de las 23 misiones

El mapa final contempla 23 misiones: 13 de Overworld, 5 de Nether y 5 de End. Cada una debe tener un ID permanente, único e independiente de su número visible o posición en la cadena. La primera finalización exitosa otorga exactamente una reliquia aleatoria al jugador que corresponda.

### Condiciones obligatorias

- Vincular el otorgamiento al evento real de finalización de Barttolo Misiones; no usar estadísticas o avances vanilla como única prueba.
- Recompensa individual por UUID. En una victoria de grupo, entregar a cada participante que el sistema de misiones marque como elegible, una sola vez por cada misión.
- El servidor registra misión reclamada y recompensa asignada de forma persistente. Morir, desconectarse, cambiar nombre/dimensión o reiniciar no restablece el registro.
- Repetir misiones sigue entregando las recompensas normales existentes, pero jamás una segunda reliquia por el mismo ID.
- Si el inventario está lleno o se corta la conexión, dejar la recompensa pendiente y entregarla de forma segura después; no perderla ni duplicarla.
- Eventos de victoria simultáneos, repetidos o reentrantes no pueden causar doble entrega.
- Mantener intactas las recompensas, el flujo, los comandos y el comportamiento actuales de las misiones.

## 6. Jefe interdimensional: integración preparada

No construir todavía el jefe ni definir sus características de combate. Preparar una API/evento de servidor para que el futuro sistema del jefe comunique una victoria verificada.

- Otorgar tres reliquias aleatorias a cada jugador elegible en su primera victoria válida.
- La recompensa es independiente de las 23 misiones y se registra por UUID con una clave permanente de encuentro/jefe.
- Las tres pueden ser del mismo tipo o diferentes.
- Repetir el encuentro no vuelve a otorgarlas.
- No aceptar como victoria la muerte de cualquier entidad parecida ni un comando o evento cliente no validado.
- Mantener una vía administrativa auditada para corregir o consultar el registro, con permisos de operador.

## 7. Persistencia y entrega segura

Persistir por UUID:

- Los niveles (0–10) de cada reliquia.
- Los IDs de misión cuya reliquia se reclamó.
- Las recompensas asignadas que aún estén pendientes de entrega.
- El estado de la recompensa del jefe final.
- Versión de esquema necesaria para migraciones.

Diseñar la concesión para no perder ni duplicar objetos ante guardados, reinicios, inventario lleno o desconexión. Guardar el registro antes o de forma atómica con la entrega, usar un estado pendiente recuperable y documentar el orden transaccional. La autoridad y las escrituras son siempre server-side.

## 8. Comandos

Comandos sugeridos, con Brigadier y autocompletado:

- `/reliquias` y `/reliquias estado`: cada jugador consulta sus propios niveles, efectos y límites.
- `/reliquias estado <jugador>`: operador nivel 2 o superior; admitir UUID/datos guardados de jugadores desconectados cuando sea posible.
- `/reliquias dar <jugador> <tipo> <cantidad>`: operador nivel 2 o superior; validar cantidad y manejar inventario lleno sin pérdida.
- `/reliquias quitar <jugador> <tipo> <niveles>`: operador nivel 2 o superior; modificar niveles persistentes y efectos al instante.
- `/reliquias reiniciar <jugador>`: operador nivel 2 o superior; confirmación explícita, registro en log y actualización de efectos.
- `/reliquias recompensas <jugador>`: consultar misiones reclamadas, pendientes y jefe.
- `/reliquias recompensa consultar <jugador> <id_mision>`.
- `/reliquias recompensa otorgar <jugador> <id_mision>`: respetar siempre el bloqueo único; las correcciones administrativas deben ser explícitas y quedar auditadas.

Los comandos normales no modifican progreso ni registros. Quitar o reiniciar una mejora no elimina objetos no consumidos.

## 9. Mensajes

Al reclamar una reliquia, mostrar un mensaje privado coloreado con misión y tipo obtenido, sin anunciar spoilers globalmente. En una repetición, indicar brevemente que la recompensa especial de esa misión ya fue reclamada. Al consumir, mostrar tipo, nuevo nivel y efecto total. Al alcanzar el límite, informar que el ítem no se consumió.

## 10. Compatibilidad y protección del mod actual

- Preservar las funciones existentes del mod Barttolo RingWorld Misiones y su economía/recompensas.
- No cambiar las 13 misiones actuales para introducir duplicación de lógica; emitir un evento o llamada interna en el punto real de victoria.
- Asegurar que el mod de reliquias pueda funcionar de forma independiente para sus objetos y comandos; la integración de misiones debe detectar la presencia del mod/API de forma segura.
- Mantener IDs públicos estables: `barttoloreliquias:corazon_titan`, `barttoloreliquias:piel_obsidiana`, `barttoloreliquias:esencia_guerrero`, `barttoloreliquias:sangre_fenix`, `barttoloreliquias:voluntad_guardian`.
- Probar servidor dedicado, cliente, dos jugadores con progreso distinto, permisos, dos victorias simultáneas, desconexiones y datos persistentes.

## 11. Insumos disponibles y límite de fuentes

Se adjuntó `Barttolo-RingWorld-MISIONES-2.6.13.jar`. Es un JAR compilado del mod de estructuras/misiones, con `barttolo_structures` versión `2.6.13-test1` y `ringworld_biomes` versión `2.6.13-test1`. Incluye código compilado y algunos archivos fuente de parches puntuales; **no contiene el proyecto Gradle ni las fuentes completas originales**.

Usar este JAR como referencia de compatibilidad y comportamiento. Para mantener y entregar fuentes completas del mod de Misiones, solicitar o recuperar el proyecto Gradle original en el espacio de trabajo correspondiente. Si solo se dispone del JAR, no presentar una reconstrucción/descompilación como fuentes originales ni prometer una integración completa hasta verificar que se puede compilar y conservar su comportamiento.

## 12. Criterios de aceptación y entregables

Aceptar cuando:

1. Los cinco ítems se registran, tienen texturas/idiomas/tooltip correctos y funcionan en servidor dedicado.
2. Cada efecto respeta sus límites, exclusiones y persistencia tras muerte, logout, reinicio y cambio dimensional.
3. Las 23 misiones del alcance otorgan una reliquia en la primera victoria elegible, sin duplicados y con entrega pendiente recuperable.
4. Las repeticiones mantienen las recompensas actuales y no entregan otra reliquia.
5. El hook de jefe admite tres reliquias por primera victoria validada, pero no crea el jefe.
6. Comandos y permisos funcionan, los registros son consultables y las acciones de mantenimiento quedan auditadas.
7. No hay pérdida ni duplicación al simular inventario lleno, desconexión, reinicio o evento repetido.
8. No se implementan rangos, tienda, recetas, R-Coins, GUI ni recompensas automáticas de las 24 fuentes futuras.

Entregar proyecto fuente Gradle completo y reproducible del mod de reliquias, JAR listo para instalar, texturas, idiomas, README de instalación/comandos/copias de seguridad y un informe que distinga las pruebas realmente ejecutadas de las pendientes. Entregar también JAR actualizado y fuentes de Misiones solo cuando se haya verificado la disponibilidad y compilación de sus fuentes completas.

**Prioridad:** conservar el funcionamiento del servidor, impedir la explotación por repetición y evitar que se pierdan mejoras o recompensas. **Las reliquias se conquistan, no se compran.**
