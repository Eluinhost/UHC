/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.heads.PlayerHeadProvider
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

package gg.uhc.uhc.modules.heads;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.messages.MessageTemplates;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerHeadProvider {

    public static final String HEAD_NAME = ChatColor.GOLD + "Golden Head";
    public static final short PLAYER_HEAD_DATA = 3;
    protected static final String LORE_PATH = "lore";
    protected GoldenHeadsModule module;

    public void setModule(GoldenHeadsModule module) {
        Preconditions.checkState(this.module == null, "Provider is already registered with module");
        this.module = module;
    }

    public ItemStack getPlayerHeadItem(String name) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1);
        stack.setDurability(PLAYER_HEAD_DATA);

        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(name);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getPlayerHeadItem(Player player) {
        return getPlayerHeadItem(player.getName());
    }

    public ItemStack getGoldenHeadItem(String playerName) {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(HEAD_NAME);

        // add lore
        ImmutableMap<String, String> context = ImmutableMap.of(
                "player", playerName,
                "amount", Integer.toString(module.getHealAmount())
        );
        List<String> lore = module.getMessageTemaplates().evalTemplates(LORE_PATH, context);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public boolean isGoldenHead(ItemStack itemStack) {
        if(itemStack.getType() != Material.GOLDEN_APPLE) return false;

        ItemMeta im = itemStack.getItemMeta();

        return im.hasDisplayName() && im.getDisplayName().equals(HEAD_NAME);
    }

    public void setBlockAsHead(Player p, Block headBlock) {
        setBlockAsHead(p.getName(), headBlock, BlockFaceXZ.getClosest(p));
    }

    public void setBlockAsHead(String name, Block headBlock, BlockFaceXZ direction) {
        // set the type to skull
        headBlock.setType(Material.SKULL);
        headBlock.setData((byte) 1);

        Skull state = (Skull) headBlock.getState();

        state.setSkullType(SkullType.PLAYER);
        state.setOwner(name);
        state.setRotation(direction.getBlockFace());
        state.update();
    }
}