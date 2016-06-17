package rebellion;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

// DRIVER
// Define simulation parameters and run them from here
public class Driver {

	public static void main(String[] args) {
		
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream("input.txt"));
		} catch (FileNotFoundException e) {}
		
		input.nextLine();
		
		while(input.hasNextLine()) {
			// Read the parameter values
			String name = input.next();
			int xSize = input.nextInt();
			int ySize = input.nextInt();
			int steps = input.nextInt();
			double copDensity = input.nextDouble();
			double agentDensity = input.nextDouble();
			double vision = input.nextDouble();
			double legitimacy = input.nextDouble();
			int maxJailTerm = input.nextInt();
			boolean wraparound = input.nextBoolean();
			boolean movement = input.nextBoolean();
			int runs = input.nextInt();
			
			// Run each set of parameters a certain number of times
			for(int i = 1; i <= runs; i++) {
				String outputFile = name + i + ".txt";
				
				System.out.println("Running " + i + "/" + runs + " of " + name);
				
				// Construct and run the simulation
				Simulation simulation = new Simulation(xSize,
														ySize,
														steps,
														copDensity,
														agentDensity,
														vision,
														legitimacy,
														maxJailTerm,
														wraparound,
														movement,
														outputFile);
				simulation.run();
				
			}
			
		}
		
	}
	
}