package gg.uhc.uhc.modules.commands;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.Module;
import gg.uhc.uhc.modules.ModuleRegistry;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

import java.util.Map;

public class ModuleEntryConverter implements ValueConverter<Map.Entry<String, Module>> {

    protected final ModuleRegistry registry;

    public ModuleEntryConverter(ModuleRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Map.Entry<String, Module> convert(String value) {
        Optional<Module> module = registry.get(value);

        if (!module.isPresent()) throw new ValueConversionException("Invalid module id: " + value);

        return Maps.immutableEntry(value, module.get());
    }

    @Override
    public Class<Map.Entry<String, Module>> valueType() {
        return null;
    }

    @Override
    public String valuePattern() {
        return "module id";
    }
}
