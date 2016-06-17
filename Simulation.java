package rebellion;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

// SIMULATION
// Defines a simulation based on the given parameters. Initialises the
// simulation, runs through all steps, and manages positions
public class Simulation {

	// Simulation constants
	public static final double K = 2.3;
	public static final double THRESHOLD = 0.1;

	// Simulation attributes
	private int xSize;
	private int ySize;
	private int steps;
	private int numberOfCops;
	private int numberOfAgents;
	private double vision;
	private double legitimacy;
	private int maxJailTerm;
	private boolean wraparound;
	private boolean movement;

	// Simulation data structures
	private Position[][] grid;
	private ArrayList<Person> persons;

	private PrintWriter output;
	private String outputFile;

	// Constructor - Sets parameters, and builds the positions, cops, and agents
	public Simulation(int xSize, int ySize, int steps, double copDensity,
						double agentDensity, double vision, double legitimacy,
						int maxJailTerm, boolean wraparound, boolean movement,
						String outputFile) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.steps = steps;
		this.numberOfCops = (int)Math.round(xSize * ySize * copDensity);
		this.numberOfAgents = (int)Math.round(xSize * ySize * agentDensity);
		this.vision = vision;
		this.legitimacy = legitimacy;
		this.maxJailTerm = maxJailTerm;
		this.wraparound = wraparound;
		this.movement = movement;
		this.outputFile = outputFile;

		// Construct the grid and its positions
		grid = new Position[xSize][ySize];
		for(int i = 0; i < xSize; i++) {
			for(int j = 0; j < ySize; j++) {
				grid[i][j] = new Position(i, j);
			}
		}

		// Construct the array of persons
		persons = new ArrayList<Person>();
		for(int i = 0; i < numberOfCops; i++) {
			Position position = getEmptyPosition();
			Person person = new Cop(position);
			persons.add(person);
			position.addPerson(person);
		}

		for(int i = 0; i < numberOfAgents; i++) {
			Position position = getEmptyPosition();
			Person person = new Agent(position);
			persons.add(person);
			position.addPerson(person);
		}

	}

	// Runs through the simulation steps, printing required output
	public void run() {

		// Initialise the output writer and print heading
		try {
			output = new PrintWriter(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {}
		output.println("Time" + "\t" + "Quiet" + "\t" + "Jailed" + "\t" + "Active");
		printSummaryData(0);

		// Update simulation and print data at each step
		for(int i = 1; i <= steps; i++) {
			step();
			printSummaryData(i);
		}

		output.close();
	}

	// Counts and outputs the agent counts
	public void printSummaryData(int step) {
		int quiet = 0;
		int jailed = 0;
		int active = 0;
		for(int j = 0; j < persons.size(); j++) {
			if(persons.get(j) instanceof Agent) {
				Agent agent = (Agent)persons.get(j);
				if(agent.isQuiet()) {
					quiet++;
				} else if(agent.isJailed()) {
					jailed++;
				} else if(agent.isActive()) {
					active++;
				}
			}
		}

		output.println(step + "\t" + quiet + "\t" + jailed + "\t" + active);
	}

	// Updates the simulation one step
	public void step() {
		// Shuffle the list of persons
		Collections.shuffle(persons);

		// For each person in the list
		for(int i = 0; i < persons.size(); i++) {
			Person person = persons.get(i);

			// Determine person type
			if(person instanceof Agent) {
				Agent agent = (Agent)person;

				// Is the agent quiet or active?
				if(agent.isJailed() == false) {
					Position position = agent.getPosition();
					Position[] neighbourhood;
					
					// If movement is enabled, attempt to move within vision
					if(movement) {
						neighbourhood = getNeighbourhood(position.x, position.y, 
												vision, wraparound);
						agent.move(neighbourhood);
					}

					// Determine behaviour from new neighbourhood
					position = agent.getPosition();
					neighbourhood = 
							getNeighbourhood(position.x, position.y,
												vision, wraparound);
					agent.determineBehaviour(legitimacy, neighbourhood);
				} else {
					agent.reduceJailTerm();
				}

			} else if(person instanceof Cop) {
				Cop cop = (Cop)person;
				
				// Move within the neighbourhood if possible
				Position position = cop.getPosition();
				Position[] neighbourhood =
							getNeighbourhood(position.x, position.y,
												vision, wraparound);
				cop.move(neighbourhood);

				// Enforce within the new neighbourhood
				position = cop.getPosition();
				neighbourhood = 
							getNeighbourhood(position.x, position.y,
												vision, wraparound);
				cop.enforce(maxJailTerm, neighbourhood);

			}

		}

	}

	// Finds an unoccupied position randomly on the grid
	public Position getEmptyPosition() {
		Random random = new Random();
		while(true) {
			int x = random.nextInt(xSize);
			int y = random.nextInt(ySize);

			if(grid[x][y].isOccupied() == false) {
				return grid[x][y];
			}
		}
	}

	// Given a position, vision value and wraparound,
	// returns a neighbourhood of positions
	public Position[] getNeighbourhood(int x, int y, 
										double vision, boolean wraparound) {

		// Return empty array if asked for point outside of world
		if(x < 0 || x >= xSize || y < 0 || y >= ySize) {
			return new Position[0];
		}

		HashSet<Position> positions = new HashSet<Position>();

		// Look at the local square of vision, rather than the whole world
		int vMax = (int)Math.floor(vision);
		for(int i = x - vMax; i <= x + vMax; i++) {

			for(int j = y - vMax; j <= y + vMax; j++) {

				// Check if this point within the vision circle regardless of actual position
				if(Math.hypot(x - i, y - j) <= vision) {

					// Is the point within the world normally
					if(i >= 0 && i < xSize && j >= 0 && j < ySize) {
						positions.add(grid[i][j]);

						// Otherwise move the position if accounting for wraparound
					} else if(wraparound) {

						int xMod = 0;
						int yMod = 0;

						if(i < 0) { xMod = -(((i + 1) / xSize) - 1) * xSize; }
						if(i >= xSize) { xMod = -(i / xSize) * xSize; }
						if(j < 0) { yMod = -(((j + 1) / ySize) - 1) * ySize; }
						if(j >= ySize) { yMod = -(j / ySize) * ySize; }

						positions.add(grid[i + xMod][j + yMod]);

					}

				}

			}

		}

		positions.remove(grid[x][y]);

		Position[] array = new Position[positions.size()];
		array = positions.toArray(array);
		return array;

	}

}