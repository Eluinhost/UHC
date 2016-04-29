/*
 * Project: UHC
 * Class: gg.uhc.uhc.UpdateManager
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

package gg.uhc.uhc;

import gg.uhc.githubreleasechecker.ReleaseChecker;
import gg.uhc.githubreleasechecker.UpdateResponse;
import gg.uhc.githubreleasechecker.data.Release;
import gg.uhc.githubreleasechecker.deserialization.LatestReleaseQueryer;
import gg.uhc.githubreleasechecker.zafarkhaja.semver.Version;
import gg.uhc.uhc.messages.MessageTemplates;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class UpdateManager implements Runnable, Listener {
    protected final ReleaseChecker checker;
    protected final Plugin plugin;
    protected final MessageTemplates templates;

    protected Version latestUpdate;
    protected String chatMessage;
    protected BaseComponent infoMessage;

    UpdateManager(Plugin plugin, MessageTemplates templates) {
        final LatestReleaseQueryer queryer = new LatestReleaseQueryer("Eluinhost", "UHC");
        this.checker = new ReleaseChecker(plugin, queryer, true);
        this.plugin = plugin;
        this.templates = templates;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (event.getPlayer().isOp() && chatMessage != null) {
            event.getPlayer().sendMessage(chatMessage);
            event.getPlayer().spigot().sendMessage(infoMessage);
        }
    }

    @Override
    public void run() {
        try {
            final UpdateResponse response = checker.checkForUpdate();

            if (!response.hasUpdate()) return;

            final Release update = response.getUpdateDetails();

            // update.getVersion() cannot be null here
            if (update.getVersion().equals(latestUpdate)) return;

            latestUpdate = update.getVersion();

            plugin.getLogger().info(templates.evalTemplate("console", response));

            chatMessage = templates.evalTemplate("player.notify line", response);

            infoMessage = new TextComponent(templates.evalTemplate("player.info line", response));
            infoMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.getUrl()));

            for (final Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(chatMessage);
                    player.spigot().sendMessage(infoMessage);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
