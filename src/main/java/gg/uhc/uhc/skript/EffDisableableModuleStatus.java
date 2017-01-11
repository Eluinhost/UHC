package gg.uhc.uhc.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.event.Event;

public class EffDisableableModuleStatus extends Effect {
    private Expression<DisableableModule> moduleExpression;
    private Transition type;

    static void hook() {
        Skript.registerEffect(
            EffDisableableModuleStatus.class,
            Transition.COMBINED_PATTERN + " %modules%"
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        moduleExpression = (Expression<DisableableModule>) exprs[0];
        type = Transition.BY_MARK.get(parseResult.mark);
        return true;
    }

    @Override
    protected void execute(Event e) {
        for (final DisableableModule module : moduleExpression.getArray(e)) {
            switch (type) {
            case DISABLE:
                module.disable();
                break;
            case ENABLE:
                module.enable();
                break;
            case TOGGLE:
                module.toggle();
                break;
            }
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return type.toString().toLowerCase() + " " + moduleExpression.toString(event, debug);
    }
}
