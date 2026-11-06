package net.barttolo.reliquias;

import java.util.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

@Mod(Reliquias.ID)
public final class Reliquias {
  public static final String ID = "barttoloreliquias";
  public static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();
  public static final DeferredRegister<Item> REGISTER =
      DeferredRegister.create(ForgeRegistries.ITEMS, ID);
  public static final Map<RelicType, RegistryObject<Item>> ITEMS = new EnumMap<>(RelicType.class);

  static {
    for (var t : RelicType.values()) ITEMS.put(t, REGISTER.register(t.id, () -> new RelicItem(t)));
  }

  private static void creativeContents(net.minecraftforge.event.BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.INGREDIENTS) {
      for (var type : RelicType.values()) event.accept(ITEMS.get(type));
    }
  }

  public Reliquias() {
    var bus = FMLJavaModLoadingContext.get().getModEventBus();
    REGISTER.register(bus);
    bus.addListener(Reliquias::creativeContents);
    Network.init();
    MinecraftForge.EVENT_BUS.register(new Effects());
    MinecraftForge.EVENT_BUS.register(new Commands());
  }
}
