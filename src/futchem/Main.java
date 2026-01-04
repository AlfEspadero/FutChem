package futchem;

import static java.lang.IO.println;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


public class Main {

	protected static final Level level = Logger.Level.INFO;
	private static final Logger myLogger = new Logger() {
		@Override
		public void log(Level level, String msg) {
			System.out.println("[" + level + "] " + msg);
		}

		@Override
		public String getName() { return "CustomLogger"; }

		@Override
		public boolean isLoggable(Level level) {
			if (level == Level.ERROR || level == Level.WARNING) {
				return true;
			}
			return false;
		}

		@Override
		public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
			if (thrown != null) {
				System.out.println("[" + level + "] " + msg + " - Exception: " + thrown.getMessage());
			}
			else {
				System.out.println("[" + level + "] " + msg);
			}

		}

		@Override
		public void log(Level level, ResourceBundle bundle, String format, Object... params) {
			String msg = String.format(format, params);
			System.out.println("[" + level + "] " + msg);

		}
	};

	public void debug(String msg) {
		if (level == Level.DEBUG) {
			myLogger.log(Level.DEBUG, msg);
		}
	}

	// Implement backtracking team optimization, filling the team slots to maximize
	// overall chemistry and rating, we dont want to use the same player twice
	// Also we want to avoid infinite recursion so print logs to track progress
	private Team OptimizeTeam(Team team, Set<Player> players) {
		debug("Starting team optimization...");
		Team bestTeam = new Team(team);
		double bestScore = bestTeam.getChemistry() + bestTeam.getRating();
		boolean improved = true;
		while (improved) {
			improved = false;
			for (Slot slot : team.getFormation().getSlots()) {
				Player currentPlayer = team.getPlayers().get(slot);
				for (Player p : players) {
					if (p.canPlay(slot.getPosition())
							&& (currentPlayer == null || !p.getName().equals(currentPlayer.getName()))
							&& !team.getPlayers().containsValue(p)) {
						Team newTeam = new Team(team);
						newTeam.addOrReplacePlayer(p, slot);
						newTeam.updateChemistry();
						double newScore = newTeam.getChemistry() + newTeam.getRating();
						if (newScore > bestScore) {
							bestScore = newScore;
							bestTeam = new Team(newTeam);
							improved = true;
							String msg = String.format(
									"Improved team by adding %s to slot %s. New chem: %s & rating: %.2f", p.getName(),
									slot, newTeam.getChemistry(), newTeam.getRating());
							debug(msg);
						}
					}
				}
			}
			team = new Team(bestTeam);
		}
		return bestTeam;
	}

	void main() {
		Locale.setDefault(Locale.US);
		println("FutChem Main");
		println("_____________________________________________________________________________\n");

		Formation f442 = Formations.get("4-4-2");

		Manager manager = new Manager("Manuolo", "La Liga", "Spain");

		Set<Player> players = new HashSet<>();
		try {
			players = PlayerFactory.loadFromCsv("./data/real_players.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Team team = new Team("My Team", f442, manager);

		team = OptimizeTeam(team, players);

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
