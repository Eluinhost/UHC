/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.ModuleRegistry
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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

    public boolean register(Module module, String id) {
        Preconditions.checkArgument(VALID_MODULE_NAME_REGEX.matcher(id).matches(), "Module id may only contain alphanumberic characters and _, found `" + id + "`");
        Preconditions.checkArgument(id.length() <= 22, "Module names can only be 12 characters at most");

        // use all lower case in the map
        id = id.toLowerCase();

        // make sure it's a new key
        Preconditions.checkArgument(!modules.containsKey(id), "Module `" + id + "` is already registered");

        // initialize the plugin for config saving
        module.setPlugin(plugin);

        // set the module ID
        module.setId(id);

        String sectionId = "modules." + id;

        if (!config.contains(sectionId)) {
            config.createSection(sectionId);
        }

        ConfigurationSection section = config.getConfigurationSection(sectionId);

        // set load parameter if it doesn't exist
        if (!section.contains("load")) {
            section.set("load", true);
        }

        // if it's configured to not load then don't load it
        if (!section.getBoolean("load")) {
            return false;
        }

        // attempt initiaization from config section
        try {
            module.initialize(section);
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            // dont add the module if it failed to load
            return false;
        }

        // add the module to the map
        modules.put(id, module);

        // register events if required
        if (module instanceof Listener) {
            registerEvents((Listener) module);
        }

        // register the module's icon stack with the addon inventory
        addonInventory.registerNewIcon(module.getIconStack());
        return true;
    }
}
