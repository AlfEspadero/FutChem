package futchem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Formation {
	private String name;
	private List<Slot> slots;

	public Formation(String name, List<Position> positions) {
		this.name = name;
		this.slots = new ArrayList<>(11);
		Map<Position, Integer> counts = new HashMap<>();
		for (Position p : positions) {
			int idx = counts.getOrDefault(p, 0);
			slots.add(new Slot(p, idx));
			counts.put(p, idx + 1);
		}
	}

	public String getName() { return name; }

	public List<Slot> getSlots() { return List.copyOf(slots); }
}
