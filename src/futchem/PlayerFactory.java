package futchem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class PlayerFactory {

	public static Set<Player> loadFromCsv(String filePath) throws IOException {
		Set<Player> players = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip header

			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length < 6) continue;
				String name = fields[0].strip();
				// If position is invalid or empty, skip player
				Set<Position> positions = Arrays.stream(fields[1].split(";")).map(String::trim).map(p -> {
					try {
						return Position.valueOf(p);
					} catch (IllegalArgumentException e) {
						return null;
					}
				}).filter(p -> p != null).collect(Collectors.toSet());
				if (positions.isEmpty()) {
					continue;
				}
				Integer rating = Integer.parseInt(fields[2].strip());
				String nationality = fields[3].strip();
				String club = fields[4].strip();
				String league = fields[5].strip();
				players.add(new Player(name, positions, rating, nationality, club, league));
			}

		}
		return players;
	}
}
