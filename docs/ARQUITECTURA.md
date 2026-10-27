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

Los IDs canónicos se obtienen del nombre actual. migrationId y los alias de BossCatalog.get se limitan a reconocer datos previos; la lógica de combate usa Definition.id(). Los cambios de entidades e inventarios se ejecutan desde las tareas del servidor. Los temporizadores y efectos se cancelan al desactivar el plugin.

Las pruebas unitarias están en src/test/java. docs/validation contiene la última ejecución de Maven y la inspección del JAR; no constituye una prueba de juego en un servidor Paper.
