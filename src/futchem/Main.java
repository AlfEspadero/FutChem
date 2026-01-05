package futchem;

import static java.lang.IO.println;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class Main {
	void main() {
		Locale.setDefault(Locale.US);
		println("FutChem Main");
		println("_____________________________________________________________________________\n");

		Formation formation = Formations.get("4-3-3");

		Manager manager = new Manager("Manuolo", "La Liga", "Spain");

		Set<Player> players = new HashSet<>();
		try {
			players = PlayerFactory.loadFromCsv("./data/real_players.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Team team = new Team("My Team", formation, manager);

		//team = Optimizer.optimizePlayers(team, players);
		team = Optimizer.optimizeTeam(Formations.getAvailableFormations(), manager, players);

		/*
		 * for (Player p : players) { Slot slot =
		 * team.getFormation().getSlots().stream() .filter(s ->
		 * p.canPlay(s.getPosition()) && team.getPlayers().get(s) == null).findFirst()
		 * .orElse(null); if (slot != null) { team.addOrReplacePlayer(p, slot); } else {
		 * println("No available slot for player " + p.getName()); } }
		 */

		team.updateChemistry();
		println(team);
		println("Breakdown:");

		for (Slot slot : team.getFormation().getSlots()) {
			Player p = team.getPlayers().get(slot);
			if (p != null) {
				String msg = String.format("%s: %s (%s) - Chemistry: %s (%s %s, %s %s, %s %s)", slot, p.getName(),
						p.getRating(), p.getChemistry(), team.getNationalityChemistry(p), p.getNationality(),
						team.getLeagueChemistry(p), p.getLeague(), team.getClubChemistry(p), p.getClub());
				println(msg);
			}
			else {
				println(slot + ": Empty");
			}
		}
	}
}
