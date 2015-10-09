package gg.uhc.uhc.modules;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gg.uhc.uhc.inventory.IconInventory;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ModuleRegistry {

    public static final String ADDON_INVENTORY_TITLE = ChatColor.DARK_PURPLE + "UHC Control Panel";
    public static final Pattern VALID_MODULE_NAME_REGEX = Pattern.compile("^\\w+$");

    protected final Plugin plugin;
    protected final PluginManager pluginManager;
    protected final ConfigurationSection config;

    protected final Map<String, Module> modules = Maps.newHashMap();
    protected final IconInventory addonInventory;

    public ModuleRegistry(Plugin plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
        this.config = config;

        this.addonInventory = new IconInventory(ADDON_INVENTORY_TITLE);
        registerEvents(addonInventory);
    }

    public IconInventory getInventory() {
        return addonInventory;
    }

    public void registerEvents(Listener listener) {
        pluginManager.registerEvents(listener, plugin);
    }

    public Set<Map.Entry<String, Module>> getModules() {
        return ImmutableSet.copyOf(modules.entrySet());
    }

    public Optional<Module> get(String id) {
        return Optional.fromNullable(modules.get(id.toLowerCase()));
    }

    public void register(Module module, String id) {
        Preconditions.checkArgument(VALID_MODULE_NAME_REGEX.matcher(id).matches(), "Module id may only contain alphanumberic characters and _, found `" + id + "`");

        // use all lower case in the map
        id = id.toLowerCase();

        // make sure it's a new key
        Preconditions.checkArgument(!modules.containsKey(id), "Module `" + id + "` is already registered");

        // initialize the plugin for config saving
        module.setPlugin(plugin);

        String sectionId = "modules." + id;

        if (!config.contains(sectionId)) {
            config.createSection(sectionId);
        }

        // attempt initiaization from config section
        try {
            module.initialize(config.getConfigurationSection(sectionId));
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            // dont add the module if it failed to load
            return;
        }

        // add the module to the map
        modules.put(id, module);

        // register events if required
        if (module instanceof Listener) {
            registerEvents((Listener) module);
        }

        // register the module's icon stack with the addon inventory
        addonInventory.registerNewIcon(module.getIconStack());
    }
}
