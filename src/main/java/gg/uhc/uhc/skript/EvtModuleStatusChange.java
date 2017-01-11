/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.EvtModuleStatusChange
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

import gg.uhc.uhc.modules.events.ModuleChangeStatusEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;

public class EvtModuleStatusChange extends SkriptEvent {
    private Transition transition;

    static SkriptEventInfo<EvtModuleStatusChange> hook() {
        return Skript
            .registerEvent(
                "UHC Module Status Change",
                EvtModuleStatusChange.class,
                ModuleChangeStatusEvent.class,
                "module[s] " + Transition.COMBINED_PATTERN
            )
            .description("Called when modules are enabled/disabled/toggled")
            .examples("")
            .since("1.0");
    }

    @Override
    public boolean init(final Literal<?>[] args, final int matchedPattern, final SkriptParser.ParseResult parser) {
        transition = Transition.BY_MARK.get(parser.mark);

        return transition != null;
    }

    @Override
    public boolean check(final Event rawEvent) {
        assert rawEvent instanceof ModuleChangeStatusEvent;

        final ModuleChangeStatusEvent event = (ModuleChangeStatusEvent) rawEvent;

        switch (transition) {
            case TOGGLE: return true;
            case ENABLE: return event.willBeEnabled();
            case DISABLE: return !event.willBeEnabled();
            default:
                return false;
        }
    }

    @Override
    public String toString(final Event event, final boolean debug) {
        return "module " + transition.name().toLowerCase();
    }
}
