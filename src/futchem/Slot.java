package futchem;

public class Slot {
	private Position position;
	private int index; // Distinguishes multiple slots of same position (e.g., CB1, CB2)

	public Slot(Position position, int index) {
		this.position = position;
		this.index = index;
	}

	public Position getPosition() { return position; }

	public int getIndex() { return index; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Slot)) return false;
		Slot slot = (Slot) o;
		return index == slot.index && position == slot.position;
	}

	@Override
	public int hashCode() {
		return 31 * position.hashCode() + index;
	}

	@Override
	public String toString() {
		return position.name() + (index > 0 ? String.valueOf(index + 1) : "");
	}
}
