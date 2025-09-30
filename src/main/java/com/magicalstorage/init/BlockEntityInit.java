package com.magicalstorage.init;

import com.magicalstorage.MagicalStorageMod;
import com.magicalstorage.blockentities.MagicalStorageInterfaceBlockEntity;
import com.magicalstorage.blockentities.UpgradedMagicalStorageInterfaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MagicalStorageMod.MODID);

    public static final RegistryObject<BlockEntityType<MagicalStorageInterfaceBlockEntity>> MAGICAL_STORAGE_INTERFACE_BE = BLOCK_ENTITIES.register("magical_storage_interface",
            () -> BlockEntityType.Builder.of(MagicalStorageInterfaceBlockEntity::new, BlockInit.MAGICAL_STORAGE_INTERFACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<UpgradedMagicalStorageInterfaceBlockEntity>> UPGRADED_MAGICAL_STORAGE_INTERFACE_BE = BLOCK_ENTITIES.register("upgraded_magical_storage_interface",
            () -> BlockEntityType.Builder.of(UpgradedMagicalStorageInterfaceBlockEntity::new, BlockInit.UPGRADED_MAGICAL_STORAGE_INTERFACE.get()).build(null));
}