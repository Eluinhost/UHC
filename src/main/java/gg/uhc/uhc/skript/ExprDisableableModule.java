/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.ExprDisableableModule
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

package gg.uhc.uhc.skript;

import gg.uhc.uhc.modules.DisableableModule;
import gg.uhc.uhc.modules.Module;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.bukkit.event.Event;

public class ExprDisableableModule extends SimpleExpression<DisableableModule> {
    private Expression<String> names;
    private boolean multiple;

    static void hook() {
        Skript.registerExpression(
            ExprDisableableModule.class,
            DisableableModule.class,
            ExpressionType.COMBINED,
            "[all] modules",
            "[the] (1¦module|2¦modules) [called] %strings%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(
        Expression<?>[] exprs,
        int matchedPattern,
        Kleenean isDelayed,
        SkriptParser.ParseResult parseResult
    ) {
        if (matchedPattern == 0) {
            names = null;
        } else {
            names = (Expression<String>) exprs[0];
        }

        multiple = parseResult.mark == 2;

        return true;
    }

    @Override
    protected DisableableModule[] get(Event event) {
        if (names == null) {
            return Iterables.toArray(SkriptHook.getAllModules(), DisableableModule.class);
        }

        return Iterables.toArray(
            // Convert into disableable by casting to allow conversion to array
            Iterables.transform(
                // Filter out non-existing and non-disableable
                Iterables.filter(
                    // Find the module by name (or null if not exists)
                    Iterables.transform(
                        ImmutableList.copyOf(names.getArray(event)),
                        SkriptHook.findModuleByNameFunction()
                    ),
                    Predicates.and(
                        Predicates.notNull(),
                        Predicates.instanceOf(DisableableModule.class)
                    )
                ),
                new Function<Module, DisableableModule>() {
                    @Override
                    public DisableableModule apply(Module input) {
                        return (DisableableModule) input;
                    }
                }
            ),
            DisableableModule.class
        );
    }

    @Override
    public boolean isSingle() {
        return !multiple;
    }

    @Override
    public Class<? extends DisableableModule> getReturnType() {
        return DisableableModule.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return names == null ? "all modules" : "the modules " + names.toString(event, debug);
    }
}
