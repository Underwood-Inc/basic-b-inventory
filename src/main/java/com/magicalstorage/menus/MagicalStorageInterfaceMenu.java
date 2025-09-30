package com.magicalstorage.menus;

import com.magicalstorage.blockentities.MagicalStorageInterfaceBlockEntity;
import com.magicalstorage.blockentities.UpgradedMagicalStorageInterfaceBlockEntity;
import com.magicalstorage.init.MenuInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MagicalStorageInterfaceMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final ContainerData data;

    public MagicalStorageInterfaceMenu(int id, Inventory inventory, BlockEntity blockEntity, ContainerData data) {
        super(MenuInit.MAGICAL_STORAGE_INTERFACE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;

        if (blockEntity instanceof MagicalStorageInterfaceBlockEntity) {
            addSlots((MagicalStorageInterfaceBlockEntity) blockEntity);
        } else if (blockEntity instanceof UpgradedMagicalStorageInterfaceBlockEntity) {
            addSlots((UpgradedMagicalStorageInterfaceBlockEntity) blockEntity);
        }

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    public MagicalStorageInterfaceMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    private void addSlots(MagicalStorageInterfaceBlockEntity blockEntity) {
        ItemStackHandler handler = blockEntity.getItemHandler();
        
        // Main storage slots (6 rows of 9)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    private void addSlots(UpgradedMagicalStorageInterfaceBlockEntity blockEntity) {
        ItemStackHandler handler = blockEntity.getItemHandler();
        
        // Main storage slots (6 rows of 9)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new SlotItemHandler(handler, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 140 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < 54) { // Storage slots
            if (!this.moveItemStackTo(sourceStack, 54, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (index < this.slots.size() - 9) { // Player inventory
            if (!this.moveItemStackTo(sourceStack, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < this.slots.size()) { // Player hotbar
            if (!this.moveItemStackTo(sourceStack, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    public int getRange() {
        return data.get(0);
    }

    public int getEnderPearlsUsed() {
        return data.get(1);
    }

    public int getLinkedInventoriesCount() {
        return data.get(2);
    }

    public int getEyeOfEnderCost() {
        return data.get(3);
    }
}