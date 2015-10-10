package gg.uhc.uhc.modules.timer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ActionBarMessenger {

    protected final ProtocolManager manager;

    public ActionBarMessenger(ProtocolManager manager) {
        this.manager = manager;
    }

    public void sendMessage(Player player, String message) {
        sendMessage(Lists.newArrayList(player), message);
    }

    public void sendMessage(Collection<? extends Player> players, String message) {
        PacketContainer title = manager.createPacket(PacketType.Play.Server.CHAT);
        title.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        title.getBytes().write(0, (byte) 2);

        try {
            for (Player player : players) {
                manager.sendServerPacket(player, title);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
