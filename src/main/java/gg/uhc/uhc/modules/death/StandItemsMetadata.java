package gg.uhc.uhc.modules.death;

import com.google.common.collect.Maps;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;

public class StandItemsMetadata extends FixedMetadataValue {

    public static final String KEY = "Stand Items";

    public StandItemsMetadata(Plugin owningPlugin, EnumMap<EquipmentSlot, ItemStack> items) {
        super(owningPlugin, items);
    }

    public StandItemsMetadata(Plugin owningPlugin) {
        this(owningPlugin, Maps.<EquipmentSlot, ItemStack>newEnumMap(EquipmentSlot.class));
    }

    @Override
    public EnumMap<EquipmentSlot, ItemStack> value() {
        return (EnumMap<EquipmentSlot, ItemStack>) super.value();
    }
}
