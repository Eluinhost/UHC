package gg.uhc.uhc.modules;

import gg.uhc.uhc.ItemStackNBTStringFetcher;
import gg.uhc.uhc.inventory.ClickHandler;
import gg.uhc.uhc.inventory.IconStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public abstract class DisableableModule extends Module implements ClickHandler {

    protected static final String CONSOLE_FORMAT = "[UHC] Module %s is now %s";

    private boolean enabled;
    protected String iconName = "ERROR: NO ICON NAME SET";

    public DisableableModule() {
        icon.registerClickHandler(this);
    }

    /**
     * @return if enabled isn't set in the config what should the default be
     */
    protected abstract boolean isEnabledByDefault();

    @Override
    public void initialize(ConfigurationSection config) throws InvalidConfigurationException {
        super.initialize(config);

        if (!config.contains("enabled")) {
            config.set("enabled", isEnabledByDefault());
        }

        if (!config.isBoolean("enabled"))
            throw new InvalidConfigurationException("Invalid value at key " + config.getCurrentPath() + ".enabled (" + config.get("enabled") + ")");

        this.enabled = config.getBoolean("enabled");

        if (this.enabled) {
            enable();
        } else {
            disable();
        }
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

    public final void enable() {
        enabled = true;
        config.set("enabled", true);
        saveConfig();
        onEnable();
        rerender();
    }

    public final void disable() {
        enabled = false;
        config.set("enabled", false);
        saveConfig();
        onDisable();
        rerender();
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

    public final void toggle() {
        if (isEnabled()) {
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
        if (!player.hasPermission("uhc.command.uhc")) return;

        toggle();
        announceState();
    }
}
