package gg.uhc.uhc.modules.events;

import gg.uhc.uhc.modules.DisableableModule;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ModuleEnableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    protected boolean cancelled = false;

    protected final DisableableModule module;

    public ModuleEnableEvent(DisableableModule module) {
        this.module = module;
    }

    public DisableableModule getModule() {
        return module;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
