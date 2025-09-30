package com.magicalstorage.init;

import com.magicalstorage.MagicalStorageMod;
import com.magicalstorage.items.MagicalWandItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicalStorageMod.MODID);

    // Block items
    public static final RegistryObject<Item> MAGICAL_STORAGE_INTERFACE = ITEMS.register("magical_storage_interface",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UPGRADED_MAGICAL_STORAGE_INTERFACE = ITEMS.register("upgraded_magical_storage_interface",
            () -> new Item(new Item.Properties()));

    // Tools
    public static final RegistryObject<Item> MAGICAL_WAND = ITEMS.register("magical_wand",
            () -> new MagicalWandItem(new Item.Properties().stacksTo(1)));

    // Register block items
    static {
        BlockInit.registerBlockItems(ITEMS);
    }
}