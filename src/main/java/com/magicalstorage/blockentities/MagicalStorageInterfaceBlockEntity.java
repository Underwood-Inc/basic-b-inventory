package com.magicalstorage.blockentities;

import com.magicalstorage.init.BlockEntityInit;
import com.magicalstorage.init.MenuInit;
import com.magicalstorage.menus.MagicalStorageInterfaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MagicalStorageInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(54) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final ContainerData data;
    private int range = 5; // Default range
    private int enderPearlsUsed = 0;

    public MagicalStorageInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.MAGICAL_STORAGE_INTERFACE_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> range;
                    case 1 -> enderPearlsUsed;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> range = value;
                    case 1 -> enderPearlsUsed = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.magicalstorage.magical_storage_interface");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MagicalStorageInterfaceMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("range", range);
        nbt.putInt("ender_pearls_used", enderPearlsUsed);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        range = nbt.getInt("range");
        enderPearlsUsed = nbt.getInt("ender_pearls_used");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MagicalStorageInterfaceBlockEntity blockEntity) {
        if (!level.isClientSide) {
            // Scan for nearby inventories and sync them
            blockEntity.scanAndSyncInventories();
        }
    }

    private void scanAndSyncInventories() {
        if (level == null) return;

        AABB searchArea = new AABB(
                worldPosition.getX() - range, worldPosition.getY() - range, worldPosition.getZ() - range,
                worldPosition.getX() + range, worldPosition.getY() + range, worldPosition.getZ() + range
        );

        List<BlockEntity> nearbyInventories = level.getEntitiesOfClass(BlockEntity.class, searchArea)
                .stream()
                .filter(be -> be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent())
                .toList();

        // Sync items from nearby inventories to our interface
        syncInventories(nearbyInventories);
    }

    private void syncInventories(List<BlockEntity> inventories) {
        // This is a simplified sync - in a real implementation, you'd want more sophisticated logic
        for (BlockEntity inventory : inventories) {
            inventory.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                // Sync logic would go here
                // For now, we'll just mark that we found inventories
            });
        }
    }

    public boolean upgradeRange() {
        if (enderPearlsUsed < 10) { // Max 10 ender pearls
            enderPearlsUsed++;
            range += 2; // Increase range by 2 blocks per ender pearl
            setChanged();
            return true;
        }
        return false;
    }

    public int getRange() {
        return range;
    }

    public int getEnderPearlsUsed() {
        return enderPearlsUsed;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}