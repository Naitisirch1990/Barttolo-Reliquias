package net.barttolo.reliquias;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import java.util.*;
/** Server API. Call only after authoritative eligibility and victory validation. */
public final class Rewards {
 private Rewards(){}
 public static final String BOSS="boss_interdimensional";
 public static final List<String> MISSIONS;
 static{var ids=new ArrayList<String>();for(var dim:List.of("overworld","nether","end"))for(int n=1;n<=(dim.equals("overworld")?13:5);n++)ids.add(dim+"_"+String.format(java.util.Locale.ROOT,"%02d",n));MISSIONS=List.copyOf(ids);}
 private static CompoundTag records(ServerPlayer p){var d=Progress.data(p);if(!d.contains("rewards",10))d.put("rewards",new CompoundTag());return d.getCompound("rewards");}
 public static boolean claimed(ServerPlayer p,String id){return records(p).contains(id,10);}
 public static boolean missionVictory(ServerPlayer p,String id){if(!MISSIONS.contains(id))throw new IllegalArgumentException("ID de misión desconocido: "+id);return grant(p,id,1);}
 public static boolean bossVictory(ServerPlayer p){return grant(p,BOSS,3);}
 private static boolean grant(ServerPlayer p,String id,int count){if(!p.server.isSameThread())throw new IllegalStateException("La recompensa requiere el hilo del servidor");if(claimed(p,id)){RelicItem.message(p,"Misión completada. La reliquia especial ya fue reclamada.",ChatFormatting.GRAY);return false;}int[] pending=new int[count];for(int i=0;i<count;i++)pending[i]=p.getRandom().nextInt(5);var record=new CompoundTag();record.putIntArray("assigned",pending);record.putIntArray("pending",pending);record.putLong("created",System.currentTimeMillis());records(p).put(id,record);deliver(p);Progress.save(p);RelicItem.message(p,"¡RELIQUIA LEGENDARIA OBTENIDA! "+id+" ×"+count+". Cada recompensa especial se obtiene una sola vez.",ChatFormatting.GOLD);Reliquias.LOG.info("Relic reward {} granted to {} ({})",id,p.getGameProfile().getName(),p.getUUID());return true;}
 public static void deliver(ServerPlayer p){boolean changed=false;for(String id:records(p).getAllKeys()){var record=records(p).getCompound(id);var remaining=new ArrayList<Integer>();for(int index:record.getIntArray("pending")){if(index<0||index>=5){Reliquias.LOG.error("Invalid pending relic index {} for {}",index,p.getUUID());remaining.add(index);continue;}var stack=new ItemStack(Reliquias.ITEMS.get(RelicType.values()[index]).get());p.getInventory().add(stack);if(!stack.isEmpty())remaining.add(index);else{changed=true;RelicItem.message(p,"Recompensa: "+RelicType.values()[index].name+" ×1.",RelicType.values()[index].color);}}record.putIntArray("pending",remaining.stream().mapToInt(Integer::intValue).toArray());}if(changed){p.inventoryMenu.broadcastChanges();Progress.save(p);}}
 public static ComponentData status(ServerPlayer p){int claimed=0,pending=0;for(String id:records(p).getAllKeys()){claimed++;pending+=records(p).getCompound(id).getIntArray("pending").length;}return new ComponentData(claimed,pending,claimed(p,BOSS));}
 public record ComponentData(int claimed,int pending,boolean boss){}
}
