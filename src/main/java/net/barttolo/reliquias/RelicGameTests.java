package net.barttolo.reliquias;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.gametest.*;

@GameTestHolder(Reliquias.ID)
@PrefixGameTestTemplate(false)
public final class RelicGameTests {
  private static ServerPlayer player(GameTestHelper h, String name) {
    var p = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), name));
    p.getInventory().clearContent();
    p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
    return p;
  }

  private static int count(ServerPlayer p) {
    int n = 0;
    for (var t : RelicType.values()) n += p.getInventory().countItem(Reliquias.ITEMS.get(t).get());
    return n;
  }

  @GameTest(template = "empty", timeoutTicks = 100)
  public static void consumptionPersistenceRewards(GameTestHelper h) throws Exception {
    var p = player(h, "RelicTester");
    var q = player(h, "OtherTester");
    for (var t : RelicType.values()) {
      var item = Reliquias.ITEMS.get(t).get();
      p.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 11));
      for (int n = 1; n <= 10; n++) {
        p.getCooldowns().removeCooldown(item);
        item.use(h.getLevel(), p, InteractionHand.MAIN_HAND);
        h.assertTrue(Progress.level(p, t) == n, "Each use must add one level");
      }
      p.getCooldowns().removeCooldown(item);
      item.use(h.getLevel(), p, InteractionHand.MAIN_HAND);
      h.assertTrue(
          p.getMainHandItem().getCount() == 1 && Progress.level(p, t) == 10,
          "Eleventh relic must remain unused");
    }
    h.assertTrue(p.getMaxHealth() == 40, "Titan must add exactly ten hearts");
    Progress.applyHealth(p);
    Progress.applyHealth(p);
    h.assertTrue(p.getMaxHealth() == 40, "Health modifier must be idempotent");
    h.assertTrue(
        Progress.level(q, RelicType.TITAN) == 0 && q.getMaxHealth() == 20,
        "Players must remain independent");
    var snapshot = p.saveWithoutId(new CompoundTag());
    var restored = player(h, "Restored");
    restored.load(snapshot);
    Progress.applyHealth(restored);
    h.assertTrue(
        Progress.level(restored, RelicType.TITAN) == 10 && restored.getMaxHealth() == 40,
        "Levels and attributes must survive full NBT roundtrip");
    p.getInventory().clearContent();
    h.assertTrue(Rewards.missionVictory(p, "overworld_01"), "First victory must claim");
    h.assertTrue(count(p) == 1, "Exactly one mission relic");
    h.assertTrue(
        !Rewards.missionVictory(p, "overworld_01") && count(p) == 1, "Repeat must not duplicate");
    for (int slot = 0; slot < 36; slot++)
      p.getInventory().setItem(slot, new ItemStack(Items.STONE, 64));
    Rewards.missionVictory(p, "overworld_02");
    h.assertTrue(Rewards.status(p).pending() == 1, "Full inventory must preserve pending reward");
    p.getInventory().setItem(0, ItemStack.EMPTY);
    Rewards.deliver(p);
    h.assertTrue(
        count(p) == 1 && Rewards.status(p).pending() == 0, "Pending reward must be delivered once");
    Rewards.deliver(p);
    h.assertTrue(count(p) == 1, "Delivery retry must be idempotent");
    p.getInventory().clearContent();
    for (String id : Rewards.MISSIONS) Rewards.missionVictory(p, id);
    h.assertTrue(Rewards.status(p).claimed() == 23, "23 stable mission IDs");
    Rewards.bossVictory(p);
    h.assertTrue(
        Rewards.status(p).claimed() == 24 && count(p) == 24,
        "Remaining 21 missions plus boss must yield 24 objects");
    Rewards.bossVictory(p);
    h.assertTrue(count(p) == 24, "Boss repeat must not duplicate");
    Progress.clone(p, q);
    h.assertTrue(
        Progress.level(q, RelicType.TITAN) == 10 && Rewards.claimed(q, "overworld_01"),
        "Clone must preserve levels and reward history");
    Progress.set(q, RelicType.TITAN, 0);
    h.assertTrue(Progress.level(p, RelicType.TITAN) == 10, "Cloned data must not be shared");
    var source = p.server.createCommandSourceStack().withPermission(0);
    boolean denied = false;
    try {
      p.server
          .getCommands()
          .getDispatcher()
          .execute("reliquias dar RelicTester corazon_titan 1", source);
    } catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) {
      denied = true;
    }
    h.assertTrue(denied, "Non OP must not grant relics");
    h.succeed();
  }

  @GameTest(template = "empty", timeoutTicks = 100)
  public static void combatEffectsAndRegeneration(GameTestHelper h) {
    var p = player(h, "EffectTester");
    var attacker = player(h, "Attacker");
    Progress.set(p, RelicType.OBSIDIAN, 10);
    Progress.set(attacker, RelicType.WARRIOR, 10);
    var hit = new LivingHurtEvent(p, h.getLevel().damageSources().playerAttack(attacker), 10);
    MinecraftForge.EVENT_BUS.post(hit);
    h.assertTrue(
        Math.abs(hit.getAmount() - 10.8f) < .001, "Melee and resistance must combine once");
    var kill = new LivingHurtEvent(p, h.getLevel().damageSources().genericKill(), 10);
    MinecraftForge.EVENT_BUS.post(kill);
    h.assertTrue(kill.getAmount() == 10, "Administrative kill must bypass resistance");
    Progress.set(p, RelicType.GUARDIAN, 10);
    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
    h.assertTrue(
        p.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getDuration() == 100,
        "Negative effect initial duration must halve");
    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400));
    h.assertTrue(
        p.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getDuration() == 200,
        "Renewal must halve incoming duration once");
    var same = new MobEffectInstance(MobEffects.WEAKNESS, 240);
    p.addEffect(same);
    p.addEffect(same);
    h.assertTrue(
        same.getDuration() == 120, "Reapplying same object must not reduce duration twice");
    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200));
    h.assertTrue(
        p.getEffect(MobEffects.MOVEMENT_SPEED).getDuration() == 200,
        "Beneficial effects must remain unchanged");
    Progress.set(p, RelicType.TITAN, 10);
    Progress.set(p, RelicType.PHOENIX, 10);
    p.setHealth(1);
    var food = p.getFoodData();
    food.setFoodLevel(20);
    food.setSaturation(20);
    h.getLevel().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(true, p.server);
    var listener = new Effects();
    for (int i = 0; i < 100; i++) {
      food.setFoodLevel(20);
      food.setSaturation(20);
      listener.tick(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, p));
      food.tick(p);
    }
    h.assertTrue(
        Math.abs(p.getHealth() - 16) < .01, "100 saturated ticks at +50% must heal 15 points");
    float health = p.getHealth();
    food.setFoodLevel(17);
    for (int i = 0; i < 100; i++) {
      listener.tick(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, p));
      food.tick(p);
    }
    h.assertTrue(p.getHealth() == health, "Low hunger must block regeneration");
    food.setFoodLevel(20);
    food.setSaturation(20);
    h.getLevel().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, p.server);
    for (int i = 0; i < 100; i++) {
      listener.tick(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, p));
      food.tick(p);
    }
    h.assertTrue(p.getHealth() == health, "Disabled gamerule must block regeneration");
    h.getLevel().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(true, p.server);
    h.succeed();
  }
}
