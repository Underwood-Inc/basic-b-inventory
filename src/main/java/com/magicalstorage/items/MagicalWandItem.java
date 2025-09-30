package com.magicalstorage.items;

import com.magicalstorage.blockentities.UpgradedMagicalStorageInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class MagicalWandItem extends Item {
    private static final int MAX_LINK_DISTANCE = 100; // Maximum distance for linking
    private static final int EYE_OF_ENDER_COST = 1; // Cost per link

    public MagicalWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack wand = context.getItemInHand();

        if (level.isClientSide || player == null) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        
        // Check if we're clicking on an upgraded magical storage interface
        if (blockEntity instanceof UpgradedMagicalStorageInterfaceBlockEntity upgradedInterface) {
            return handleInterfaceClick(upgradedInterface, player, wand, level);
        }
        
        // Check if we're clicking on a regular inventory block
        if (blockEntity != null && blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            return handleInventoryClick(blockEntity, player, wand, level);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult handleInterfaceClick(UpgradedMagicalStorageInterfaceBlockEntity interfaceEntity, Player player, ItemStack wand, Level level) {
        // Find nearby upgraded interfaces to link with
        AABB searchArea = new AABB(
                interfaceEntity.getBlockPos().getX() - MAX_LINK_DISTANCE,
                interfaceEntity.getBlockPos().getY() - MAX_LINK_DISTANCE,
                interfaceEntity.getBlockPos().getZ() - MAX_LINK_DISTANCE,
                interfaceEntity.getBlockPos().getX() + MAX_LINK_DISTANCE,
                interfaceEntity.getBlockPos().getY() + MAX_LINK_DISTANCE,
                interfaceEntity.getBlockPos().getZ() + MAX_LINK_DISTANCE
        );

        var nearbyInterfaces = level.getEntitiesOfClass(BlockEntity.class, searchArea)
                .stream()
                .filter(be -> be instanceof UpgradedMagicalStorageInterfaceBlockEntity)
                .filter(be -> !be.equals(interfaceEntity))
                .toList();

        if (nearbyInterfaces.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.magicalstorage.no_interfaces_found"), true);
            return InteractionResult.FAIL;
        }

        // For now, just show a message about linking capability
        player.displayClientMessage(Component.translatable("message.magicalstorage.interface_ready_for_linking"), true);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleInventoryClick(BlockEntity inventoryEntity, Player player, ItemStack wand, Level level) {
        // Find nearby upgraded interfaces
        AABB searchArea = new AABB(
                inventoryEntity.getBlockPos().getX() - MAX_LINK_DISTANCE,
                inventoryEntity.getBlockPos().getY() - MAX_LINK_DISTANCE,
                inventoryEntity.getBlockPos().getZ() - MAX_LINK_DISTANCE,
                inventoryEntity.getBlockPos().getX() + MAX_LINK_DISTANCE,
                inventoryEntity.getBlockPos().getY() + MAX_LINK_DISTANCE,
                inventoryEntity.getBlockPos().getZ() + MAX_LINK_DISTANCE
        );

        var nearbyInterfaces = level.getEntitiesOfClass(BlockEntity.class, searchArea)
                .stream()
                .filter(be -> be instanceof UpgradedMagicalStorageInterfaceBlockEntity)
                .toList();

        if (nearbyInterfaces.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.magicalstorage.no_interfaces_nearby"), true);
            return InteractionResult.FAIL;
        }

        // Check if player has enough eye of ender
        if (!player.getInventory().hasAnyMatching(stack -> 
                stack.getItem() == net.minecraft.world.item.Items.ENDER_EYE && stack.getCount() >= EYE_OF_ENDER_COST)) {
            player.displayClientMessage(Component.translatable("message.magicalstorage.not_enough_eye_of_ender"), true);
            return InteractionResult.FAIL;
        }

        // Consume eye of ender and link inventory
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == net.minecraft.world.item.Items.ENDER_EYE && stack.getCount() >= EYE_OF_ENDER_COST) {
                stack.shrink(EYE_OF_ENDER_COST);
                break;
            }
        }

        // Link the inventory to the nearest interface
        UpgradedMagicalStorageInterfaceBlockEntity nearestInterface = (UpgradedMagicalStorageInterfaceBlockEntity) nearbyInterfaces.get(0);
        if (nearestInterface.linkInventory(inventoryEntity.getBlockPos(), player)) {
            player.displayClientMessage(Component.translatable("message.magicalstorage.inventory_linked"), true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Make the wand shimmer to indicate it's magical
    }
}