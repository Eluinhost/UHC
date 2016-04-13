/*
 * Project: UHC
 * Class: gg.uhc.uhc.util.LocationUtil
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

package gg.uhc.uhc.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public final class LocationUtil {

    protected static final int NETHER_MAX_HEIGHT = 128;

    private LocationUtil() {}

    protected static boolean damagesPlayer(Material material) {
        switch (material) {
            case LAVA:
            case STATIONARY_LAVA:
            case CACTUS:
            case FIRE:
                return true;
            default:
                return false;
        }
    }

    protected static boolean canStandOn(Material material) {
        switch (material) {
            // all of these are 'solid' according to Material but I'm treating them as 'unsolid'
            // as you can fall through them on teleport
            case TRAP_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
            case WOODEN_DOOR:
            case IRON_TRAPDOOR:
            // wtf bukkit you can't even stand on these, they have no hitbox
            case WALL_SIGN:
            case SIGN_POST:
            case STONE_PLATE:
            case WOOD_PLATE:
            case GOLD_PLATE:
            case WALL_BANNER:
            case STANDING_BANNER:
                return false;
            default:
                return material.isSolid();
        }
    }

    /**
     * Checks for the highest safe to stand on block with 2 un-solid blocks above it (excluding above world height).
     *
     * <p>Does not teleport on to non-solid blocks or blocks that can damage the player.</p>
     * <p>Only teleports into water if it is not at head height (feet only)</p>
     * <p>
     *     If the world type is NETHER then searching will start at 128 instead of the world max height to avoid the
     *     bedrock roof.
     * </p>
     *
     * @param world world to check within
     * @param xcoord the x coord to check at
     * @param zcoord the z coord to check at
     * @return -1 if no valid location found, otherwise coordinate with non-air Y coord with 2 air blocks above it
     */
    public static int findHighestTeleportableY(World world, int xcoord, int zcoord) {
        final Location startingLocation = new Location(
                world,
                xcoord,
                world.getEnvironment() == World.Environment.NETHER ? NETHER_MAX_HEIGHT : world.getMaxHeight(),
                zcoord
        );

        boolean above2WasSafe = false;
        boolean aboveWasSafe = false;
        boolean above2WasWater = false;
        boolean aboveWasWater = false;

        Block currentBlock = startingLocation.getBlock();

        Material type;
        boolean damagesPlayer;
        boolean canStandOn;
        boolean aboveAreSafe;
        while (currentBlock.getY() >= 0) {
            type = currentBlock.getType();

            // get info about the current block
            damagesPlayer = damagesPlayer(type);
            canStandOn = canStandOn(type);

            aboveAreSafe = aboveWasSafe && above2WasSafe && !above2WasWater;

            // valid block if it has 2 safe blocks above it, it doesn't damage the player,
            // is safe to stand on and there isn't any water in the head space
            if (aboveAreSafe && !damagesPlayer && canStandOn) {
                return currentBlock.getY();
            }

            // move safe blocks
            above2WasSafe = aboveWasSafe;
            aboveWasSafe = !canStandOn && !damagesPlayer;

            // move water blocks
            above2WasWater = aboveWasWater;
            aboveWasWater = type == Material.WATER || type == Material.STATIONARY_WATER;

            // move down a block and run again
            currentBlock = currentBlock.getRelative(BlockFace.DOWN);
        }

        return -1;
    }
}
