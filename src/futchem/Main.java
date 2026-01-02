package futchem;

import java.util.ArrayList;
import static java.lang.IO.*;


public class Main {

	void main() {
		println("FutChem Main");

		ArrayList<Player> players = new ArrayList<Player>();
		Team team = new Team("My Team");
		println("Team " + team.getName() + " is at " + team.getChemistry() + " chemistry.");
	}

}
