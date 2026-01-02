package futchem;

import java.util.HashMap;
import java.util.Map;


public class Team {
	// Team for EA FC Ultimate Team
	private String name;
	private HashMap<Position,Player> players;
	private Integer chemistry;

	public Team(String name) {
		this.name = name;
		HashMap<Position,Player> mapa = new HashMap<Position,Player>();
		for (Position position : Position.values()) {
			mapa.put(position, null);
		}
		this.players = mapa;
		this.chemistry = 0;
	}

	// Getters
	public String getName() { return name; }

	public Map<Position, Player> getPlayers() { return new HashMap<Position,Player>(players); }

	public Integer getChemistry() { return calculateChemistry(); }
	
	public void addOrReplacePlayer (Player player, Position position) {
		if (player.canPlay(position)) {
			players.put(position, player);
		} else {
			System.out.println("Player " + player.getName() + " cannot play in position " + position);
		}
	}

	public void removePlayer(Player player) {
		players.values().removeIf(p -> p != null && p.equals(player));
	}

	public Integer iconCount() {
		return (int) players.values().stream().filter(player -> player != null && player.isIcon()).count();
	}

	public Integer calculateChemistry() {
		return players.values().stream().filter(player -> player != null).mapToInt(Player::getChemistry).sum();
    }
	
	public void updateChemistry() {
		players.values().stream().filter(player -> player != null).forEach(player -> player.calculateChemistry(this));
	}

	public String toString() {
		return name + " (" + chemistry + ")";
	}

}
