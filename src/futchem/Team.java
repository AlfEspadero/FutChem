package futchem;

import java.util.HashMap;
import java.util.Map;


public class Team {
	private String name;
	private Formation formation;
	private Map<Slot, Player> players;
	private Manager manager;

	public Team(String name, Formation formation, Manager manager) {
		this.name = name;
		this.formation = formation;
		this.players = new HashMap<>();
		for (Slot slot : formation.getSlots()) {
			players.put(slot, null);
		}
		this.manager = manager;
	}

	public String getName() { return name; }

	public Formation getFormation() { return formation; }

	public Map<Slot, Player> getPlayers() { return new HashMap<>(players); }

	public Manager getManager() { return manager; }

	public void setManager(Manager manager) { this.manager = manager; }

	public void addOrReplacePlayer(Player player, Slot slot) {
		if (!players.containsKey(slot)) {
			System.out.println("Slot " + slot + " not in this formation");
			return;
		}
		if (player.canPlay(slot.getPosition())) {
			players.put(slot, player);
		}
		else {
			System.out.println("Player " + player.getName() + " cannot play " + slot.getPosition());
		}
	}

	public void removePlayer(Player player) {
		players.values().removeIf(p -> p != null && p.equals(player));
	}

	public Integer iconCount() {
		return (int) players.values().stream().filter(player -> player != null && player.isIcon()).count();
	}

	public Integer getChemistry() {
		return players.values().stream().filter(player -> player != null).mapToInt(Player::getChemistry).sum();
	}

	public Double getRating() {
		return players.values().stream().filter(player -> player != null).mapToInt(Player::getRating).average()
				.orElse(0.0);
	}

	public void updateChemistry() {
		players.values().stream().filter(player -> player != null).forEach(player -> player.calculateChemistry(this));
	}

	@Override
	public String toString() {
		return String.format("Team: %s, Formation: %s, Rating: %.2f, Chemistry: %d, Icons: %d, Manager: %s",name,
				formation.getName(), getRating(), getChemistry(), iconCount(), manager.getName());
	}

}
