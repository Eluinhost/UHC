package gg.uhc.uhc.modules.heads;

import java.util.UUID;

public class FallingHeadMetadata {

    protected final UUID uuid;
    protected final BlockFaceXZ direction;

    public FallingHeadMetadata(UUID uuid, BlockFaceXZ direction) {
        this.uuid = uuid;
        this.direction = direction;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BlockFaceXZ getDirection() {
        return direction;
    }
}
