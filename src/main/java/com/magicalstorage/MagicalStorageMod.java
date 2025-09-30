package com.magicalstorage;

import com.magicalstorage.init.BlockInit;
import com.magicalstorage.init.ItemInit;
import com.magicalstorage.init.MenuInit;
import com.magicalstorage.init.BlockEntityInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(MagicalStorageMod.MODID)
public class MagicalStorageMod {
    public static final String MODID = "magicalstorage";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> MAGICAL_STORAGE_TAB = CREATIVE_MODE_TABS.register("magical_storage_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.magicalstorage"))
            .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
            .icon(() -> ItemInit.MAGICAL_STORAGE_INTERFACE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemInit.MAGICAL_STORAGE_INTERFACE.get());
                output.accept(ItemInit.UPGRADED_MAGICAL_STORAGE_INTERFACE.get());
                output.accept(ItemInit.MAGICAL_WAND.get());
            }).build());

    public MagicalStorageMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register registries
        BlockInit.BLOCKS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code here
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting code here
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLCommonSetupEvent event) {
            // Client setup code here
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
                event.accept(ItemInit.MAGICAL_STORAGE_INTERFACE.get());
                event.accept(ItemInit.UPGRADED_MAGICAL_STORAGE_INTERFACE.get());
            }
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(ItemInit.MAGICAL_WAND.get());
            }
        }
    }
}