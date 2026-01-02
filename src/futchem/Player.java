package futchem;

import java.util.HashSet;
import java.util.Set;


public class Player {
	// Player for EA FC Ultimate Team
	private String name;
	private Set<Position> positions;
	private Integer rating;
	private String nationality;
	private String club;
	private String league;
	private Integer chemistry;

	public Player(String name, Set<Position> positions, Integer rating, String nationality, String club,
			String league) {
		this.name = name;
		this.positions = positions;
		this.rating = rating;
		this.nationality = nationality;
		this.club = club;
		this.league = league;
		this.chemistry = 0;
	}

	// Getters
	public String getName() { return name; }

	public Set<Position> getPositions() { return new HashSet<Position>(positions); }

	public Integer getRating() { return rating; }

	public String getNationality() { return nationality; }

	public String getClub() { return club; }

	public String getLeague() { return league; }

	public Integer getChemistry() { return chemistry; }

	public void setChemistry(Integer chemistry) { this.chemistry = chemistry; }

	public void calculateChemistry(Team team) {
		// Chemistry calculation will consist of:
		// +1/2/3 for same club with 1/3/6 other players
		// +1/2/3 for same nationality with 1/3/6 other players
		// +1/2/3 for same league with 2/4/7 other players
		// Max chemistry is 3 in total
		// Min chemistry is 0
		// Icons and Heroes always have max chemistry and (for now) do not contribute to others' chemistry
		if (isIcon() || isHero()) {
			this.chemistry = 3;
			return;
		}
		int clubCount = 0;
		int nationality = 0;
		int leagueCount = 0;
		for (Player p : team.getPlayers().values()) {
			if (p != null && !p.equals(this) && !p.isIcon() && !p.isHero()) {
				if (p.getClub().equals(this.club)) {
					clubCount++;
				}
				if (p.getNationality().equals(this.nationality)) {
					nationality++;
				}
				if (p.getLeague().equals(this.league)) {
					leagueCount++;
				}
			}
		}
		int clubChemistry = (clubCount >= 6) ? 3 : (clubCount >= 3) ? 2 : (clubCount >= 1) ? 1 : 0;
		int nationalityChemistry = (nationality >= 6) ? 3 : (nationality >= 3) ? 2 : (nationality >= 1) ? 1 : 0;
		int leagueChemistry = (leagueCount >= 7) ? 3 : (leagueCount >= 4) ? 2 : (leagueCount >= 2) ? 1 : 0;
		this.chemistry = Math.min(3, clubChemistry + nationalityChemistry + leagueChemistry); // We put min to ensure max chemistry is 3
	}

	public boolean canPlay(Position position) {
		return positions.contains(position);
	}

	public boolean isIcon() { return club.equals("Icons"); }

	public boolean isHero() { return club.equals("Heroes"); }

	public String toString() {
		return name + " (" + rating + ")";
	}
}
