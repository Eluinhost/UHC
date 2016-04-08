/*
 * Project: UHC
 * Class: gg.uhc.uhc.util.ActionBarMessenger
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

package gg.uhc.uhc.util;

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
