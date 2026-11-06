package net.barttolo.reliquias;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
  private static final SimpleChannel CHANNEL =
      NetworkRegistry.newSimpleChannel(
          new ResourceLocation(Reliquias.ID, "progress"), () -> "1", "1"::equals, "1"::equals);

  public static void init() {
    CHANNEL
        .messageBuilder(Sync.class, 0, NetworkDirection.PLAY_TO_CLIENT)
        .encoder(Sync::encode)
        .decoder(Sync::decode)
        .consumerMainThread(Sync::handle)
        .add();
  }

  public static void sync(ServerPlayer p) {
    if (p instanceof net.minecraftforge.common.util.FakePlayer) return;
    CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new Sync(Progress.levels(p)));
  }

  public record Sync(int[] levels) {
    static void encode(Sync p, FriendlyByteBuf b) {
      for (int n : p.levels) b.writeVarInt(n);
    }

    static Sync decode(FriendlyByteBuf b) {
      int[] a = new int[5];
      for (int i = 0; i < 5; i++) a[i] = Math.max(0, Math.min(10, b.readVarInt()));
      return new Sync(a);
    }

    static void handle(Sync p, Supplier<NetworkEvent.Context> context) {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientProgress.update(p.levels));
      context.get().setPacketHandled(true);
    }
  }
}
