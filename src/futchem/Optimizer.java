package futchem;

import java.lang.System.Logger.Level;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class Optimizer {

	protected static final Level level = Level.DEBUG;
	private static final MyLogger myLogger = new MyLogger();

	private static void debug(String msg) {
		if (level == Level.DEBUG) {
			myLogger.log(Level.DEBUG, msg);
		}
	}

	/**
	 * Greedy fill: ensure the team is fully populated (no Empty slots) by selecting
	 * the highest-rated valid player for each slot, without duplicates.
	 */
	private static Team fillTeamGreedy(Team team, Set<Player> players) {
		Team filled = new Team(team);

		for (Slot slot : filled.getFormation().getSlots()) {
			// Skip already filled slots
			if (filled.getPlayers().get(slot) != null) {
				continue;
			}

			Player best = null;
			for (Player p : players) {
				if (!p.canPlay(slot.getPosition())) {
					continue;
				}
				if (filled.getPlayers().containsValue(p)) {
					continue;
				}
				if (best == null || p.getRating() > best.getRating()) {
					best = p;
				}
			}

			if (best != null) {
				filled.addOrReplacePlayer(best, slot);
			}
		}

		filled.updateChemistry();
		return filled;
	}

	/**
	 * Local search that keeps the team full: only consider replacing an existing
	 * player in a slot with a new one (no removals).
	 *
	 * Objective stays the same (chemistry + rating), but since we start from a full
	 * team, we won't end up with "almost empty" solutions.
	 */
	public static Team optimizePlayersBT(Team team, Set<Player> players) {
		debug("Starting team optimization...");

		// 1) Always start from a filled team to avoid "empty team" optima
		team = fillTeamGreedy(team, players);

		Team bestTeam = new Team(team);
		double bestScore = bestTeam.getChemistry() + bestTeam.getRating();

		boolean improved = true;
		while (improved) {
			improved = false;

			for (Slot slot : bestTeam.getFormation().getSlots()) {
				Player currentPlayer = bestTeam.getPlayers().get(slot);

				// If a slot is still empty (insufficient player pool), try to fill it
				if (currentPlayer == null) {
					for (Player p : players) {
						if (p.canPlay(slot.getPosition()) && !bestTeam.getPlayers().containsValue(p)) {
							Team candidate = new Team(bestTeam);
							candidate.addOrReplacePlayer(p, slot);
							candidate.updateChemistry();

							double candidateScore = candidate.getChemistry() + candidate.getRating();
							if (candidateScore > bestScore) {
								bestScore = candidateScore;
								bestTeam = new Team(candidate);
								improved = true;
								debug(String.format("Filled slot %s with %s. New chem: %s & rating: %.2f", slot,
										p.getName(), candidate.getChemistry(), candidate.getRating()));
							}
						}
					}
					continue;
				}

				// 2) Replacement-only improvement (team stays full)
				for (Player p : players) {
					if (!p.canPlay(slot.getPosition())) {
						continue;
					}
					// Avoid duplicates; allow replacing the current player with itself? no.
					if (p.getName().equals(currentPlayer.getName())) {
						continue;
					}
					if (bestTeam.getPlayers().containsValue(p)) {
						continue;
					}

					Team candidate = new Team(bestTeam);
					candidate.addOrReplacePlayer(p, slot);
					candidate.updateChemistry();

					double candidateScore = candidate.getChemistry() + candidate.getRating();
					if (candidateScore > bestScore) {
						bestScore = candidateScore;
						bestTeam = new Team(candidate);
						improved = true;
						debug(String.format(
								"Improved team by replacing in slot %s with %s. New chem: %s & rating: %.2f", slot,
								p.getName(), candidate.getChemistry(), candidate.getRating()));
					}
				}
			}
		}

		return bestTeam;
	}

	public static Set<Team> optimizeTeamBT(Set<Formation> formations, Manager manager, Set<Player> players) {
		debug("Starting full team optimization...");
		SortedMap<String, Double[]> results = new TreeMap<>();
		Set<Team> resTeam = new HashSet<>();
		Team bestTeam = null;
		double bestScore = 0.0;

		for (Formation formation : formations) {
			Team team = new Team("Optimized Team", formation, manager);
			team = optimizePlayersBT(team, players);

			double teamScore = team.getChemistry() + team.getRating();
			if (teamScore > bestScore) {
				resTeam.clear();
				bestScore = teamScore;
				bestTeam = new Team(team);
				resTeam.add(bestTeam);
				debug(String.format("New best team found with formation %s. Chem: %s & rating: %.2f",
						formation.getName(), team.getChemistry(), team.getRating()));
			}
			else if (teamScore == bestScore) {
				resTeam.add(new Team(team));
				debug(String.format("Found another team with formation %s matching best score. Chem: %s & rating: %.2f",
						formation.getName(), team.getChemistry(), team.getRating()));
			}
			else {
				debug(String.format("Formation %s resulted in chem: %s \\& rating: %.2f", formation.getName(),
						team.getChemistry(), team.getRating()));
			}

			results.put(formation.getName(), new Double[] {
					team.getChemistry().doubleValue(),
					team.getRating() });
		}

		debug("Optimization results sorted by score:");
		for (Map.Entry<String, Double[]> entry : results.entrySet().stream().sorted((e1, e2) -> {
			double score1 = e1.getValue()[0] + e1.getValue()[1];
			double score2 = e2.getValue()[0] + e2.getValue()[1];
			return Double.compare(score2, score1);
		}).toList()) {
			debug(String.format("Formation: %s => Chem: %.0f, Rating: %.2f", entry.getKey(), entry.getValue()[0],
					entry.getValue()[1]));
		}

		return resTeam;
	}
	
}
