package net.barttolo.reliquias;
import net.minecraft.world.item.*;
import net.minecraft.world.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.List;
import org.jetbrains.annotations.Nullable;
public final class RelicItem extends Item {
 private final RelicType type;
 public RelicItem(RelicType t){super(new Properties().stacksTo(64).rarity(Rarity.EPIC));type=t;}
 @Override public Component getName(ItemStack stack){return Component.literal(type.name).withStyle(type.color);}
 @Override public InteractionResultHolder<ItemStack> use(Level world,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(world.isClientSide)return InteractionResultHolder.success(stack);var p=(ServerPlayer)player;if(p.getCooldowns().isOnCooldown(this))return InteractionResultHolder.fail(stack);int n=Progress.level(p,type);if(n>=10){message(p,"Poder máximo alcanzado: 10/10. El objeto no se consume.",ChatFormatting.YELLOW);return InteractionResultHolder.fail(stack);}Progress.data(p).putInt(type.id,n+1);Progress.applyHealth(p);if(type==RelicType.TITAN)p.heal(2);stack.shrink(1);for(var item:Reliquias.ITEMS.values())p.getCooldowns().addCooldown(item.get(),10);Network.sync(p);Progress.save(p);message(p,"¡MEJORA PERMANENTE DESBLOQUEADA! "+type.name+": "+(n+1)+"/10 — "+type.total(n+1),type.color);return InteractionResultHolder.consume(stack);}
 static void message(Player p,String text,ChatFormatting color){p.displayClientMessage(Component.literal("[Reliquias] "+text).withStyle(color),false);}
 @Override public void appendHoverText(ItemStack stack,@Nullable Level level,List<Component> lines,TooltipFlag flags){lines.add(Component.literal(type.lore).withStyle(ChatFormatting.GRAY));lines.add(Component.literal(type.benefit+" · Permanente").withStyle(type.color));int n=ClientProgress.level(type);lines.add(Component.literal(n<0?"Sincronizando progreso…":"Tu progreso: "+n+"/10").withStyle(ChatFormatting.WHITE));if(n>=0)lines.add(Component.literal("Total: "+type.total(n)).withStyle(ChatFormatting.GRAY));if(n==10)lines.add(Component.literal("Poder máximo alcanzado").withStyle(ChatFormatting.GOLD));lines.add(Component.literal("Se consume al utilizarse").withStyle(ChatFormatting.YELLOW));lines.add(Component.literal("Clic derecho para absorber su poder").withStyle(ChatFormatting.DARK_GRAY));}
}
