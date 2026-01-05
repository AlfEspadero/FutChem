package futchem;

import static java.lang.IO.println;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class Main {
	void main() {
		Locale.setDefault(Locale.US);
		println("FutChem Main");
		println("_____________________________________________________________________________\n");

		Manager manager = new Manager("Simeone", "Ligue 1", "France");

		Set<Player> players = new HashSet<>();
		try {
			players = PlayerFactory.loadFromCsv("./data/fifa_players.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Team team = new Team();

		Set<Team> teams = Optimizer.optimizeTeamBT(Formations.getAvailableFormations(), manager, players);

		if (!teams.isEmpty()) {
			if (teams.size() > 1) {
				println(String.format("Multiple (%s) optimal teams found %s. Displaying one of them.", teams.size(),
						teams.stream().map(t -> t.getFormation().getName()).sorted(Comparator.reverseOrder())
								.toList()));
			}
			team = teams.iterator().next();
		}
		if (team == null) {
			println("No team could be optimized with the given players and formations.");
			return;
		}

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
