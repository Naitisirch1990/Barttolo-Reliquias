// Optional bridge for the supplied Barttolo Misiones 2.6.13 binary.
// Runs only if the target class is actually loaded. No client victory packets.
function initializeCoreMod() {
    return {
        'barttolo_relic_first_victory': {
            'target': {'type': 'CLASS', 'name': 'net.barttolo.structures.Missions'},
            'transformer': function(node) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var hooks = 0;
                for (var mi = 0; mi < node.methods.size(); mi++) {
                    var method = node.methods.get(mi);
                    if (method.name !== 'settle' || method.desc !== '(Lnet/barttolo/structures/Missions$Arena;Ljava/util/UUID;ZZ)V') continue;
                    var insns = method.instructions.toArray();
                    for (var i = 0; i < insns.length; i++) {
                        var insn = insns[i];
                        if (insn.getOpcode() === Opcodes.INVOKESTATIC && insn.owner === 'net/barttolo/structures/RwesAddon' && insn.name === 'afterWin' && insn.desc === '(Lnet/barttolo/structures/Missions;Lnet/minecraft/server/level/ServerPlayer;I)V') {
                            // Existing stack: Missions, ServerPlayer, arenaNumber.
                            // Duplicate only the last two category-1 values.
                            var hook = new InsnList();
                            hook.add(new InsnNode(Opcodes.DUP2));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 'net/barttolo/reliquias/MissionBridge', 'onVictory', '(Ljava/lang/Object;I)V', false));
                            method.instructions.insertBefore(insn, hook);
                            method.maxStack += 2;
                            hooks++;
                        }
                    }
                }
                if (hooks !== 1) throw new Error('Barttolo Reliquias: incompatible Misiones binary; expected one validated victory hook, found ' + hooks);
                return node;
            }
        }
    };
}
