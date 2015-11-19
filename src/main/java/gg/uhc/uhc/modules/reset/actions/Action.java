/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.reset.actions.Action
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

package gg.uhc.uhc.modules.reset.actions;

import com.google.common.base.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Action {

    protected final UUID uuid;

    protected boolean canRevert = false;

    public Action(UUID uuid) {
        this.uuid = uuid;
    }

    protected Optional<Player> getPlayer() {
        return Optional.fromNullable(Bukkit.getPlayer(uuid));
    }

    public boolean run() {
        if (canRevert) return false;

        Optional<Player> player = getPlayer();

        if (!player.isPresent()) return false;

        run(player.get());
        canRevert = true;
        return true;
    }

    public boolean revert() {
        if (!canRevert) return false;

        Optional<Player> player = getPlayer();

        if (!player.isPresent()) return false;

        revert(player.get());
        canRevert = false;
        return true;
    }

    protected abstract void run(Player player);
    protected abstract void revert(Player player);
}
