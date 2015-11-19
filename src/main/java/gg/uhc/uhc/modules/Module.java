/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.Module
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

package gg.uhc.uhc.modules;

import com.google.common.base.Preconditions;
import gg.uhc.uhc.inventory.IconStack;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

public abstract class Module {

    protected final IconStack icon = new IconStack(Material.BARRIER, 1);
    protected ConfigurationSection config;
    protected MessageTemplates messages;
    protected Plugin plugin;
    protected String id;

    protected void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    protected void setMessages(MessageTemplates messages) {
        this.messages = messages;
    }

    protected void setConfig(ConfigurationSection section) {
        this.config = section;
    }

    public void initialize() throws InvalidConfigurationException { }

    protected void saveConfig() {
        plugin.saveConfig();
    }

    protected void setId(String id) {
        Preconditions.checkState(this.id == null, "ID has already been set, cannot be set again");
        this.id = id;
    }

    public final IconStack getIconStack() {
        return icon;
    }
}
