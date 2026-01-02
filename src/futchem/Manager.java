package futchem;

public class Manager {
	private String name;
	private String league;
	private String nationality;

	public Manager(String name, String league, String nationality) {
		super();
		this.name = name;
		this.league = league;
		this.nationality = nationality;
	}

	public String getName() { return name; }

	public String getLeague() { return league; }

	public void setLeague(String league) { this.league = league; }

	public String getNationality() { return nationality; }

	@Override
	public String toString() {
		return String.format("Name: %s, League: %s, Nationality: %s", name, league, nationality);
	}

}
