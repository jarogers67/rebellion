package rebellion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// PERSON
// Defines a generic person with a position and the ability to move
public class Person {
	
	// The person's position
	private Position position;
	
	// Constructor
	public Person(Position position) {
		this.position = position;
	}
	
	// Given a neighbourhood of positions, will attempt to move to one
	public void move(Position[] positions) {
		
		// Shuffle the list of neighbourhood positions
		ArrayList<Position> list = 
				new ArrayList<Position>(Arrays.asList(positions));
		Collections.shuffle(list);
		
		// For each position, check if it is not occupied
		for(int i = 0; i < list.size(); i++) {
			Position newPosition = list.get(i);
			if(newPosition.isOccupied() == false) {
				
				// If so, move there
				setPosition(newPosition);
				return;
			}
		}
	}
	
	// Returns the position
	public Position getPosition() {
		return position;
	}
	
	// Updates both old and new positions as well as the person
	public void setPosition(Position newPosition) {
		Position oldPosition = this.position;
		oldPosition.removePerson(this);
		newPosition.addPerson(this);
		this.position = newPosition;
	}
	
}