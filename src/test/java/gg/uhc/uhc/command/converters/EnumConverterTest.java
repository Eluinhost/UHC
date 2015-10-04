package gg.uhc.uhc.command.converters;

import joptsimple.ValueConversionException;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class EnumConverterTest {

    @Test
    public void test_case_insensitive() {
        EnumConverter converter = EnumConverter.forEnum(Material.class);

        assertThat(converter.convert("LOG")).isEqualTo(Material.LOG);
        assertThat(converter.convert("dirt")).isEqualTo(Material.DIRT);
        assertThat(converter.convert("Grass")).isEqualTo(Material.GRASS);
    }

    @Test(expected = ValueConversionException.class)
    public void test_invalid() {
        EnumConverter converter = EnumConverter.forEnum(Material.class);

        converter.convert("INVALID_MATERIAL");
    }
}
