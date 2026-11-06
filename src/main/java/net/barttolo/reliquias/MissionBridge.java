package net.barttolo.reliquias;
import net.minecraft.server.level.ServerPlayer;
/** Injected at the successful, eligible-player victory branch of Misiones 2.6.13. */
public final class MissionBridge {
 private MissionBridge(){}
 public static void onVictory(Object player,int arena){
  if(!(player instanceof ServerPlayer p)||arena<2||arena>14)return;
  Rewards.missionVictory(p,String.format(java.util.Locale.ROOT,"overworld_%02d",arena-1));
 }
}
