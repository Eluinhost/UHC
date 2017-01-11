/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.ExpDisableableModuleStatus
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

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.event.Event;

public class ExprDisableableModuleStatus extends SimplePropertyExpression<DisableableModule, Boolean> {
    private static final String PROPERTY = "enabled status";

    static void hook() {
        register(ExprDisableableModuleStatus.class, Boolean.class, "[enabled] status[es]", "modules");
    }

    @Override
    protected String getPropertyName() {
        return PROPERTY;
    }

    @Override
    public Boolean convert(final DisableableModule disableableModule) {
        return disableableModule.isEnabled();
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return new Class[] { Boolean.class };
        }

        return null;
    }

    @Override
    public void change(final Event event, final Object[] delta, final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            assert delta != null;

            final boolean newState = (Boolean) delta[0];

            for (final DisableableModule module : getExpr().getArray(event)) {
                if (newState) {
                    module.enable();
                } else {
                    module.disable();
                }
            }
        } else {
            super.change(event, delta, mode);
        }
    }
}
