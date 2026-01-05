package futchem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PlayerFactory {

	private static final Map<String, EnumSet<Position>> ALIASES = Map.of("CF", EnumSet.of(Position.ST, Position.CAM));

	public static EnumSet<Position> parsePositions(String positionsStr) {
		if (positionsStr == null || positionsStr.isBlank()) {
			return EnumSet.noneOf(Position.class);
		}

		EnumSet<Position> result = EnumSet.noneOf(Position.class);

		for (String token : positionsStr.split(";")) {
			token = token.trim();
			if (token.isEmpty()) continue;

			EnumSet<Position> alias = ALIASES.get(token);
			if (alias != null) {
				result.addAll(alias);
				continue;
			}

			try {
				result.add(Position.valueOf(token));
			} catch (IllegalArgumentException ignored) {
			}
		}

		return result;
	}

	public static Player playerFromArray(String[] fields) {
		String name = fields[0].strip();
		// If position is invalid or empty, skip player
		Set<Position> positions = parsePositions(fields[1].strip());
		Integer rating = Integer.parseInt(fields[2].strip());
		String nationality = fields[3].strip();
		String club = fields[4].strip();
		String league = fields[5].strip();
		return new Player(name, positions, rating, nationality, club, league);
	}

	public static Set<Player> loadFromCsv(String filePath) throws IOException {
		Set<Player> players = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip header

			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length < 6) continue;
				Player player = playerFromArray(fields);
				if (player.getPositions().isEmpty()) continue;
				players.add(player);
			}

		}
		return players;
	}
}
