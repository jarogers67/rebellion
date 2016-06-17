package rebellion;
import java.util.Random;

// AGENT
// An extension of Person with personal attributes
// May become active, or be jailed over time
public class Agent extends Person {

	// Private attributes
	private double riskAversion;
	private double perceivedHardship;
	private boolean active;
	private int jailTerm;
	
	// Constructor - Defines individual characteristics
	public Agent(Position position) {
		super(position);
		Random random = new Random();
		riskAversion = random.nextDouble();
		perceivedHardship = random.nextDouble();
		active = false;
		jailTerm = 0;
	}
	
	// Given the value of government legitimacy and a neighbourhood of positions
	// determines if the agent will become acive or not
	public void determineBehaviour(double legitimacy, Position[] positions) {

		// Begin counts
		int cops = 0;
		int activeAgents = 1;

		// For all positions, update the counts
		for(int i = 0; i < positions.length; i++) {
			if(positions[i].hasActiveAgent()) {
				activeAgents++;
			} else if(positions[i].hasCop()) {
				cops++;
			}
		}
		
		// Calculate the values as per the model
		double grievance = perceivedHardship * (1 - legitimacy);
		double estimatedArrestProbability =
				1 - Math.exp(-Simulation.K * Math.floor(cops / activeAgents));
		double motivation =
				grievance - riskAversion * estimatedArrestProbability;
		
		// Is motivation greater than the threshold?
		active = (motivation > Simulation.THRESHOLD);
	}
	
	// Arrest this agent for up to the provided jail term
	public void arrest(int jailTerm) {
		active = false;
		Random random = new Random();
		this.jailTerm = random.nextInt(jailTerm);
	}
	
	// Reduce the jail term by 1
	public void reduceJailTerm() {
		jailTerm--;
	}
	
	// Is the agent active?
	public boolean isActive() {
		return active;
	}
	
	// Is the agent jailed?
	public boolean isJailed() {
		return (!active && jailTerm > 0);
	}
	
	// Is the agent quiet?
	public boolean isQuiet() {
		return (!active && jailTerm == 0);
	}
	
}