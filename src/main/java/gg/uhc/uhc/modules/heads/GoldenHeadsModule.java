package gg.uhc.uhc.modules.heads;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.NumberFormat;

public class GoldenHeadsModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Golden Heads";
    protected static final NumberFormat formatter = NumberFormat.getNumberInstance();
    protected static final int TICKS_PER_HALF_HEART = 25;

    static {
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
    }

    protected final PlayerHeadProvider playerHeadProvider;

    protected int healAmount;

    public GoldenHeadsModule(PlayerHeadProvider provider) {
        this.playerHeadProvider = provider;

        this.iconName = ICON_NAME;
        this.icon.setType(Material.SKULL_ITEM);
        this.icon.setDurability((short) 3);
        this.icon.setWeight(-5);

        // register the new recipe
        ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, 3);

        Bukkit.addRecipe(modified);
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int amount) {
        this.healAmount = amount;
        config.set("heal amount", this.healAmount);
        saveConfig();
        rerender();
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        if (!section.contains("heal amount")) {
            section.set("heal amount", 6);
        }

        if (!section.isInt("heal amount"))
            throw new InvalidConfigurationException("Invalid value at " + section.getCurrentPath() + ".heal amount (" + section.get("heal amount"));

        // TODO check heal amount sane
        healAmount = section.getInt("heal amount");

        super.initialize(section);
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore(ChatColor.GREEN + "Heal: " + formatter.format(healAmount / 2D) + " hearts", "Golden heads are craftable");
            // show heal amount on icon
            icon.setAmount(healAmount);
        } else {
            icon.setLore("Golden heads are not craftable and heal 2 hearts");
            icon.setAmount(0);
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() != Material.GOLDEN_APPLE) return;

        ItemStack centre = event.getInventory().getMatrix()[4];

        if (centre == null || centre.getType() != Material.SKULL_ITEM) return;

        if (!isEnabled()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        SkullMeta meta = (SkullMeta) centre.getItemMeta();
        event.getInventory().setResult(playerHeadProvider.getGoldenHeadItem(meta.hasOwner() ? meta.getOwner() : "Manually Crafted"));
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if(isEnabled() && playerHeadProvider.isGoldenHead(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    TICKS_PER_HALF_HEART * healAmount,
                    1
            ));
        }
    }
}
