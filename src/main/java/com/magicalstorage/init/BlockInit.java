package com.magicalstorage.init;

import com.magicalstorage.MagicalStorageMod;
import com.magicalstorage.blocks.MagicalStorageInterfaceBlock;
import com.magicalstorage.blocks.UpgradedMagicalStorageInterfaceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicalStorageMod.MODID);

    public static final RegistryObject<Block> MAGICAL_STORAGE_INTERFACE = BLOCKS.register("magical_storage_interface",
            () -> new MagicalStorageInterfaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.BLOCK)));

    public static final RegistryObject<Block> UPGRADED_MAGICAL_STORAGE_INTERFACE = BLOCKS.register("upgraded_magical_storage_interface",
            () -> new UpgradedMagicalStorageInterfaceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .strength(4.0f, 8.0f)
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.BLOCK)));

    // Helper method to register block items
    public static void registerBlockItems(DeferredRegister<Item> items) {
        items.register("magical_storage_interface", () -> new BlockItem(MAGICAL_STORAGE_INTERFACE.get(), new Item.Properties()));
        items.register("upgraded_magical_storage_interface", () -> new BlockItem(UPGRADED_MAGICAL_STORAGE_INTERFACE.get(), new Item.Properties()));
    }
}