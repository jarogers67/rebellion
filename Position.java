package rebellion;
import java.util.ArrayList;

// POSITION
// Represents an x-y location and persons present at that location
public class Position {

	// Position coordinates
	public final int x;
	public final int y;
	
	// Persons Array
	private ArrayList<Person> persons;
	
	// Constructor
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		persons = new ArrayList<Person>();
	}
	
	// Adds a person to the position
	public void addPerson(Person person) {
		persons.add(person);
	}
	
	// Removes a person from the position
	public void removePerson(Person person) {
		persons.remove(person);
	}
	
	// Is the position occupied by a cop or an active agent?
	public boolean isOccupied() {
		for(int i = 0; i < persons.size(); i++) {
			if(persons.get(i) instanceof Cop) {
				return true;
			} else if(((Agent)persons.get(i)).isJailed() == false) {
				return true;
			}
		}
		return false;
	}
	
	// Is the position occupied by a cop?
	public boolean hasCop() {
		for(int i = 0; i < persons.size(); i++) {
			if(persons.get(i) instanceof Cop) {
				return true;
			}
		}
		return false;
	}
	
	// Is the position occupied by an active agent?
	public boolean hasActiveAgent() {
		for(int i = 0; i < persons.size(); i++) {
			if(persons.get(i) instanceof Agent
					&& ((Agent)persons.get(i)).isActive()) {
				return true;
			}
		}
		return false;
	}
	
	// Returns the active agent present at the location?
	public Agent getActiveAgent() {
		for(int i = 0; i < persons.size(); i++) {
			if(persons.get(i) instanceof Agent
					&& ((Agent)persons.get(i)).isActive()) {
				return (Agent)persons.get(i);
			}
		}
		return null;
	}

}