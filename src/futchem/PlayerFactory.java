package futchem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PlayerFactory {

	public static List<Player> loadFromCsv(String filePath) throws IOException {
		List<Player> players = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip header

			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				String name = fields[0].strip();
				// If position is invalid or empty, skip player
				Set<Position> positions = Arrays.stream(fields[1].split(";"))
						.map(String::trim)
						.map(p -> {
							try {
								return Position.valueOf(p);
							} catch (IllegalArgumentException e) {
								return null;
							}
						}).filter(p -> p != null)
						.collect(Collectors.toSet());
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
