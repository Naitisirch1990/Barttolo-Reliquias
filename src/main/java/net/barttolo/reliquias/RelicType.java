package net.barttolo.reliquias;
import net.minecraft.ChatFormatting;
public enum RelicType {
 TITAN("corazon_titan","Corazón de Titán","La esencia vital de los antiguos titanes.","+1 corazón permanente por uso",ChatFormatting.RED),
 OBSIDIAN("piel_obsidiana","Piel de Obsidiana","Una coraza de piedra y poder antiguo.","−1% de daño recibido por uso",ChatFormatting.DARK_PURPLE),
 WARRIOR("esencia_guerrero","Esencia del Guerrero","La voluntad de quienes nunca cedieron.","+2% de daño cuerpo a cuerpo por uso",ChatFormatting.GOLD),
 PHOENIX("sangre_fenix","Sangre de Fénix","Una chispa del ave que renace.","+5% de regeneración natural por uso",ChatFormatting.YELLOW),
 GUARDIAN("voluntad_guardian","Voluntad del Guardián","El juramento de un protector eterno.","−5% de duración de efectos negativos por uso",ChatFormatting.AQUA);
 public final String id,name,lore,benefit; public final ChatFormatting color;
 RelicType(String id,String name,String lore,String benefit,ChatFormatting color){this.id=id;this.name=name;this.lore=lore;this.benefit=benefit;this.color=color;}
 public static RelicType byId(String id){for(var t:values())if(t.id.equals(id))return t;throw new IllegalArgumentException("Tipo desconocido");}
 public String total(int n){return switch(this){case TITAN->"+"+n+" corazones";case OBSIDIAN->"−"+n+"% daño recibido";case WARRIOR->"+"+(2*n)+"% daño cuerpo a cuerpo";case PHOENIX->"+"+(5*n)+"% regeneración natural";case GUARDIAN->"−"+(5*n)+"% duración de efectos negativos";};}
}
