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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public abstract class DisableableModule extends Module implements ClickHandler {

    protected static final String CONSOLE_FORMAT = "[UHC] Module %s is now %s";
    protected static final String CONFIRMATION_TITLE = ChatColor.DARK_PURPLE + "Toggle: ";

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
     * @return if enabled isn't set in the config what should the default be
     */
    protected abstract boolean isEnabledByDefault();

    @Override
    public void initialize() throws InvalidConfigurationException {
        if (!config.contains("enabled")) {
            config.set("enabled", isEnabledByDefault());
        }

        if (!config.isBoolean("enabled"))
            throw new InvalidConfigurationException("Invalid value at key " + config.getCurrentPath() + ".enabled (" + config.get("enabled") + ")");

        // store inverted version to trigger change
        this.enabled = !config.getBoolean("enabled");
        toggle();

        // register a click handler on our icon to show the confirmation inventory
        icon.registerClickHandler(new ClickHandler() {
            @Override
            public void onClick(Player player) {
                if (player.hasPermission("uhc.command.uhc.admin")) {
                    confirmation.showTo(player);
                }
            }
        });

        // setup confirmation inventory
        confirmation = new IconInventory(CONFIRMATION_TITLE + id);

        for (int i = 0; i < 9; i++) {
            IconStack stack;
            if (i == 4) {
                stack = new IconStack(Material.WOOL, 1, (short) 5);
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

    protected void rerender() {
        IconStack icon = getIconStack();

        if (isEnabled()) {
            icon.setDisplayName(ChatColor.GREEN + iconName);
            icon.setAmount(1);
        } else {
            icon.setDisplayName(ChatColor.RED + iconName);
            icon.setAmount(0);
        }
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public final boolean enable() {
        if (isEnabled()) return false;

        ModuleEnableEvent event = new ModuleEnableEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return false;

        enabled = true;
        config.set("enabled", true);
        saveConfig();
        onEnable();
        rerender();

        return true;
    }

    public final boolean disable() {
        if (!isEnabled()) return false;

        ModuleDisableEvent event = new ModuleDisableEvent(this);
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
        String enableStatus = isEnabled() ? "enabled" : "disabled";

        Bukkit.getConsoleSender().sendMessage(String.format(CONSOLE_FORMAT, iconName, enableStatus));

        TextComponent base = new TextComponent("[UHC] ");
        base.setColor(ChatColor.AQUA);

        TextComponent itemNBT = new TextComponent(ItemStackNBTStringFetcher.readFromItemStack(getIconStack()));

        TextComponent module = new TextComponent(iconName);
        module.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{itemNBT}));
        module.setUnderlined(true);
        module.setColor(isEnabled() ? ChatColor.GREEN : ChatColor.RED);

        base.addExtra(module);
        base.addExtra(" is " + enableStatus);

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
        if (!player.hasPermission("uhc.command.uhc.admin")) return;

        if (toggle()) {
            announceState();
        }
    }
}
