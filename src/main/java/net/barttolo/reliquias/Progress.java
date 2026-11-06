package net.barttolo.reliquias;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;
public final class Progress {
 private static final UUID HEALTH=UUID.fromString("815487ed-a09c-4e17-969f-2a721793f7d2");
 public static CompoundTag data(Player p){var root=p.getPersistentData();if(!root.contains(Player.PERSISTED_NBT_TAG,10))root.put(Player.PERSISTED_NBT_TAG,new CompoundTag());var persisted=root.getCompound(Player.PERSISTED_NBT_TAG);if(!persisted.contains(Reliquias.ID,10))persisted.put(Reliquias.ID,new CompoundTag());var d=persisted.getCompound(Reliquias.ID);d.putInt("schema",1);return d;}
 public static int level(Player p,RelicType t){return Math.max(0,Math.min(10,data(p).getInt(t.id)));}
 public static int[] levels(Player p){return java.util.Arrays.stream(RelicType.values()).mapToInt(t->level(p,t)).toArray();}
 public static void set(ServerPlayer p,RelicType t,int n){data(p).putInt(t.id,Math.max(0,Math.min(10,n)));applyHealth(p);Network.sync(p);save(p);}
 public static void applyHealth(Player p){var a=p.getAttribute(Attributes.MAX_HEALTH);if(a==null)return;int n=level(p,RelicType.TITAN);var m=a.getModifier(HEALTH);if(m!=null&&m.getAmount()==2*n)return;if(m!=null)a.removeModifier(HEALTH);if(n>0)a.addPermanentModifier(new AttributeModifier(HEALTH,"Barttolo Titan",2*n,AttributeModifier.Operation.ADDITION));if(p.getHealth()>p.getMaxHealth())p.setHealth(p.getMaxHealth());}
 public static void save(ServerPlayer p){p.server.getPlayerList().save(p);}
 public static void clone(Player original,Player next){var d=data(original).copy();var root=next.getPersistentData();if(!root.contains(Player.PERSISTED_NBT_TAG,10))root.put(Player.PERSISTED_NBT_TAG,new CompoundTag());root.getCompound(Player.PERSISTED_NBT_TAG).put(Reliquias.ID,d);}
}
