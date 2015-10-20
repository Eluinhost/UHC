package gg.uhc.uhc.modules.team.requests;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gg.uhc.uhc.modules.team.FunctionalUtil;
import gg.uhc.uhc.modules.team.TeamModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestManager {

    public enum AcceptState {
        ACCEPT("Team request %d from '%s' was accepted", ChatColor.GREEN + "Your team request was accepted"),
        DENY("Team request %d from '%s' was denied", ChatColor.RED + "Your team request was denied"),
        CANCEL("Team request %d from '%s' was cancelled.", ChatColor.RED + "Your team request timed out and was cancelled.");

        protected final String broadcast;
        protected final String notify;

        AcceptState(String broadcast, String notify) {
            this.broadcast = ChatColor.DARK_GRAY + broadcast;
            this.notify = notify;
        }
    }

    protected static final String ADDED_REQUEST = ChatColor.AQUA + "Added team request";

    public static final String ADMIN_PERMISSION = "uhc.command.teamrequestadmin";

    protected final Map<UUID, TeamRequest> byUUID = Maps.newHashMap();
    protected final Map<Integer, TeamRequest> byId = Maps.newHashMap();
    protected final Collection<TeamRequest> requests = byUUID.values();

    protected final Map<Integer, BukkitRunnable> timers = Maps.newHashMap();

    protected final Plugin plugin;
    protected final TeamModule module;
    protected final long autoCancelTime;

    public RequestManager(Plugin plugin, TeamModule module, long autoCancelTime) {
        this.plugin = plugin;
        this.module = module;

        Preconditions.checkArgument(autoCancelTime > 0);
        this.autoCancelTime = autoCancelTime;
    }

    public Optional<TeamRequest> getByUUID(UUID uuid) {
        return Optional.fromNullable(byUUID.get(uuid));
    }

    public Optional<TeamRequest> getById(int id) {
        return Optional.fromNullable(byId.get(id));
    }

    public List<TeamRequest> getRequests() {
        return ImmutableList.copyOf(requests);
    }

    /**
     * Removes the request and processes the response
     *
     * @param id the id of the request
     * @param accepted the state to set
     * @return true if id existed, false otherwise
     */
    public boolean finalizeRequest(int id, AcceptState accepted) {
        Optional<TeamRequest> optional = removeRequest(id);

        if (!optional.isPresent()) return false;

        TeamRequest request = optional.get();

        broadcast(String.format(accepted.broadcast, request.getId(), request.getOwnerName()));

        Player player = Bukkit.getPlayer(request.getOwner());

        if (player != null) {
            player.sendMessage(accepted.notify);
        }

        if (accepted == AcceptState.ACCEPT) {
            Iterable<OfflinePlayer> players = Iterables.filter(Iterables.transform(request.getOthers(), FunctionalUtil.OFFLINE_PLAYER_FROM_NAME), Predicates.notNull());

            Optional<Team> potentialTeam = module.findFirstEmptyTeam();

            if (!potentialTeam.isPresent()) {
                broadcast(ChatColor.RED + "Failed to create a new team for request " + request.getId() + " as there no more empty teams. Clear a team and resend the request");
                return true;
            }

            Team team = potentialTeam.get();

            for (OfflinePlayer p : players) {
                team.addPlayer(p);
            }

            team.addPlayer(Bukkit.getOfflinePlayer(request.getOwner()));
        }

        // otherwise do nothing
        return true;
    }

    public boolean acceptRequest(int id) {
        return finalizeRequest(id, AcceptState.ACCEPT);
    }

    public boolean denyRequest(int id) {
        return finalizeRequest(id, AcceptState.DENY);
    }

    public boolean cancelRequest(int id) {
        return finalizeRequest(id, AcceptState.CANCEL);
    }

    protected Optional<TeamRequest> removeRequest(int id) {
        TeamRequest request = byId.remove(id);

        if (request == null) return Optional.absent();

        // cancel the timer and UUID mapping too
        timers.remove(id).cancel();
        byUUID.remove(request.getOwner());

        return Optional.fromNullable(request);
    }

    /**
     * Adds a new team request. If a request already existed for the owner, it is cancelled before this one is added.
     *
     * @param request the request to add
     */
    public void addRequest(TeamRequest request) {
        UUID owner = request.getOwner();
        int id = request.getId();

        TeamRequest existing = byUUID.get(owner);
        if (existing != null) {
            cancelRequest(existing.getId());
        }

        byUUID.put(owner, request);
        byId.put(id, request);

        // start timer to auto-remove
        AutoCancelTask task = new AutoCancelTask(id);
        task.runTaskLater(plugin, autoCancelTime);
        timers.put(id, task);

        Player player = Bukkit.getPlayer(request.getOwner());

        if (player != null) {
            player.sendMessage(ADDED_REQUEST);
        }

        TextComponent others = new TextComponent(Joiner.on(", ").join(request.getOthers()));
        others.setColor(ChatColor.DARK_PURPLE);

        TextComponent accept = new TextComponent("Accept");
        accept.setColor(ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teamrequest accept " + request.getId()));

        TextComponent deny = new TextComponent("Deny");
        deny.setColor(ChatColor.RED);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teamrequest deny " + request.getId()));

        TextComponent component = new TextComponent("New request (ID: " + request.getId() + ") from " + request.getOwnerName() + " to team with: ");
        component.setColor(ChatColor.GRAY);
        component.addExtra(others);
        component.addExtra(" ");
        component.addExtra(accept);
        component.addExtra(" | ");
        component.addExtra(deny);

        broadcast(component);
    }

    protected void broadcast(BaseComponent... components) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(ADMIN_PERMISSION)) {
                player.spigot().sendMessage(components);
            }
        }
    }

    protected void broadcast(String... messages) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(ADMIN_PERMISSION)) {
                player.sendMessage(messages);
            }
        }
    }

    protected class AutoCancelTask extends BukkitRunnable {

        protected final int id;

        public AutoCancelTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            cancelRequest(id);
        }
    }
}
