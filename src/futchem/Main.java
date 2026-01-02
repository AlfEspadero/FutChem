package futchem;

import static java.lang.IO.println;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Main {

	void main() {
		Locale.setDefault(Locale.US);
		println("FutChem Main");
		println("_____________________________________________________________________________\n");

		Formation f442 = Formations.get("4-4-2");

		Manager manager = new Manager("Manuolo", "NWSL", "USA");

		List<Player> players = new ArrayList<>();
		try {
			players = PlayerFactory.loadFromCsv("./data/players.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Team team = new Team("My Team", f442, manager);
		for (Player p : players) {
			Slot slot = team.getFormation().getSlots().stream()
					.filter(s -> p.canPlay(s.getPosition()) && team.getPlayers().get(s) == null).findFirst()
					.orElse(null);
			if (slot != null) {
				team.addOrReplacePlayer(p, slot);
			}
			else {
				println("No available slot for player " + p.getName());
			}
		}

		team.updateChemistry();
		println(team);
		println("Breakdown:");
		for (Slot slot : team.getFormation().getSlots()) {
			Player p = team.getPlayers().get(slot);
			if (p != null) {
				println(slot + ": " + p.getName() + " - Chemistry: " + p.getChemistry());
			}
			else {
				println(slot + ": Empty");
			}
		}
	}
}
