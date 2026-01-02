package futchem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Formations {
	private static final Map<String, Formation> REGISTRY = new HashMap<>();

	static {
		// 3-back formations
		register("3-1-4-2", Position.GK, Position.CB, Position.CB, Position.CB, Position.CDM, Position.RM, Position.CM,
				Position.CM, Position.LM, Position.ST, Position.ST);

		register("3-4-1-2", Position.GK, Position.CB, Position.CB, Position.CB, Position.RM, Position.CM, Position.CM,
				Position.LM, Position.CAM, Position.ST, Position.ST);

		register("3-4-2-1", Position.GK, Position.CB, Position.CB, Position.CB, Position.RM, Position.CM, Position.CM,
				Position.LM, Position.CAM, Position.CAM, Position.ST);

		register("3-5-2", Position.GK, Position.CB, Position.CB, Position.CB, Position.RM, Position.CM, Position.CDM,
				Position.CM, Position.LM, Position.ST, Position.ST);

		register("3-4-3", Position.GK, Position.CB, Position.CB, Position.CB, Position.RM, Position.CM, Position.CM,
				Position.LM, Position.RW, Position.ST, Position.LW);

		// 4-back formations
		register("4-1-2-1-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM,
				Position.RM, Position.LM, Position.CAM, Position.ST, Position.ST);

		register("4-1-2-1-2(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM,
				Position.CM, Position.CM, Position.CAM, Position.ST, Position.ST);

		register("4-1-3-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CAM,
				Position.CAM, Position.CAM, Position.ST, Position.ST);

		register("4-1-4-1", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.RM,
				Position.CM, Position.CM, Position.LM, Position.ST);

		register("4-2-1-3", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CDM,
				Position.CAM, Position.RW, Position.ST, Position.LW);

		register("4-2-2-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CDM,
				Position.CAM, Position.CAM, Position.ST, Position.ST);

		register("4-2-3-1", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CDM,
				Position.CAM, Position.CAM, Position.CAM, Position.ST);

		register("4-2-3-1(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM,
				Position.CDM, Position.RM, Position.CAM, Position.LM, Position.ST);

		register("4-2-4", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CDM,
				Position.RW, Position.LW, Position.ST, Position.ST);

		register("4-3-1-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CM, Position.CM,
				Position.CM, Position.CAM, Position.ST, Position.ST);

		register("4-3-2-1", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CM, Position.CM,
				Position.CM, Position.RW, Position.ST, Position.LW);

		register("4-3-3", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CM, Position.CM,
				Position.CM, Position.RW, Position.ST, Position.LW);

		register("4-3-3(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM, Position.CM,
				Position.CM, Position.RW, Position.ST, Position.LW);

		register("4-3-3(3)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CDM,
				Position.CDM, Position.CM, Position.RW, Position.ST, Position.LW);

		register("4-3-3(4)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.CM, Position.CM,
				Position.CAM, Position.RW, Position.ST, Position.LW);

		register("4-4-1-1(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.RM,
				Position.CM, Position.CM, Position.LM, Position.CAM, Position.ST);

		register("4-4-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.RM, Position.CM,
				Position.CM, Position.LM, Position.ST, Position.ST);

		register("4-4-2(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.RM, Position.CDM,
				Position.CDM, Position.LM, Position.ST, Position.ST);

		register("4-5-1", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.RM, Position.CM,
				Position.CM, Position.CM, Position.LM, Position.ST);

		register("4-5-1(2)", Position.GK, Position.RB, Position.CB, Position.CB, Position.LB, Position.RM, Position.CM,
				Position.CAM, Position.CM, Position.LM, Position.ST);

		// 5-back formations
		register("5-2-1-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.CB, Position.LB, Position.CM,
				Position.CM, Position.CAM, Position.ST, Position.ST);

		register("5-2-3", Position.GK, Position.RB, Position.CB, Position.CB, Position.CB, Position.LB, Position.CM,
				Position.CM, Position.RW, Position.ST, Position.LW);

		register("5-3-2", Position.GK, Position.RB, Position.CB, Position.CB, Position.CB, Position.LB, Position.CM,
				Position.CM, Position.CM, Position.ST, Position.ST);

		register("5-4-1", Position.GK, Position.RB, Position.CB, Position.CB, Position.CB, Position.LB, Position.RM,
				Position.CM, Position.CM, Position.LM, Position.ST);

	}

	private static void register(String name, Position... positions) {
		REGISTRY.put(name, new Formation(name, List.of(positions)));
	}

	public static Formation get(String name) {
		Formation f = REGISTRY.get(name);
		if (f == null) throw new IllegalArgumentException("Unknown formation: " + name);
		return f;
	}

	public static List<String> getAvailableFormations() { return List.copyOf(REGISTRY.keySet()); }
}
