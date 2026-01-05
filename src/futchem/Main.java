package futchem;

import static java.lang.IO.println;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/***
 * * Main class for the FutChem application. Loads player data, optimizes a team
 * based on formations, and displays the results. Kaggle dataset used
 * (fifa_players.csv):
 * https://www.kaggle.com/datasets/joebeachcapital/fifa-players?select=male_players_23.csv
 * Kaggle datased used (fbref_players.csv):
 * https://www.kaggle.com/datasets/jacksonjohannessen/fifa-and-irl-soccer-player-data?select=fifa_fbref_merged.csv
 * * @author Alfonso Espadero
 */

public class Main {
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		println("FutChem Main");
		println("_____________________________________________________________________________\n");

		Manager manager = new Manager("Simeone", "La Liga", "Spain");

		Set<Player> players = new HashSet<>();
		try {
			players = PlayerFactory.loadFromCsv("./data/fbref_players.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Team team = new Team();

		Set<Team> teams = Optimizer.optimizeTeamBT(Formations.getAvailableFormations(), manager, players);

		if (teams.size() > 1)
			println(String.format("Multiple (%s) optimal teams found %s. Displaying one of them.", teams.size(),
					teams.stream().map(t -> t.getFormation().getName()).sorted(Comparator.reverseOrder()).toList()));

		team = teams.iterator().next();

		team.updateChemistry();
		println(team);
		println("Breakdown:");

		for (Slot slot : team.getFormation().getSlots()) {
			Player p = team.getPlayers().get(slot);
			String msg = String.format("%s: %s (%s) - Chemistry: %s (%s %s, %s %s, %s %s)", slot, p.getName(),
					p.getRating(), p.getChemistry(), team.getNationalityChemistry(p), p.getNationality(),
					team.getLeagueChemistry(p), p.getLeague(), team.getClubChemistry(p), p.getClub());
			println(msg);
		}
	}
}
