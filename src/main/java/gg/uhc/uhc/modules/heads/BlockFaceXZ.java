/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.heads.BlockFaceXZ
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
     * @param blockFace the block face to represent.
     */
    BlockFaceXZ(BlockFace blockFace) {
        face = blockFace;
    }

    /**
     * Get the closest block face to the direction.
     *
     * @param lookAngle the direction in radians
     * @return the closest block face in the 2D plane
     */
    public static BlockFaceXZ getClosest(double lookAngle) {
        final BlockFaceXZ[] directions = BlockFaceXZ.values();
        BlockFaceXZ best = directions[0];
        double angle = Math.abs(best.getAngle());
        for (final BlockFaceXZ bfv : BlockFaceXZ.values()) {
            double diff = lookAngle - bfv.getAngle();
            if (diff > Math.PI * 2) {
                diff -= Math.PI * 2;
            } else if (diff < 0) {
                diff += Math.PI * 2;
            }
            if (Math.abs(diff) < angle) {
                best = bfv;
                angle = Math.abs(diff);
            }
        }
        return best;
    }

    /**
     * Gets the closest blockface to the entities facing direction.
     *
     * @param entity the entity
     * @return closest XZ block face
     */
    public static BlockFaceXZ getClosest(Entity entity) {
        return getClosest(Math.toRadians(entity.getLocation().getYaw()));
    }

    /**
     * @return amount of X coordinates.
     */
    public int getX() {
        return -face.getModX();
    }

    /**
     * @return amount of Z-coordinates.
     */
    public int getZ() {
        return face.getModZ();
    }

    /**
     * @return Bukkit BlockFace used in calculations.
     */
    public BlockFace getBlockFace() {
        return face;
    }

    /**
     * @return The angle between the x and z.
     */
    public double getAngle() {
        return StrictMath.atan2(getX(), getZ());
    }
}
