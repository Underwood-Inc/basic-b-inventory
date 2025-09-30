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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpgradedMagicalStorageInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(54) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final ContainerData data;
    private int range = 5;
    private int enderPearlsUsed = 0;
    private Map<UUID, BlockPos> linkedInventories = new HashMap<>();
    private int eyeOfEnderCost = 1; // Cost per link
    private boolean ritualPerformed = false;
    private int ritualLevel = 0; // 0 = no ritual, 1 = basic ritual, 2 = advanced ritual

    public UpgradedMagicalStorageInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.UPGRADED_MAGICAL_STORAGE_INTERFACE_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> range;
                    case 1 -> enderPearlsUsed;
                    case 2 -> linkedInventories.size();
                    case 3 -> eyeOfEnderCost;
                    case 4 -> ritualLevel;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> range = value;
                    case 1 -> enderPearlsUsed = value;
                    case 3 -> eyeOfEnderCost = value;
                    case 4 -> ritualLevel = value;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.magicalstorage.upgraded_magical_storage_interface");
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
        nbt.putInt("eye_of_ender_cost", eyeOfEnderCost);
        nbt.putBoolean("ritual_performed", ritualPerformed);
        nbt.putInt("ritual_level", ritualLevel);
        
        CompoundTag linkedTag = new CompoundTag();
        linkedInventories.forEach((uuid, pos) -> {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            linkedTag.put(uuid.toString(), posTag);
        });
        nbt.put("linked_inventories", linkedTag);
        
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        range = nbt.getInt("range");
        enderPearlsUsed = nbt.getInt("ender_pearls_used");
        eyeOfEnderCost = nbt.getInt("eye_of_ender_cost");
        ritualPerformed = nbt.getBoolean("ritual_performed");
        ritualLevel = nbt.getInt("ritual_level");
        
        linkedInventories.clear();
        if (nbt.contains("linked_inventories")) {
            CompoundTag linkedTag = nbt.getCompound("linked_inventories");
            linkedTag.getAllKeys().forEach(uuidStr -> {
                CompoundTag posTag = linkedTag.getCompound(uuidStr);
                BlockPos pos = new BlockPos(
                    posTag.getInt("x"),
                    posTag.getInt("y"),
                    posTag.getInt("z")
                );
                linkedInventories.put(UUID.fromString(uuidStr), pos);
            });
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, UpgradedMagicalStorageInterfaceBlockEntity blockEntity) {
        if (!level.isClientSide) {
            // Scan for nearby inventories and sync them
            blockEntity.scanAndSyncInventories();
            // Also sync with linked inventories
            blockEntity.syncLinkedInventories();
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

        syncInventories(nearbyInventories);
    }

    private void syncLinkedInventories() {
        if (level == null) return;

        for (BlockPos linkedPos : linkedInventories.values()) {
            BlockEntity linkedEntity = level.getBlockEntity(linkedPos);
            if (linkedEntity != null && linkedEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
                // Sync with linked inventory
                linkedEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    // Remote sync logic would go here
                });
            }
        }
    }

    private void syncInventories(List<BlockEntity> inventories) {
        for (BlockEntity inventory : inventories) {
            inventory.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                // Sync logic would go here
            });
        }
    }

    public boolean upgradeRange() {
        if (enderPearlsUsed < 15) { // Upgraded version can use more ender pearls
            enderPearlsUsed++;
            range += 3; // Increase range by 3 blocks per ender pearl
            setChanged();
            return true;
        }
        return false;
    }

    public boolean linkInventory(BlockPos pos, Player player) {
        if (level == null) return false;
        
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            UUID inventoryId = UUID.randomUUID();
            linkedInventories.put(inventoryId, pos);
            setChanged();
            return true;
        }
        return false;
    }

    public boolean unlinkInventory(UUID inventoryId) {
        if (linkedInventories.remove(inventoryId) != null) {
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

    public Map<UUID, BlockPos> getLinkedInventories() {
        return new HashMap<>(linkedInventories);
    }

    public int getEyeOfEnderCost() {
        return eyeOfEnderCost;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean hasRitualBeenPerformed() {
        return ritualPerformed;
    }

    public int getRitualLevel() {
        return ritualLevel;
    }

    public void performRitualUpgrade() {
        ritualPerformed = true;
        ritualLevel = 1;
        
        // Apply ritual benefits
        range += 10; // Increase range significantly
        eyeOfEnderCost = Math.max(1, eyeOfEnderCost - 1); // Reduce linking cost
        
        setChanged();
    }

    public boolean canPerformAdvancedRitual() {
        return ritualPerformed && ritualLevel == 1;
    }

    public void performAdvancedRitual() {
        if (canPerformAdvancedRitual()) {
            ritualLevel = 2;
            range += 20; // Even more range
            eyeOfEnderCost = 1; // Minimal linking cost
            
            setChanged();
        }
    }
}