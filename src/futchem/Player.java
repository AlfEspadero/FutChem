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

	public Set<Position> getPositions() { return new HashSet<>(positions); }

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
		// Icons always have max chemistry and count as 1 towards all leagues, and count
		// as 2 towards those with same nationality
		// Heroes always have max chemistry and count as 2 towards those with the same
		// league, and normally towards same nationality
		if (isIcon() || isHero()) {
			this.chemistry = 3;
			return;
		}
		int clubCount = 0;
		int nationalityCount = 0;
		int leagueCount = 0;
		for (Player p : team.getPlayers().values()) {

			if (p != null && !p.equals(this)) {
				if (p.isIcon()) {
					leagueCount++;
					nationalityCount += p.getNationality().equals(this.nationality) ? 2 : 0;
					continue;
				}

				if (p.isHero()) {
					leagueCount += p.getLeague().equals(this.league) ? 2 : 0;
					nationalityCount += p.getNationality().equals(this.nationality) ? 1 : 0;
					continue;
				}

				if (p.getClub().equals(this.club)) {
					clubCount++;
				}
				if (p.getNationality().equals(this.nationality)) {
					nationalityCount++;
				}
				if (p.getLeague().equals(this.league)) {
					leagueCount++;
				}
			}
		}
		nationalityCount += team.getManager().getNationality().equals(this.nationality) ? 1 : 0;
		leagueCount += team.getManager().getLeague().equals(this.league) ? 1 : 0;

		int clubChemistry = (clubCount >= 6) ? 3 : (clubCount >= 3) ? 2 : (clubCount >= 1) ? 1 : 0;
		int nationalityChemistry = (nationalityCount >= 6) ? 3
				: (nationalityCount >= 3) ? 2
				: (nationalityCount >= 1) ? 1
				: 0;
		int leagueChemistry = (leagueCount >= 7) ? 3 : (leagueCount >= 4) ? 2 : (leagueCount >= 2) ? 1 : 0;
		// We put min to ensure max chemistry is 3
		this.chemistry = Math.min(3, clubChemistry + nationalityChemistry + leagueChemistry);
	}

	public boolean canPlay(Position position) {
		return positions.contains(position);
	}

	public boolean isIcon() { return club.equals("Icons"); }

	public boolean isHero() { return club.equals("Heroes"); }

	@Override
	public String toString() {
		return name + " (" + rating + ")";
	}
}
