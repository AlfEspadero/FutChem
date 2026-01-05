package futchem;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Optimizer {

	protected static final Level level = Level.DEBUG;
	private static final MyLogger myLogger = new MyLogger();

	private static void debug(String msg) {
		if (level == Level.DEBUG) {
			myLogger.log(Level.DEBUG, msg);
		}
	}

	// Implement backtracking team optimization, filling the team slots to maximize
	// overall chemistry and rating, we dont want to use the same player twice
	// Also we want to avoid infinite recursion so print logs to track progress
	public static Team optimizePlayers(Team team, Set<Player> players) {
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

	// We can now also implement a more advanced optimization using backtracking not
	// only with players but also checking all formations
	public static Team optimizeTeam(Set<Formation> formations, Manager manager, Set<Player> players) {
		debug("Starting full team optimization...");
		Map<String, Double[]> results = new HashMap<>();
		Team bestTeam = null;
		double bestScore = 0.0;
		for (Formation formation : formations) {
			Team team = new Team("Optimized Team", formation, manager);
			team = optimizePlayers(team, players);
			double teamScore = team.getChemistry() + team.getRating();
			if (teamScore > bestScore) {
				bestScore = teamScore;
				bestTeam = new Team(team);
				String msg = String.format("New best team found with formation %s. Chem: %s & rating: %.2f",
						formation.getName(), team.getChemistry(), team.getRating());
				debug(msg);
			}
			else {
				String msg = String.format("Formation %s resulted in chem: %s & rating: %.2f",
						formation.getName(), team.getChemistry(), team.getRating());
				debug(msg);
			}
			results.put(formation.getName(), new Double[] { team.getChemistry().doubleValue(), team.getRating() });
		}
		debug("Optimization results by formation:");
		Map.Entry<String, Double[]>[] sortedResults = results.entrySet().toArray(new Map.Entry[0]);
		sortedResults = java.util.Arrays.stream(sortedResults)
				.sorted((e1, e2) -> Double.compare(e2.getValue()[0] + e2.getValue()[1], e1.getValue()[0] + e1.getValue()[1]))
				.toArray(Map.Entry[]::new);
		for (Map.Entry<String, Double[]> entry : sortedResults) {
			String msg = String.format("Formation: %s => Chem: %.0f, Rating: %.2f", entry.getKey(),
					entry.getValue()[0], entry.getValue()[1]);
			debug(msg);
		}
		return bestTeam;
	}
}
