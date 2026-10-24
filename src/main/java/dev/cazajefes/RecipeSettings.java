package dev.cazajefes;

import java.io.File;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

/** Overrides retain existing exact custom ingredients unless explicitly changed. */
final class RecipeSettings {
    private final JavaPlugin plugin;
    private YamlConfiguration config;
    RecipeSettings(JavaPlugin plugin) { this.plugin = plugin; reload(); }
    void reload() {
        File file = new File(plugin.getDataFolder(), "recipes.yml");
        if (!file.exists()) plugin.saveResource("recipes.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }
    boolean apply(String id, String[] shape, Map<Character, RecipeChoice> choices) {
        ConfigurationSection section = config.getConfigurationSection("recipes." + id);
        if (section == null) return true;
        if (!section.getBoolean("enabled", true)) return false;
        List<String> rows = section.getStringList("shape");
        if (!rows.isEmpty()) {
            if (rows.size() != 3 || rows.stream().anyMatch(s -> s.length() != 3))
                throw new IllegalArgumentException(id + ": shape debe ser 3 filas de 3 caracteres");
            for (int i = 0; i < 3; i++) shape[i] = rows.get(i);
        }
        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        if (ingredients != null) for (String key : ingredients.getKeys(false)) {
            if (key.length() != 1 || key.charAt(0) == ' ') throw new IllegalArgumentException(id + ": símbolo inválido");
            Material material = Material.matchMaterial(ingredients.getString(key, ""));
            if (material == null || !material.isItem() || material.isAir()) throw new IllegalArgumentException(id + ": material inválido " + key);
            choices.put(key.charAt(0), new RecipeChoice.MaterialChoice(material));
        }
        Set<Character> used = new HashSet<>();
        for (String row : shape) for (char c : row.toCharArray()) if (c != ' ') {
            if (!choices.containsKey(c)) throw new IllegalArgumentException(id + ": ingrediente desconocido " + c);
            used.add(c);
        }
        if (used.isEmpty()) throw new IllegalArgumentException(id + ": receta vacía");
        choices.keySet().retainAll(used);
        return true;
    }
}
