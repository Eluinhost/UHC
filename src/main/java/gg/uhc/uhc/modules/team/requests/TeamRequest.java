package gg.uhc.uhc.modules.team.requests;

import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class TeamRequest {

    protected static int globalId = 1;

    protected final int id = globalId++;

    protected final UUID owner;
    protected final String ownerName;
    protected final Set<String> others;

    public TeamRequest(UUID owner, Set<String> others) {
        this.owner = owner;
        this.ownerName = Bukkit.getPlayer(owner).getName();
        this.others = others;
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Set<String> getOthers() {
        return others;
    }
}
