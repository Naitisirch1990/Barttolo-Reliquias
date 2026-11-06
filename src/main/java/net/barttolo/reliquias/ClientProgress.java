package net.barttolo.reliquias;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
@Mod.EventBusSubscriber(modid=Reliquias.ID,value=Dist.CLIENT)
public final class ClientProgress {
 private static int[] levels;
 public static void update(int[] a){levels=a.clone();}
 public static int level(RelicType t){return levels==null?-1:levels[t.ordinal()];}
 @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut e){levels=null;}
}
