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

	public Team(Team team) {
		this.name = team.name;
		this.formation = team.formation;
		this.players = new HashMap<>(team.players);
		this.manager = team.manager;
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

	public void removePlayer(Slot slot) {
		if (players.containsKey(slot)) {
			players.put(slot, null);
		}
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
		return String.format("Team: %s, Formation: %s, Rating: %.2f, Chemistry: %d, Icons: %d, Manager: %s", name,
				formation.getName(), getRating(), getChemistry(), iconCount(), manager.getName());
	}

	// For the following we do the same as in Team.calculateChemistry
	// +1/2/3 for same club with 1/3/6 other players
	// +1/2/3 for same nationality with 1/3/6 other players
	// +1/2/3 for same league with 2/4/7 other players
	// Ignore icons for now

	public Integer getNationalityChemistry(Player p) {
		long val = players.values().stream()
				.filter(player -> player != null && player.getNationality().equals(p.getNationality())).count();
		val += manager.getNationality().equals(p.getNationality()) ? 1 : 0;
		if (val >= 7) return 3;
		if (val >= 4) return 2;
		if (val >= 2) return 1;
		return 0;
	}

	public Integer getLeagueChemistry(Player p) {
		long val = players.values().stream()
				.filter(player -> player != null && player.getLeague().equals(p.getLeague())).count();
		val += manager.getLeague().equals(p.getLeague()) ? 1 : 0;
		if (val >= 8) return 3;
		if (val >= 5) return 2;
		if (val >= 3) return 1;
		return 0;
	}

	public Integer getClubChemistry(Player p) {
		long val = players.values().stream().filter(player -> player != null && player.getClub().equals(p.getClub()))
				.count();
		if (val >= 7) return 3;
		if (val >= 4) return 2;
		if (val >= 2) return 1;
		return 0;
	}

}
