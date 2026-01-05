package futchem;

import java.lang.System.Logger.Level;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class Optimizer {

	protected static final Level level = System.getenv("OPTIMIZER_LOG_LEVEL") != null
			? Level.valueOf(System.getenv("OPTIMIZER_LOG_LEVEL"))
			: Level.INFO;
	private static final MyLogger myLogger = new MyLogger();
	private static final int PLAYER_LIMIT = System.getenv("OPTIMIZER_PLAYER_LIMIT") != null
			? Integer.parseInt(System.getenv("OPTIMIZER_PLAYER_LIMIT"))
			: 150;

	private static void debug(String msg) {
		if (level == Level.DEBUG) {
			myLogger.log(Level.DEBUG, msg);
		}
	}

	static Random rnd = new Random(42);

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

	private static Team fillTeamRandom(Team team, Set<Player> players, Random rnd) {
		Team filled = new Team(team);

		for (Slot slot : filled.getFormation().getSlots()) {
			List<Player> candidates = players.stream().filter(p -> p.canPlay(slot.getPosition()))
					.filter(p -> !filled.getPlayers().containsValue(p)).toList();

			if (!candidates.isEmpty()) {
				Player chosen = candidates.get(rnd.nextInt(candidates.size()));
				filled.addOrReplacePlayer(chosen, slot);
			}
		}

		filled.updateChemistry();
		return filled;
	}

	private static Team fillTeamWorst(Team team) {
		Team filled = new Team(team);

		for (Slot slot : filled.getFormation().getSlots()) {
			Player worst = new Player("Dummy" + rnd.nextInt(1000), EnumSet.of(slot.getPosition()), 0,
					"None" + rnd.nextInt(1000), "None" + rnd.nextInt(1000), "None" + rnd.nextInt(1000));
			filled.addOrReplacePlayer(worst, slot);
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

		Team bestTeam = new Team(team);
		double bestScore = bestTeam.getChemistry() + bestTeam.getRating();

		boolean improved = true;
		while (improved) {
			improved = false;

			for (Slot slot : bestTeam.getFormation().getSlots()) {
				Player currentPlayer = bestTeam.getPlayers().get(slot);

				// 2) Replacement-only improvement (team stays full)
				for (Player p : players) {
					if (!p.canPlay(slot.getPosition())) {
						continue;
					}
					// Avoid duplicates; allow replacing the current player with itself? no.
					if (p.equals(currentPlayer)) {
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
		double bestScore = Double.NEGATIVE_INFINITY;

		for (Formation formation : formations) {
			Team team = new Team("Optimized Team", formation, manager);

			List<Team> starts = List.of(fillTeamWorst(team), fillTeamGreedy(team, players),
					fillTeamRandom(team, players, rnd), fillTeamRandom(team, players, rnd)).stream().distinct()
					.collect(Collectors.toList());
			while (starts.size() < 20) {
				starts.add(fillTeamRandom(team, players, rnd));
			}
			for (Team start : starts) {
				
				Set<Player> limitedPlayers = players.stream()
					    .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
					    .limit(PLAYER_LIMIT)
					    .collect(Collectors.toSet());
				
				Team result = optimizePlayersBT(start, limitedPlayers);
				double score = result.getRating() + result.getChemistry();

				if (level.equals(Level.DEBUG)) {
					if (results.containsKey(formation.getName())) {
						Double[] existing = results.get(formation.getName());
						if (score > (existing[0] + existing[1])) {
							results.put(formation.getName(), new Double[] {
									result.getChemistry().doubleValue(),
									result.getRating() });
						}
					}
					else {
						results.put(formation.getName(), new Double[] {
								result.getChemistry().doubleValue(),
								result.getRating() });
					}
				}

				if (score > bestScore) {
					bestScore = score;
					resTeam.clear();
					resTeam.add(result);
					debug(String.format("New best team found with formation %s: Chem: %.0f, Rating: %.2f",
							formation.getName(), result.getChemistry().doubleValue(), result.getRating()));
				}
				else if (score == bestScore) {
					resTeam.add(result);
					debug(String.format("Another optimal team found with formation %s: Chem: %.0f, Rating: %.2f",
							formation.getName(), result.getChemistry().doubleValue(), result.getRating()));
				}
				else {
					debug(String.format("Formation %s resulted in Chem: %.0f, Rating: %.2f", formation.getName(),
							result.getChemistry().doubleValue(), result.getRating()));
				}
			}
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
