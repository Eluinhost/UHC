package gg.uhc.uhc.modules.heads;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

public enum BlockFaceXZ {

    NORTH(BlockFace.NORTH),
    EAST(BlockFace.EAST),
    SOUTH(BlockFace.SOUTH),
    WEST(BlockFace.WEST),
    NORTH_EAST(BlockFace.NORTH_EAST),
    NORTH_NORTH_EAST(BlockFace.NORTH_NORTH_EAST),
    EAST_NORTH_EAST(BlockFace.EAST_NORTH_EAST),
    NORTH_WEST(BlockFace.NORTH_WEST),
    NORTH_NORTH_WEST(BlockFace.NORTH_NORTH_WEST),
    WEST_NORTH_WEST(BlockFace.WEST_NORTH_WEST),
    SOUTH_EAST(BlockFace.SOUTH_EAST),
    SOUTH_SOUTH_EAST(BlockFace.SOUTH_SOUTH_EAST),
    EAST_SOUTH_EAST(BlockFace.EAST_SOUTH_EAST),
    SOUTH_WEST(BlockFace.SOUTH_WEST),
    SOUTH_SOUTH_WEST(BlockFace.SOUTH_SOUTH_WEST),
    WEST_SOUTH_WEST(BlockFace.WEST_SOUTH_WEST);

    private final BlockFace face;

    /**
     * @param blockFace the block face to represent
     */
    BlockFaceXZ(BlockFace blockFace) {
        face = blockFace;
    }

    /**
     * Get the closest block face to the direction
     *
     * @param lookAngle the direction in radians
     * @return the closest block face in the 2D plane
     */
    public static BlockFaceXZ getClosest(double lookAngle) {
        BlockFaceXZ[] directions = BlockFaceXZ.values();
        BlockFaceXZ best = directions[0];
        double angle = Math.abs(best.getAngle());
        for(BlockFaceXZ bfv : BlockFaceXZ.values()) {
            double a = lookAngle - bfv.getAngle();
            if(a > Math.PI * 2) {
                a -= Math.PI * 2;
            } else if(a < 0) {
                a += Math.PI * 2;
            }
            if(Math.abs(a) < angle) {
                best = bfv;
                angle = Math.abs(a);
            }
        }
        return best;
    }

    /**
     * Gets the closest blockface to the entities facing direction
     *
     * @param entity the entity
     * @return closest XZ block face
     */
    public static BlockFaceXZ getClosest(Entity entity) {
        return getClosest(Math.toRadians(entity.getLocation().getYaw()));
    }

    /**
     * @return amount of X coordinates
     */
    public int getX() {
        return -face.getModX();
    }

    /**
     * @return amount of Z-coordinates
     */
    public int getZ() {
        return face.getModZ();
    }

    /**
     * @return the block face
     */
    public BlockFace getBlockFace() {
        return face;
    }

    /**
     * @return The angle between the x and z
     */
    public double getAngle() {
        return StrictMath.atan2(getX(), getZ());
    }
}
