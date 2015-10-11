package gg.uhc.uhc.modules.health;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import gg.uhc.uhc.modules.events.ModuleDisableEvent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HardcoreHeartsModule extends Module implements Listener {

    protected static final String ICON_NAME = "Hardcore Hearts";

    protected final AutoRespawnModule respawnModule;

    public HardcoreHeartsModule(AutoRespawnModule respawnModule) {
        this.respawnModule = respawnModule;
        this.icon.setType(Material.DOUBLE_PLANT);
        this.icon.setDurability((short) 4);
        this.icon.setWeight(50);
        this.icon.setDisplayName(ICON_NAME);
        this.icon.setLore("Showing hardcore hearts on login");
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        ProtocolLibrary.getProtocolManager().addPacketListener(new HardcoreHeartsListener());

        super.initialize(section);
        // make sure to enable the respawn module
        respawnModule.enable();

        if (!respawnModule.isEnabled()){
            throw new InvalidConfigurationException("Error enabling the respawn module. The respawn module is required to run hardcore hearts");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ModuleDisableEvent event) {
        if (event.getModule() == respawnModule) event.setCancelled(true);
    }

    class HardcoreHeartsListener extends PacketAdapter {

        public HardcoreHeartsListener() {
            super(HardcoreHeartsModule.this.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
                event.getPacket().getBooleans().write(0, true);
            }
        }
    }
}
