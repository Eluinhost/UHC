package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    protected int healAmount = 0;

    public GoldenHeadsModule(PlayerHeadProvider playerHeadProvider, IconStack icon, boolean enabled, int healAmount) {
        super(ICON_NAME, icon, enabled);
        this.playerHeadProvider = playerHeadProvider;

        icon.setType(Material.SKULL_ITEM);
        icon.setDurability((short) 3);

        setHealAmount(healAmount);

        // register the new recipe
        ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
                .shape("AAA", "ABA", "AAA")
                .setIngredient('A', Material.GOLD_INGOT)
                .setIngredient('B', Material.SKULL_ITEM, 3);

        Bukkit.addRecipe(modified);
    }

    public void setHealAmount(int halfHearts) {
        Preconditions.checkArgument(halfHearts > 0);
        this.healAmount = halfHearts;

        updateIconInfo();
    }

    protected void updateIconInfo() {
        if (enabled) {
            icon.setLore(ChatColor.GREEN + "Heal: " + formatter.format(healAmount / 2D) + " hearts", "Golden heads are craftable");
            // show heal amount on icon
            icon.setAmount(healAmount);
        } else {
            icon.setLore("Golden heads are not craftable and heal 2 hearts");
            icon.setAmount(0);
        }
    }

    @Override
    public void onEnable() {
        updateIconInfo();
    }

    @Override
    public void onDisable() {
        updateIconInfo();
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
