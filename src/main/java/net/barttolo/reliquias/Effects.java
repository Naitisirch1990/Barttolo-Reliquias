package net.barttolo.reliquias;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.GameRules;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.*;
import java.util.*;
public final class Effects {
 private final Map<Player,Regen> regen=new WeakHashMap<>();
 private final Set<Object> adjusted=Collections.newSetFromMap(new WeakHashMap<>());
 private static final TagKey<MobEffect> IMMUNE=TagKey.create(Registries.MOB_EFFECT,new ResourceLocation(Reliquias.ID,"duration_immune"));
 private static final class Regen{double progress;int threshold;}
 @SubscribeEvent public void clone(PlayerEvent.Clone e){Progress.clone(e.getOriginal(),e.getEntity());}
 @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e){restore(e.getEntity());}
 @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent e){restore(e.getEntity());}
 @SubscribeEvent public void dimension(PlayerEvent.PlayerChangedDimensionEvent e){restore(e.getEntity());}
 private void restore(Player p){if(p instanceof ServerPlayer s){Progress.applyHealth(s);Network.sync(s);Rewards.deliver(s);}}
 @SubscribeEvent(priority=EventPriority.LOW) public void hurt(LivingHurtEvent e){if(e.getEntity().level().isClientSide)return;var source=e.getSource();if(source.is(DamageTypes.GENERIC_KILL)||source.is(DamageTypes.FELL_OUT_OF_WORLD)||source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))return;float amount=e.getAmount();if(e.getEntity() instanceof Player p)amount*=1f-.01f*Progress.level(p,RelicType.OBSIDIAN);if(source.is(DamageTypes.PLAYER_ATTACK)&&source.getDirectEntity() instanceof Player p)amount*=1f+.02f*Progress.level(p,RelicType.WARRIOR);e.setAmount(amount);}
 @SubscribeEvent public void effect(MobEffectEvent.Applicable e){if(!(e.getEntity() instanceof ServerPlayer p))return;var effect=e.getEffectInstance();var type=effect.getEffect();if(type.isInstantenous()||type.getCategory()!=MobEffectCategory.HARMFUL||effect.isInfiniteDuration()||effect.getDuration()<1||net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.tags().getTag(IMMUNE).contains(type)||!adjusted.add(effect))return;int n=Progress.level(p,RelicType.GUARDIAN);if(n>0)effect.duration=Math.max(1,(int)Math.floor(effect.getDuration()*(1-.05*n)));}
 @SubscribeEvent public void tick(TickEvent.PlayerTickEvent e){if(e.phase!=TickEvent.Phase.START||!(e.player instanceof ServerPlayer p))return;if(p.tickCount%100==0)Rewards.deliver(p);int n=Progress.level(p,RelicType.PHOENIX);var food=p.getFoodData();boolean eligible=n>0&&p.isAlive()&&!p.isSpectator()&&p.getHealth()>0&&p.getHealth()<p.getMaxHealth()&&p.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)&&food.getFoodLevel()>=18;int threshold=food.getFoodLevel()>=20&&food.getSaturationLevel()>0?10:80;if(!eligible){if(regen.remove(p)!=null)food.tickTimer=0;return;}var state=regen.computeIfAbsent(p,k->new Regen());if(state.threshold!=threshold){state.progress=0;state.threshold=threshold;}state.progress+=1+.05*n;if(state.progress>=threshold){state.progress-=threshold;food.tickTimer=threshold-1;}else food.tickTimer=0;}
}
