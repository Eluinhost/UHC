/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.DisableableModule
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

import gg.uhc.uhc.ItemStackNBTStringFetcher;
import gg.uhc.uhc.inventory.ClickHandler;
import gg.uhc.uhc.inventory.IconInventory;
import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.events.ModuleDisableEvent;
import gg.uhc.uhc.modules.events.ModuleEnableEvent;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public abstract class DisableableModule extends Module implements ClickHandler {

    public static final String ADMIN_PERMISSION = "uhc.command.uhc.admin";

    protected static final String ENABLED_KEY = "enabled";
    protected static final String ENABLED_LORE_PATH = "enabled lore";
    protected static final String DISABLED_LORE_PATH = "disabled lore";
    protected static final String CONFIRMATION_TITLE = ChatColor.DARK_PURPLE + "Toggle: ";
    protected static final int INVENTORY_WIDTH = 9;
    protected static final int CONFIRM_ITEM_SLOT = 4;
    protected static final short CONFIRM_WOOL_COLOUR_ID = 5;

    protected static final ClickHandler CLOSE_INVENTORY = new ClickHandler() {
        @Override
        public void onClick(Player player) {
            player.closeInventory();
        }
    };

    protected static final ClickHandler CANCELLED_TOGGLE = new ClickHandler() {
        @Override
        public void onClick(Player player) {
            player.sendMessage(ChatColor.RED + "Cancelled toggle");
        }
    };

    protected boolean enabled;
    protected IconInventory confirmation;
    protected String iconName = "ERROR: NO ICON NAME SET";

    /**
     * @return if enabled isn't set in the config what should the default be.
     */
    protected abstract boolean isEnabledByDefault();

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains(ENABLED_KEY)) {
            config.set(ENABLED_KEY, isEnabledByDefault());
        }

        if (!config.isBoolean(ENABLED_KEY)) {
            throw new InvalidConfigurationException(
                    "Invalid value at key " + config.getCurrentPath() + ".enabled (" + config.get(ENABLED_KEY) + ")"
            );
        }

        // store inverted version to trigger change
        this.enabled = !config.getBoolean(ENABLED_KEY);
        toggle();

        // register a click handler on our icon to show the confirmation inventory
        icon.registerClickHandler(new ClickHandler() {
            @Override
            public void onClick(Player player) {
                if (player.hasPermission(ADMIN_PERMISSION)) {
                    confirmation.showTo(player);
                }
            }
        });

        // setup confirmation inventory
        confirmation = new IconInventory(CONFIRMATION_TITLE + id);

        for (int i = 0; i < INVENTORY_WIDTH; i++) {
            final IconStack stack;
            if (i == CONFIRM_ITEM_SLOT) {
                stack = new IconStack(Material.WOOL, 1, CONFIRM_WOOL_COLOUR_ID);
                stack.setDisplayName(ChatColor.GREEN + "Confirm Toggle");
                stack.setLore("Clicking this will toggle the module " + id);
                stack.registerClickHandler(this);
            } else {
                stack = new IconStack(Material.AIR);
                stack.registerClickHandler(CANCELLED_TOGGLE);
            }

            stack.setWeight(i);
            stack.registerClickHandler(CLOSE_INVENTORY);
            confirmation.registerNewIcon(stack);
        }

        // register confirmation inventory for events
        Bukkit.getServer().getPluginManager().registerEvents(confirmation, plugin);
    }

    protected final void rerender() {
        if (isEnabled()) {
            renderEnabled();
        } else {
            renderDisabled();
        }
    }

    protected void renderEnabled() {
        icon.setDisplayName(ChatColor.GREEN + iconName);
        icon.setAmount(1);
        icon.setLore(getEnabledLore());
    }

    protected List<String> getEnabledLore() {
        return messages.getRawStrings(ENABLED_LORE_PATH);
    }

    protected void renderDisabled() {
        icon.setDisplayName(ChatColor.RED + iconName);
        icon.setAmount(0);
        icon.setLore(getDisabledLore());
    }

    protected List<String> getDisabledLore() {
        return messages.getRawStrings(DISABLED_LORE_PATH);
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public final boolean enable() {
        if (isEnabled()) return false;

        final ModuleEnableEvent event = new ModuleEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return false;

        enabled = true;
        config.set(ENABLED_KEY, true);
        saveConfig();
        onEnable();
        rerender();

        return true;
    }

    public final boolean disable() {
        if (!isEnabled()) return false;

        final ModuleDisableEvent event = new ModuleDisableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return false;

        enabled = false;
        config.set("enabled", false);
        saveConfig();
        onDisable();
        rerender();

        return true;
    }

    public void announceState() {
        final String enableStatus = isEnabled() ? "enabled" : "disabled";

        final Map<String, Object> context = ImmutableMap.<String, Object>builder()
                .put("name", iconName)
                .put("status", enableStatus)
                .build();

        Bukkit.getConsoleSender().sendMessage(
                messages.getRoot().evalTemplate("modules.changed.console notice", context)
        );

        final TextComponent base = new TextComponent(
                messages.getRoot().evalTemplate("modules.changed.broadcast.prefix", context)
        );

        base.setColor(ChatColor.AQUA);

        final TextComponent itemNbt = new TextComponent(ItemStackNBTStringFetcher.readFromItemStack(getIconStack()));

        final TextComponent module = new TextComponent(iconName);
        module.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{itemNbt}));
        module.setUnderlined(true);
        module.setColor(isEnabled() ? ChatColor.GREEN : ChatColor.RED);

        base.addExtra(module);
        base.addExtra(messages.getRoot().evalTemplate("modules.changed.broadcast.status", context));

        Bukkit.spigot().broadcast(base);
    }

    public final boolean toggle() {
        return isEnabled() ? disable() : enable();
    }

    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onClick(Player player) {
        if (!player.hasPermission(ADMIN_PERMISSION)) return;

        if (toggle()) {
            announceState();
        }
    }
}
