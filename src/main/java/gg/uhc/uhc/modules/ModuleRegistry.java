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
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gg.uhc.uhc.inventory.IconInventory;
import gg.uhc.uhc.messages.MessageTemplates;
import gg.uhc.uhc.messages.SubsectionMessageTemplates;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ModuleRegistry {

    public static final int CATEGORY_WORLD = 0;
    public static final int CATEGORY_APPLES = 10;
    public static final int CATEGORY_POTIONS = 20;
    public static final int CATEGORY_RECIPIES = 30;
    public static final int CATEGORY_HEALTH = 40;
    public static final int CATEGORY_DEATH = 50;
    public static final int CATEGORY_MISC = 100;

    public static final String ADDON_INVENTORY_TITLE = ChatColor.DARK_PURPLE + "UHC Control Panel";
    public static final Pattern VALID_MODULE_NAME_REGEX = Pattern.compile("^\\w+$");

    protected final Plugin plugin;
    protected final PluginManager pluginManager;
    protected final MessageTemplates strings;
    protected final ConfigurationSection config;

    protected final Map<String, Module> modules = Maps.newHashMap();
    protected final IconInventory addonInventory;

    public ModuleRegistry(Plugin plugin, MessageTemplates strings, ConfigurationSection config) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
        this.config = config;
        this.strings = strings;

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

    public Set<String> getIds() {
        return ImmutableSet.copyOf(modules.keySet());
    }

    public Optional<Module> get(String id) {
        return Optional.fromNullable(modules.get(id.toLowerCase()));
    }

    public <T extends Module> Optional<T> get(Class<T> klass) {
        return (Optional<T>) Iterables.tryFind(modules.values(), Predicates.instanceOf(klass));
    }

    /**
     * Helper method for transition.
     * Sets the module id and then registers it using #register(Module)
     *
     * @return true if module loaded
     * @throws IllegalStateException if module's id was already set
     * @see ModuleRegistry#register(Module)
     *
     * @deprecated
     */
    public boolean register(Module module, String id) {
        module.setId(id);
        return register(module);
    }

    /**
     * Registers the given module with the registry.
     *
     * <p>
     *     A module must have an id set that is:
     *     <ul>
     *         <li>at most 22 characters</li>
     *         <li>alphanumeric + _</li>
     *         <li>not already registered (case insensitive)</li>
     *     </ul>
     * </p>
     *
     * <p>
     *     The module will be given a:
     *     <ul>
     *         <li>Plugin</li>
     *         <li>message templates</li>
     *         <li>configuration section</li>
     *     </ul>
     *     These will only be added if they have not already been set
     * </p>
     *
     * <p>If the configuration says not to load the module this method will then return false and nothing else will be done</p>
     *
     * <p>
     *     If the module does load:
     *     <ul>
     *         <li>Module#initialize() is called for initialization of the module, If any exceptions are thrown during initialization the module will not be loaded</li>
     *         <li>If the module implements Listener it is registered for events</li>
     *         <li>the module's icon is registered in the config inventory</li>
     *         <li>it is stored in the registry</li>
     *     </ul>
     * </p>
     * @param module the module to register
     * @return true if the module is loaded and stored
     */
    public boolean register(Module module) {
        String id = module.getId();
        Preconditions.checkNotNull(id, "Module does not have an id set");
        Preconditions.checkArgument(VALID_MODULE_NAME_REGEX.matcher(id).matches(), "Module id may only contain alphanumberic characters and _, found `" + id + "`");
        Preconditions.checkArgument(module.getId().length() <= 22, "Module names can only be 22 characters at most");

        // use all lower case in the map
        id = id.toLowerCase();

        // make sure it's a new key
        Preconditions.checkArgument(!modules.containsKey(id), "Module `" + id + "` is already registered");

        String sectionId = "modules." + id;

        // check add inject plugin
        if (module.getPlugin() == null) {
            module.setPlugin(plugin);
        }

        // check and inject templates
        if (module.getMessageTemaplates() == null) {
            module.setMessageTemplates(new SubsectionMessageTemplates(strings, sectionId));
        }

        if (!config.contains(sectionId)) {
            config.createSection(sectionId);
        }

        // check and inject configuration
        ConfigurationSection section;
        if (module.getConfig() == null) {
            section = config.getConfigurationSection(sectionId);

            // add the configuration
            module.setConfig(section);
        } else {
            section = module.getConfig();
        }

        // set load parameter if it doesn't exist
        if (!section.contains("load")) {
            section.set("load", true);
        }

        // if it's configured to not load then don't load it
        if (!section.getBoolean("load")) {
            return false;
        }

        // attempt initiaization
        try {
            module.initialize();
        } catch (Exception ex) {
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
