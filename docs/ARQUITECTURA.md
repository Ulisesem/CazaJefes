# Organización del proyecto

| Área | Componentes principales |
| --- | --- |
| Catálogo y presentación | BossCatalog, BossLore, BestiaryCategory, BestiaryMenu |
| Encuentros y etapas | BossManager, BossStages, BossParticipation, DamageLedger |
| Combate | BossTactics, CombatPhases, BossAbilities, BossAbilityCatalog, BossDisplacement |
| Daño y renacimiento | SafeDamage, BossDamageProgress, BossProtection, BossRebirth |
| Efectos | BurstVisuals, BossAuras, CombatVisuals, BossDeathVisuals |
| Botín y objetos | RelicItems, LootRules, LootScatter, LootSpiral, DeathCompletion |
| Administración | CazaJefesCommand, BossSpawnpoints, AdminPermissions, EquipmentCommand |
| Persistencia y migración | ConfigMigration, CurrencyMigration, BossCatalog.get |
| Recetas y mercado | RecipeSettings, RecipeAlchemy, RecipeMenu, MarketMenu |

Los IDs canónicos se obtienen del nombre actual. Definition ya no guarda un ID de migración y BossCatalog.get solo acepta IDs actuales. Los cambios de entidades e inventarios se ejecutan desde las tareas del servidor. Los temporizadores y efectos se cancelan al desactivar el plugin.

Las pruebas unitarias están en src/test/java. docs/validation contiene la última ejecución de Maven y la inspección del JAR; no constituye una prueba de juego en un servidor Paper.

Componentes de 1.15.0: BossIntegrity/SlimeHealth (ciclo nativo y vida), EncounterSafety (espacio y refugios), BurstCadence/AbyssalGaze (coordinación), MobCombos/SpellVisuals/BossEffects (combos y efectos), CosmeticFX (presupuesto compartido), BossStatistics (victorias y exportación), RareAppearance (presentación de propiedades).

Forja PvP 1.16.0: DuelCatalog define las ocho piezas y reglas puras; DuelRelics registra materiales/recetas y procesa disparos y daños aceptados. RaresMenu añade dos categorías y enlaces entre recetas asociadas. Los poderes no comparten el temporizador de debuffs de bosses.
