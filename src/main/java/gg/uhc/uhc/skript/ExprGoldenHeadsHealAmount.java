/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.ExprGoldenHeadsHealAmount
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
import gg.uhc.uhc.modules.heads.GoldenHeadsModule;
import gg.uhc.uhc.util.Action2;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.common.collect.ImmutableMap;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Map;

public class ExprGoldenHeadsHealAmount extends PropertyExpression<DisableableModule, Integer> {
    private static Map<Changer.ChangeMode, Action2<GoldenHeadsModule, Integer>> ACTIONS =
        ImmutableMap
            .<Changer.ChangeMode, Action2<GoldenHeadsModule, Integer>>builder()
            .put(Changer.ChangeMode.SET, new Action2<GoldenHeadsModule, Integer>() {
                @Override
                public void apply(GoldenHeadsModule element, Integer delta) {
                    element.setHealAmount(delta);
                }
            })
            .put(Changer.ChangeMode.ADD, new Action2<GoldenHeadsModule, Integer>() {
                @Override
                public void apply(GoldenHeadsModule element, Integer element2) {
                    element.setHealAmount(element.getHealAmount() + element2);
                }
            })
            .put(Changer.ChangeMode.REMOVE, new Action2<GoldenHeadsModule, Integer>() {
                @Override
                public void apply(GoldenHeadsModule element, Integer element2) {
                    element.setHealAmount(element.getHealAmount() - element2);
                }
            })
            .build();

    static void hook() {
        Skript.registerExpression(
                ExprGoldenHeadsHealAmount.class,
                Integer.class,
                ExpressionType.PROPERTY,
                "[the] heal amount of %module%", "%module%'[s] heal amount");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(
            final Expression<?>[] exprs,
            final int matchedPattern,
            final Kleenean isDelayed,
            final SkriptParser.ParseResult parseResult
    ) {
        setExpr((Expression<GoldenHeadsModule>) exprs[0]);
        return true;
    }

    @Override
    protected Integer[] get(Event event, DisableableModule[] source) {
        return get(source, new Converter<DisableableModule, Integer>() {
            @Override
            public Integer convert(final DisableableModule module) {
                if (module instanceof GoldenHeadsModule) {
                    return ((GoldenHeadsModule) module).getHealAmount();
                }

                Skript.error("Cannot get heal amount from module " + module.getId());
                throw new UnsupportedOperationException();
            }
        });
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        return ACTIONS.containsKey(mode) ? new Class[] { Integer.class } : super.acceptChange(mode);
    }

    @Override
    public void change(final Event event, final Object[] delta, final Changer.ChangeMode mode) {
        final int castDelta = (Integer) delta[0];
        final Action2<GoldenHeadsModule, Integer> action = ACTIONS.get(mode);

        if (action == null) {
            super.change(event, delta, mode);
            return;
        }

        for (final DisableableModule module : getExpr().getArray(event)) {
            if (!(module instanceof GoldenHeadsModule)) {
                Skript.error("Cannot modify the heal amount of module: " + module.getId());
                continue;
            }

            action.apply((GoldenHeadsModule) module, castDelta);
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "the heal amount of " + getExpr().toString(event, debug);
    }
}
