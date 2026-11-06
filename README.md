# Barttolo Reliquias

Minecraft Java **1.20.1**, Forge **47.4.10**, Java **17**. Mod independiente para servidor y clientes Forge, con cinco reliquias transferibles que se consumen para aplicar mejoras permanentes al jugador. Proyecto nuevo desarrollado a partir de la especificación de RingWorld.

![Las cinco reliquias originales](docs/reliquias-preview.png)

| Reliquia | Por uso | Tope de diez usos |
|---|---|---|
| Corazón de Titán | +1 corazón | +10 corazones |
| Piel de Obsidiana | −1% daño recibido elegible | −10% |
| Esencia del Guerrero | +2% daño cuerpo a cuerpo | +20% |
| Sangre de Fénix | +5% tasa de regeneración natural | +50% |
| Voluntad del Guardián | −5% duración de efectos negativos compatibles | −50% |

Las cinco reliquias aparecen en la pestaña **Ingredientes** del inventario creativo y en su buscador.

Clic derecho aplica un nivel y consume exactamente una unidad, también en creativo. A 10/10 conserva el objeto. Los tooltips muestran el progreso del jugador que mira el objeto, sincronizado por el servidor. Las reliquias sin consumir se guardan e intercambian libremente.

## Instalación y compilación

Instalar el JAR de `build/libs/` en `mods/` tanto del servidor como de los clientes, junto con Forge 47.4.10. Conservar una copia del mundo antes de actualizar. Este mod no necesita RingWorld, Misiones, Lootr ni Renris Slot para sus propios objetos y comandos.

Con un JDK 17 y acceso a los repositorios Maven:

```sh
./gradlew build
./gradlew runGameTestServer -x downloadAssets
```

GitHub Actions ejecuta compilación y GameTests con Java 17 y Gradle 8.8, y conserva el JAR y el registro como artefactos. Consultar [VALIDACION.md](docs/VALIDACION.md) para distinguir lo ejecutado de lo pendiente.

## Misiones y jefe futuro

La integración automática reconoce la victoria real y elegible de las **13 misiones actuales de Overworld** del JAR suministrado `Barttolo-RingWorld-MISIONES-2.6.13-Chat-Color-y-Numero(1).jar`. Instalar ese mismo JAR y sus dependencias habituales junto con Reliquias. Un transformador opcional de Forge añade una llamada al punto de victoria; no se reemplaza el archivo de Misiones. Si el patrón esperado no coincide, se informa una incompatibilidad al arrancar.

Cada primera victoria entrega una reliquia aleatoria por UUID. Las repeticiones mantienen las recompensas habituales y no entregan otra reliquia. Las misiones futuras de Nether y End cuentan con IDs y API, pero todavía necesitan que sus sistemas de victoria llamen esa API: no se crean esas diez misiones mediante este mod.

IDs estables: `overworld_01`–`overworld_13`, `nether_01`–`nether_05`, `end_01`–`end_05`. El plan de campaña completo es **23 reliquias de misiones más 3 del jefe = 26**. La API `Rewards.bossVictory(ServerPlayer)` concede las tres del jefe una sola vez y solo debe invocarse después de validar la victoria y sus participantes en el servidor. El jefe aún no se crea. Las otras 24 reliquias previstas para asaltos y eventos no tienen distribución automática en esta versión.

## Comandos

Todos pueden usar `/reliquias` y `/reliquias estado` para ver sus niveles.

Los siguientes requieren operador de nivel 2 o superior y aceptan jugadores conectados:

```text
/reliquias estado <jugador>
/reliquias dar <jugador> <tipo> <cantidad>
/reliquias quitar <jugador> <tipo> <niveles>
/reliquias establecer <jugador> <tipo> <niveles>
/reliquias reiniciar <jugador> confirmar
/reliquias recompensas <jugador>
/reliquias recompensa consultar <jugador> <id_mision>
/reliquias recompensa otorgar <jugador> <id_mision>
```

Tipos: `corazon_titan`, `piel_obsidiana`, `esencia_guerrero`, `sangre_fenix`, `voluntad_guardian`. Cantidad de objetos: 1–640; niveles: 0–10. `dar` comprueba capacidad antes de entregar y rechaza el pedido completo si no cabe. El comando de recompensa respeta el registro único. Reiniciar solo restaura las mejoras a cero; conserva las recompensas reclamadas y requiere la palabra `confirmar`. Las modificaciones administrativas quedan registradas en el log.

## Persistencia y entrega

Los niveles, las recompensas asignadas y los objetos pendientes residen en el NBT persistente del jugador y se copian al reaparecer. Inventario y registro se guardan juntos en el archivo vanilla `playerdata/<UUID>.dat`. La entrega fuerza el guardado del jugador; un inventario lleno conserva los objetos pendientes, que se reintentan al entrar y cada cinco segundos. Respaldar `playerdata/` con el mundo detenido. Restaurar archivos antiguos también restaura sus inventarios e historiales: no mezclar copias de fechas distintas.

Los objetos ya entregados y el historial se guardan en una misma instantánea del jugador; no se usa un registro externo con guardado independiente. Fallos físicos del disco, ediciones externas y otros mods que cambien el guardado necesitan revisión propia y no quedan cubiertos por esta garantía.

## Mecánicas y compatibilidad

Titán usa un modificador de salud de UUID fijo y no cambia la salud base. Obsidiana y Guerrero ajustan una vez `LivingHurtEvent`: conservan los cálculos vanilla de daño previo, críticos y encantamientos, y el procesamiento posterior de daño. Obsidiana excluye vacío, `generic_kill` y fuentes que atraviesen invulnerabilidad. Guerrero solo acepta un golpe directo `player_attack`.

Fénix acelera el reloj de regeneración de FoodData conservando la curación y el coste de hambre/saturación vanilla; no cura por separado. Respeta hambre y `naturalRegeneration`. Su ajuste del reloj puede interactuar con otros mods que cambien FoodData, por lo que deben probarse juntos.

Guardián reduce la duración antes de combinar el efecto entrante, una vez por instancia. No altera efectos instantáneos, beneficiosos, neutros ni infinitos. El tag de efectos `barttoloreliquias:duration_immune` permite excluir efectos de otros mods mediante datapack.

No hay recetas, botín natural, GUI, rangos ni compra con R-Coins. El administrador controla la distribución adicional de los objetos.
