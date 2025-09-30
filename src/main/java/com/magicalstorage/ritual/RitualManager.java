package com.magicalstorage.ritual;

import com.magicalstorage.blockentities.UpgradedMagicalStorageInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class RitualManager {
    private static final Map<Item, Integer> SACRIFICIAL_ITEMS = new HashMap<>();
    
    static {
        SACRIFICIAL_ITEMS.put(Items.DIAMOND, 4);
        SACRIFICIAL_ITEMS.put(Items.EMERALD, 8);
        SACRIFICIAL_ITEMS.put(Items.NETHERITE_INGOT, 1);
        SACRIFICIAL_ITEMS.put(Items.ENDER_EYE, 16);
        SACRIFICIAL_ITEMS.put(Items.GHAST_TEAR, 4);
        SACRIFICIAL_ITEMS.put(Items.BLAZE_ROD, 8);
    }

    public static boolean canPerformRitual(Level level, BlockPos centerPos) {
        // Check if there's an upgraded interface at the center
        if (!(level.getBlockEntity(centerPos) instanceof UpgradedMagicalStorageInterfaceBlockEntity)) {
            return false;
        }

        // Check for ritual pillars in the correct pattern
        return checkRitualPillars(level, centerPos);
    }

    private static boolean checkRitualPillars(Level level, BlockPos centerPos) {
        // Check for 4 pillars in a 3x3 square pattern around the center
        BlockPos[] pillarPositions = {
            centerPos.offset(-1, 0, -1), // North-west
            centerPos.offset(1, 0, -1),  // North-east
            centerPos.offset(-1, 0, 1),  // South-west
            centerPos.offset(1, 0, 1)    // South-east
        };

        for (BlockPos pillarPos : pillarPositions) {
            // Check if there's a ritual pillar (custom block or obsidian)
            if (level.getBlockState(pillarPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static RitualResult performRitual(Level level, BlockPos centerPos) {
        if (!canPerformRitual(level, centerPos)) {
            return RitualResult.FAILED;
        }

        UpgradedMagicalStorageInterfaceBlockEntity interfaceEntity = 
            (UpgradedMagicalStorageInterfaceBlockEntity) level.getBlockEntity(centerPos);
        
        if (interfaceEntity == null) {
            return RitualResult.FAILED;
        }

        // Check if ritual has already been performed
        if (interfaceEntity.hasRitualBeenPerformed()) {
            return RitualResult.ALREADY_PERFORMED;
        }

        // Perform the ritual upgrade
        interfaceEntity.performRitualUpgrade();
        
        // Remove the ritual pillars
        removeRitualPillars(level, centerPos);

        return RitualResult.SUCCESS;
    }

    private static void removeRitualPillars(Level level, BlockPos centerPos) {
        BlockPos[] pillarPositions = {
            centerPos.offset(-1, 0, -1),
            centerPos.offset(1, 0, -1),
            centerPos.offset(-1, 0, 1),
            centerPos.offset(1, 0, 1)
        };

        for (BlockPos pillarPos : pillarPositions) {
            level.setBlock(pillarPos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public static Map<Item, Integer> getSacrificialItems() {
        return new HashMap<>(SACRIFICIAL_ITEMS);
    }

    public enum RitualResult {
        SUCCESS,
        FAILED,
        ALREADY_PERFORMED
    }
}