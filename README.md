# FutChem

Squad optimization tool for EA FC Ultimate Team. Given a CSV of players, it (almost) always finds the best formation and player assignments to maximize chemistry and team rating.

## Features

- Supports 25+ formations (3-back, 4-back, 5-back variants)
- Chemistry calculation based on club, nationality, and league links
- Icon and Hero card support
- Backtracking optimizer with greedy/random initialization
- Manager links

## Requirements

- Java 25+

## Usage

1. Prepare a CSV file with player data. Expected columns:
   - `player_name` or `short_name`
   - `player_positions` (semicolon-separated, e.g., `ST;CAM`)
   - `overall`
   - `nationality_name`
   - `club_name`
   - `league_name`

2. Edit `Main.java` to point to your CSV and set your manager preferences:

```java
Manager manager = new Manager("Simeone", "La Liga", "Spain");
players = PlayerFactory.loadFromCsv("./data/your_players.csv");
```

3. Run:

```sh
java -jar target/FutChem.jar
```

Or compile and run from source:

```sh
javac -d bin src/futchem/*.java
java -cp bin futchem.Main
```

## Configuration

Environment variables:

| Variable                 	| Default 	| Description                                 	|
|--------------------------	|---------	|---------------------------------------------	|
| `OPTIMIZER_LOG_LEVEL`    	| INFO    	| Set to  `DEBUG`  for verbose output         	|
| `OPTIMIZER_PLAYER_LIMIT` 	| `150`   	| Max players to consider during optimization 	|


## Data Sources

Sample datasets included in `data/`:

- [FIFA Players (Kaggle)](https://www.kaggle.com/datasets/joebeachcapital/fifa-players)
- [FIFA + FBRef merged (Kaggle)](https://www.kaggle.com/datasets/jacksonjohannessen/fifa-and-irl-soccer-player-data)
- [FIFA 2025 Players (Kaggle)](https://www.kaggle.com/datasets/nyagami/ea-sports-fc-25-database-ratings-and-stats?select=all_players.csv)

## License

See [LICENSE](LICENSE).
