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