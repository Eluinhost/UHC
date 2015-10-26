/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.death.ModifiedDeathMessagesModule
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

package gg.uhc.uhc.modules.death;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.ModuleRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.StringReader;
import java.io.StringWriter;

public class ModifiedDeathMessagesModule extends DisableableModule implements Listener {

    protected static final String ICON_NAME = "Modified Death Messages";
    protected static final String FORMAT_KEY = "format";
    protected static final String FORMAT_EXPLANATION_KEY = "format explanation";

    protected String formatExplanation;
    protected Mustache template;

    public ModifiedDeathMessagesModule() {
        this.iconName = ICON_NAME;
        this.icon.setType(Material.BANNER);
        this.icon.setWeight(ModuleRegistry.CATEGORY_DEATH);
    }

    @Override
    public void initialize(ConfigurationSection section) throws InvalidConfigurationException {
        if (!section.contains(FORMAT_KEY)) {
            section.set(FORMAT_KEY, "&c{{original}} at {{player.world}},{{player.blockCoords}}");
        }

        if (!section.contains(FORMAT_EXPLANATION_KEY)) {
            section.set(FORMAT_EXPLANATION_KEY, "<message> at <coords>");
        }

        String format = section.getString(FORMAT_KEY);
        formatExplanation = section.getString(FORMAT_EXPLANATION_KEY);

        MustacheFactory mf = new DefaultMustacheFactory();
        try {
            template = mf.compile(new StringReader(ChatColor.translateAlternateColorCodes('&', format)), "death-message");
        } catch (Exception ex) {
            throw new InvalidConfigurationException("Error parsing death message template", ex);
        }

        super.initialize(section);
    }

    @Override
    public void rerender() {
        super.rerender();

        if (isEnabled()) {
            icon.setLore("Death messages are modified", "Format: " + formatExplanation);
        }
        else {
            icon.setLore("Death messages are not modified");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        StringWriter writer = new StringWriter();
        template.execute(writer, new DeathContext(event));

        event.setDeathMessage(writer.getBuffer().toString());
    }

    @Override
    protected boolean isEnabledByDefault() {
        return false;
    }
}
