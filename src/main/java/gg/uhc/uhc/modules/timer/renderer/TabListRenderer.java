/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.renderer.TabListRenderer
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

package gg.uhc.uhc.modules.timer.renderer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Optional;
import org.bukkit.plugin.Plugin;

public class TabListRenderer implements TimerRenderer {

    protected static final WrappedChatComponent CLEAR_BAR_JSON = WrappedChatComponent.fromJson("{\"translate\":\"\"}");
    protected static final PacketType PACKET_TYPE = PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER;
    protected static final int TOP_INDEX = 0;
    protected static final int BOTTOM_INDEX = 1;

    protected final ProtocolManager manager;

    protected final int writeIndex;
    protected final int clearIndex;

    protected WrappedChatComponent[] lastInterceptedMessages = new WrappedChatComponent[] {
        CLEAR_BAR_JSON,
        CLEAR_BAR_JSON
    };

    public TabListRenderer(Plugin plugin, ProtocolManager manager, TabListPosition position) {
        this.manager = manager;

        if (position == TabListPosition.TOP) {
            writeIndex = TOP_INDEX;
            clearIndex = BOTTOM_INDEX;
        } else {
            writeIndex = BOTTOM_INDEX;
            clearIndex = TOP_INDEX;
        }

        manager.addPacketListener(new PacketAdapter(plugin, PACKET_TYPE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                final StructureModifier<WrappedChatComponent> components = event.getPacket().getChatComponents();

                lastInterceptedMessages[TOP_INDEX] =
                        Optional.fromNullable(components.readSafely(TOP_INDEX)).or(CLEAR_BAR_JSON);
                lastInterceptedMessages[BOTTOM_INDEX] =
                        Optional.fromNullable(components.readSafely(BOTTOM_INDEX)).or(CLEAR_BAR_JSON);
            }
        });
    }

    protected void sendBarMessage(String message) {
        final PacketContainer container = this.manager.createPacket(PACKET_TYPE);

        container.getChatComponents()
                .write(writeIndex, WrappedChatComponent.fromText(message))
                .write(clearIndex, lastInterceptedMessages[clearIndex]);

        this.manager.broadcastServerPacket(container);
    }

    protected void removeBar() {
        final PacketContainer container = this.manager.createPacket(PACKET_TYPE);

        container.getChatComponents()
                .write(TOP_INDEX, lastInterceptedMessages[TOP_INDEX])
                .write(BOTTOM_INDEX, lastInterceptedMessages[BOTTOM_INDEX]);

        this.manager.broadcastServerPacket(container);
    }

    @Override
    public void onStart(String message) {
        sendBarMessage(message);
    }

    @Override
    public void onUpdate(String message, double progress) {
        sendBarMessage(message);
    }

    @Override
    public void onStop() {
        removeBar();
    }
}
