import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import javax.script.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

/** Runs the actual shipped transformer against a supplied compatible Misiones JAR. */
public final class VerifyMissionBridge {
  public static void main(String[] args) throws Exception {
    var engine = new ScriptEngineManager().getEngineByName("nashorn");
    if (engine == null) throw new IllegalStateException("Nashorn dependency missing");
    engine.eval(Files.readString(Path.of(args[0])));
    var definitions = (ScriptObjectMirror) ((Invocable) engine).invokeFunction("initializeCoreMod");
    var definition = (ScriptObjectMirror) definitions.get("barttolo_relic_first_victory");
    var transform = (ScriptObjectMirror) definition.get("transformer");
    ClassNode node = new ClassNode();
    try (var jar = new JarFile(args[1])) {
      new ClassReader(jar.getInputStream(jar.getJarEntry("net/barttolo/structures/Missions.class")))
          .accept(node, 0);
    }
    transform.call(null, node);
    int calls = 0;
    for (var m : node.methods) {
      if (m.name.equals("settle")) new Analyzer<>(new BasicVerifier()).analyze(node.name, m);
      for (var i : m.instructions)
        if (i instanceof MethodInsnNode call
            && call.owner.equals("net/barttolo/reliquias/MissionBridge")) calls++;
    }
    if (calls != 1) throw new AssertionError("Expected one victory hook: " + calls);
    boolean rejected = false;
    try {
      transform.call(null, new ClassNode());
    } catch (Exception expected) {
      rejected = true;
    }
    if (!rejected) throw new AssertionError("Unsupported target was not rejected");
    System.out.println(
        "PASS: actual Nashorn transformer inserts one hook, bytecode stack verifies, incompatible"
            + " target rejected.");
  }
}
