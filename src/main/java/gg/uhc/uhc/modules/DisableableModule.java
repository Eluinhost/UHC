package gg.uhc.uhc.modules;

import gg.uhc.uhc.ItemStackNBTStringFetcher;
import gg.uhc.uhc.inventory.ClickHandler;
import gg.uhc.uhc.inventory.IconStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class DisableableModule extends ConfigurableModule implements ClickHandler {

    protected static final String CONSOLE_FORMAT = "[UHC] Module %s is now %s";

    protected final String name;
    protected final IconStack icon;

    protected boolean enabled;

    public DisableableModule(String name, IconStack icon, boolean enabled) {
        // TODO use extension of configurable module to pull/save enabled state to the configuration as it is changed
        this.name = name;
        this.icon = icon;

        icon.registerClickHandler(this);

        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public final void enable() {
        this.enabled = true;

        // set display name to GREEN and set amount to 1
        icon.setDisplayName(ChatColor.GREEN + name);
        icon.setAmount(1);

        onEnable();
    }

    public final void disable() {
        this.enabled = false;

        // set display name to RED and set amount to 0
        icon.setDisplayName(ChatColor.RED + name);
        icon.setAmount(0);

        onDisable();
    }

    public final void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onClick(Player player) {
        // TODO permissions
        toggle();

        String enableStatus = enabled ? "enabled" : "disabled";

        Bukkit.getConsoleSender().sendMessage(String.format(CONSOLE_FORMAT, enableStatus, name));

        TextComponent base = new TextComponent("[UHC] ");
        base.setColor(ChatColor.AQUA);

        TextComponent itemNBT = new TextComponent(ItemStackNBTStringFetcher.readFromItemStack(icon));

        TextComponent module = new TextComponent(name);
        module.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{itemNBT}));
        module.setUnderlined(true);
        module.setColor(enabled ? ChatColor.GREEN : ChatColor.RED);

        base.addExtra(module);
        base.addExtra(" is now " + enableStatus);

        Bukkit.spigot().broadcast(base);
    }
}
