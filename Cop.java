package rebellion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// COP
// An extension of Person with the ability to arrest active agents
public class Cop extends Person {

	// Constructor
	public Cop(Position position) {
		super(position);
	}
	
	// Given a neighbourhood of positions, attempts to find an active agent
	// If found, it is arrested and the cop will move to its location
	public void enforce(int jailTerm, Position[] neighbourhood) {
		
		// Shuffle the list of neighbourhood positions
		ArrayList<Position> list = 
				new ArrayList<Position>(Arrays.asList(neighbourhood));
		Collections.shuffle(list);
		
		// For each position, check if it has an active agent
		for(int i = 0; i < list.size(); i++) {
			Position newPosition = list.get(i);
			if(newPosition.hasActiveAgent()) {
				
				// If so, arrest it, set position to new location, and finish
				newPosition.getActiveAgent().arrest(jailTerm);
				setPosition(newPosition);
				return;
			}
		}
	}
	
}
