package com.magicalstorage.init;

import com.magicalstorage.MagicalStorageMod;
import com.magicalstorage.menus.MagicalStorageInterfaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MagicalStorageMod.MODID);

    public static final RegistryObject<MenuType<MagicalStorageInterfaceMenu>> MAGICAL_STORAGE_INTERFACE_MENU = MENUS.register("magical_storage_interface",
            () -> IForgeMenuType.create(MagicalStorageInterfaceMenu::new));
}