/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.CondDisableableModuleEnabled
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

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.bukkit.event.Event;

public class CondDisableableModuleEnabled extends Condition {
    private Expression<DisableableModule> moduleExpression;
    private boolean expecting;

    static void hook() {
        Skript.registerCondition(
            CondDisableableModuleEnabled.class,
            "%modules% [(is|are)] (1¦enabled|2¦disabled)"
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
        moduleExpression = (Expression<DisableableModule>) exprs[0];
        expecting = parseResult.mark == 1;
        return true;
    }

    @Override
    public boolean check(Event event) {
        return Iterables.all(
            Iterables.transform(
                ImmutableList.copyOf(moduleExpression.getArray(event)),
                new Function<DisableableModule, Boolean>() {
                    @Override
                    public Boolean apply(DisableableModule input) {
                        return input.isEnabled();
                    }
                }
            ),
            Predicates.equalTo(expecting)
        );
    }

    @Override
    public String toString(Event event, boolean debug) {
        return moduleExpression.toString(event, debug) + " is " + (expecting ? "enabled" : "disabled");
    }
}
