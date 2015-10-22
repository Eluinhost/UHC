/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.team.PrefixTest
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

package gg.uhc.uhc.modules.team;

import gg.uhc.uhc.modules.team.prefixes.Prefix;
import org.bukkit.ChatColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(PowerMockRunner.class)
public class PrefixTest {
    @Test
    public void testContainsColoursNoFormatting() throws Exception {
        Prefix prefix = new Prefix(ChatColor.AQUA);

        assertThat(prefix.containsColours(false, ChatColor.AQUA)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.AQUA)).isTrue();

        assertThat(prefix.containsColours(false, ChatColor.BLUE)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.ITALIC)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.ITALIC)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.AQUA, ChatColor.ITALIC)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.AQUA, ChatColor.ITALIC)).isFalse();

        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
    }

    @Test
    public void testContainsColoursOneFormatting() throws Exception {
        Prefix prefix = new Prefix(ChatColor.AQUA, ChatColor.ITALIC);

        assertThat(prefix.containsColours(false, ChatColor.AQUA)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.AQUA)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.BLUE)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.ITALIC)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.ITALIC)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.AQUA, ChatColor.ITALIC)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.AQUA, ChatColor.ITALIC)).isTrue();

        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
    }

    @Test
    public void testContainsColoursTwoFormatting() throws Exception {
        Prefix prefix = new Prefix(ChatColor.AQUA, ChatColor.ITALIC, ChatColor.BOLD);

        assertThat(prefix.containsColours(false, ChatColor.AQUA)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.AQUA)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.BLUE)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.ITALIC)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.ITALIC)).isFalse();

        assertThat(prefix.containsColours(false, ChatColor.AQUA, ChatColor.ITALIC)).isTrue();
        assertThat(prefix.containsColours(true, ChatColor.AQUA, ChatColor.ITALIC)).isFalse();

        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
        assertThat(prefix.containsColours(true, ChatColor.BLUE, ChatColor.ITALIC)).isFalse();
    }
}