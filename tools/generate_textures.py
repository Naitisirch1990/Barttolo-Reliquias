from pathlib import Path
from PIL import Image,ImageDraw
import json
root=Path(__file__).resolve().parents[1]
r=root/'src/main/resources';a=r/'assets/barttoloreliquias'
ids=['corazon_titan','piel_obsidiana','esencia_guerrero','sangre_fenix','voluntad_guardian']
names=['Corazón de Titán','Piel de Obsidiana','Esencia del Guerrero','Sangre de Fénix','Voluntad del Guardián']
colors=[('#241325','#e5ad47','#694021','#a82145'),('#160d27','#8861ad','#31233e','#633f98'),('#230e1c','#c5d4da','#5b1f39','#a3324c'),('#391528','#ffd061','#9d352f','#ef7b31'),('#09192d','#e3bd63','#183e70','#367fb1')]
for index,id in enumerate(ids):
 im=Image.new('RGBA',(32,32));d=ImageDraw.Draw(im)
 outline,gold,fill,glow=colors[index]
 d.polygon([(5,4),(15,1),(16,1),(26,4),(27,17),(24,24),(16,30),(8,24),(4,17)],fill=outline)
 d.polygon([(6,5),(16,3),(25,5),(25,17),(22,23),(16,27),(10,23),(6,17)],fill=gold)
 d.polygon([(8,7),(16,5),(23,7),(23,16),(21,21),(16,25),(11,21),(8,16)],fill=fill)
 d.line([(8,7),(16,5),(23,7)],fill='#fff0bd');d.line([(11,22),(16,26),(22,21)],fill='#704c3c')
 for x,y in [(6,6),(25,6),(7,17),(24,17),(16,27)]:d.point((x,y),fill='#fff0b0')
 if index==0:
  d.polygon([(10,10),(13,8),(16,11),(19,8),(22,10),(22,14),(16,21),(10,14)],fill='#520f32');d.polygon([(11,10),(14,10),(16,12),(18,10),(21,10),(21,14),(16,19),(11,14)],fill='#e63758');d.line([(12,11),(14,11),(15,12)],fill='#ffb1a3');d.line([(17,17),(20,14)],fill='#a91d48');d.point((16,18),fill='#ffcaad')
 elif index==1:
  d.polygon([(16,8),(22,13),(19,20),(13,20),(10,13)],fill='#120d20');d.polygon([(16,9),(20,13),(16,18),(12,13)],fill='#9970c9');d.polygon([(16,10),(16,17),(12,13)],fill='#554477');d.line([(11,17),(14,15),(16,19),(20,16)],fill='#ce8aff');d.point((16,11),fill='#ede1ff')
 elif index==2:
  d.line([(11,20),(21,9)],fill='#11182a',width=4);d.line([(12,19),(21,9)],fill='#d8edf0',width=2);d.line([(13,15),(18,20)],fill='#e5b558',width=2);d.line([(11,21),(14,18)],fill='#91482c',width=2);d.point((21,9),fill='#ffffff');d.line([(10,10),(11,9),(12,10)],fill='#ff795c');d.line([(21,22),(22,21)],fill='#ff795c')
 elif index==3:
  d.polygon([(16,7),(19,13),(23,11),(21,18),(17,20),(16,24),(14,20),(10,18),(9,11),(14,14)],fill='#a83a2b');d.polygon([(16,9),(18,15),(22,13),(19,18),(16,21),(12,18),(10,13),(15,16)],fill='#ff9e36');d.polygon([(16,12),(18,17),(16,20),(14,17)],fill='#fff3a5');d.point((16,11),fill='#ffffff')
 else:
  d.polygon([(9,14),(13,10),(19,10),(23,14),(19,18),(13,18)],fill='#071b31');d.polygon([(10,14),(14,11),(18,11),(22,14),(18,17),(14,17)],fill='#80d8e5');d.rectangle((15,11,17,17),fill='#185083');d.rectangle((16,12,17,16),fill='#0a1729');d.point((16,12),fill='#ffffff');d.line([(13,21),(16,23),(19,21)],fill='#e4c77b')
 folder=a/'textures/item';folder.mkdir(parents=True,exist_ok=True);im.save(folder/(id+'.png'))
 folder=a/'models/item';folder.mkdir(parents=True,exist_ok=True);(folder/(id+'.json')).write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':f'barttoloreliquias:item/{id}'}},indent=2))
folder=a/'lang';folder.mkdir(parents=True,exist_ok=True)
(folder/'es_es.json').write_text(json.dumps({f'item.barttoloreliquias.{i}':n for i,n in zip(ids,names)},ensure_ascii=False,indent=2))
(folder/'en_us.json').write_text(json.dumps({f'item.barttoloreliquias.{i}':n for i,n in zip(ids,['Titan Heart','Obsidian Skin','Warrior Essence','Phoenix Blood','Guardian Will'])},indent=2))
folder=r/'data/barttoloreliquias/tags/mob_effect';folder.mkdir(parents=True,exist_ok=True);(folder/'duration_immune.json').write_text('{"replace":false,"values":[]}')
# Exact 32px source textures; nearest-neighbor preview for review.
preview=Image.new('RGBA',(5*160,192),(18,23,35,255))
for index,id in enumerate(ids):preview.alpha_composite(Image.open(a/'textures/item'/f'{id}.png').resize((128,128),Image.Resampling.NEAREST),(index*160+16,24))
(root/'docs').mkdir(exist_ok=True);preview.save(root/'docs/reliquias-preview.png')
