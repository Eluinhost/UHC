package gg.uhc.uhc.modules.health;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.autorespawn.AutoRespawnModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class HardcoreHeartsModule extends DisableableModule {

    protected static final String ICON_NAME = "Hardcore Hearts";

    protected final AutoRespawnModule respawnModule;
    protected boolean initialized = false;

    public HardcoreHeartsModule(AutoRespawnModule respawnModule) {
        this.respawnModule = respawnModule;
        this.icon.setType(Material.DOUBLE_PLANT);
        this.icon.setDurability((short) 4);
        this.icon.setWeight(50);

        this.iconName = ICON_NAME;
    }

    @Override
    public void rerender() {
        super.rerender();
        icon.setLore(isEnabled() ? "Showing hardcore hearts" : "Showing regular hearts");
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        ProtocolLibrary.getProtocolManager().addPacketListener(new HardcoreHeartsListener());

        super.initialize(section);
        initialized = true;
    }

    @Override
    public void onEnable() {
        if (!respawnModule.isEnabled()) {
            respawnModule.enable();

            if (initialized) {
                respawnModule.announceState();
            }
        }
    }

    @Override
    public void announceState() {
        super.announceState();

        Bukkit.broadcastMessage(ChatColor.GRAY + "You will need to relog to see the new heart settings");
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }

    class HardcoreHeartsListener extends PacketAdapter {

        public HardcoreHeartsListener() {
            super(HardcoreHeartsModule.this.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (isEnabled() && event.getPacketType().equals(PacketType.Play.Server.LOGIN)) {
                event.getPacket().getBooleans().write(0, true);
            }
        }
    }
}
