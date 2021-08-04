import java.util.*;
import java.util.Random;

/*
 * File:	RandomScheduling.java
 * Course: 	20HT - Operating Systems - 1DV512
 * Author: 	Lola Barberan, lb223ui
 * Date: 	November 2020
 */

// TODO: put this source code file into a new Java package with meaningful name (e.g., dv512.YourStudentID)!

// You can implement additional fields and methods in code below, but
// you are not allowed to rename or remove any of it!

// Additionally, please remember that you are not allowed to use any third-party libraries

public class RandomScheduling {
	
	public static class ScheduledProcess {
		int processId;
		int burstTime;
		int arrivalMoment;
		
		// The total time the process has waited since its arrival
		int totalWaitingTime;
		
		// The total CPU time the process has used so far
		// (when equal to burstTime -> the process is complete!)
		int allocatedCpuTime;

		public ScheduledProcess(int processId, int burstTime, int arrivalMoment) {
			this.processId = processId;
			this.burstTime = burstTime;
			this.arrivalMoment = arrivalMoment;
		}
		
		// ... add further fields and methods, if necessary
		public void TotalWaitingTime(int totalWaitingTime) {
			this.totalWaitingTime = totalWaitingTime;
		}
	}
		
	// Random number generator that must be used for the simulation
	Random rng = new Random();

	// ... add further fields and methods, if necessary

	public ArrayList<ScheduledProcess> processes = new ArrayList<>();
		
	public RandomScheduling(long rngSeed) {
		this.rng = new Random(rngSeed);
	}
	
	public void reset() {
		// TODO - remove any information from the previous simulation, if necessary
		this.processes = new ArrayList<>();

	}

	public void runNewSimulation(final boolean isPreemptive, final int timeQuantum,
	    final int numProcesses, final int minBurstTime, final int maxBurstTime,
		final int maxArrivalsPerTick, final double probArrival) {

		ArrayList<Integer> processesAT = new ArrayList<>();

		reset();

		int tick = 0;
		while (processes.size() < numProcesses) {
			//ArrayList<Integer> maxBT = new ArrayList<>();
			for (int arrivals = 0; arrivals < maxArrivalsPerTick; arrivals++) {
				int prob = rng.nextInt(100);
				if (processesAT.size() == 0) {
					prob = 0;
				}
				if (processesAT.size() == numProcesses) {
					prob = 100;
				}
				if (prob < (probArrival * 100)) {
					processesAT.add(tick);
					int BurstTime = rng.nextInt(maxBurstTime - minBurstTime) + minBurstTime;
					ScheduledProcess p = new ScheduledProcess(processes.size(), BurstTime, tick);
					if (isPreemptive) {p.allocatedCpuTime = 0;}
					processes.add(p);

				}
			}
			tick++;
		}

		ArrayList<Integer> order = new ArrayList<>();
		order.add(0);
		while(order.size() < numProcesses){
			int position = rng.nextInt(9) + 1;
			if(!order.contains(position)){
				order.add(position);
			}
		}


		if(!isPreemptive){
		for (int p = 0; p < order.size(); p++) {
			int i = order.get(p);
			int at = processes.get(i).arrivalMoment;
			int sum = 0;

			for (int j = 0; j < Math.min(p, processes.size()); j++) {
				sum += processes.get(order.get(j)).burstTime;
			}
			if(at <= sum) {
				processes.get(i).TotalWaitingTime(sum - at);
			}
			else {
				processes.add(processes.get(i));
				processes.remove(i);
				Collections.swap(order, p, p+1);
				p--;
			}
		}

		}

		else {
			boolean close = false;
			while (!close) {
				for (int p = 0; p < numProcesses; p++) {
					int i = order.get(p);
					int bt = processes.get(i).burstTime;

					for (int j = 0; j < timeQuantum; j++) {
						if (processes.get(i).allocatedCpuTime < bt) {
							processes.get(i).allocatedCpuTime += 1;
							for (int k = 0; k < numProcesses; k++) {
								if ((k != i) && (processes.get(k).burstTime != processes.get(k).allocatedCpuTime)) {
									processes.get(k).totalWaitingTime += 1;
								}
							}
						}
					}
				}

				int valid = 0;
				for (int a = 0; a < numProcesses; a++) {
					valid += processes.get(a).burstTime - processes.get(a).allocatedCpuTime;
				}
				if (valid == 0) {
					close = true;
				}
			}

		}

		// TODO:
		// 1. Run a simulation as a loop, with one iteration considered as one unit of time (tick)
		// 2. The simulation should involve the provided number of processes "numProcesses"
		// 3. The simulation loop should finish after the all of the processes have completed
		// 4. On each tick, a new process might arrive with the given probability (chance)
		// 5. However, the max number of new processes per tick is limited
		// by the given value "maxArrivalsPerTick"
		// 6. The burst time of the new process is chosen randomly in the interval
		// between the values "minBurstTime" and "maxBurstTime" (inclusive)

		// 7. When the CPU is idle and no process is running, the scheduler
		// should pick one of the available processes *at random* and start its execution
		// 8. If the preemptive version of the scheduling algorithm is invoked, then it should 
		// allow up to "timeQuantum" time units (loop iterations) to the process,
		// and then preempt the process (pause its execution and return it to the queue)
		
		// If necessary, add additional fields (and methods) to this class to
		// accomodate your solution

		// Note: for all of the random number generation purposes in this method,
		// use "this.rng" !
	}
	
	public void printResults() {
		// TODO:
		// 1. For each process, print its ID, burst time, arrival time, and total waiting time
		// 2. Afterwards, print the complete execution time of the simulation
		// and the average process waiting time
		for(int i = 0; i < processes.size(); i++) {
			System.out.println("ID:" + processes.get(i).processId + ", AT:" + processes.get(i).arrivalMoment + ", BT:" + processes.get(i).burstTime + ", WT:" + processes.get(i).totalWaitingTime);

		}

		System.out.println("Total Execution Time: " + (processes.get(processes.size()-1).totalWaitingTime + processes.get(processes.size()-1).arrivalMoment + processes.get(processes.size()-1).burstTime));

		double average = 0;
		for (int i = 0; i < processes.size(); i++) {
			average += processes.get(i).totalWaitingTime;
		}
		average /= processes.size();
		System.out.println("Average WT: " + average);


	}
		
	
	public static void main(String args[]) {
		// TODO: replace the seed value below with your birth date, e.g., "20001001"
		final long rngSeed = 20000405;
		
		
		// Do not modify the code below — instead, complete the implementation
		// of other methods!
		RandomScheduling scheduler = new RandomScheduling(rngSeed);
		
		final int numSimulations = 5;
		
		final int numProcesses = 10;
		final int minBurstTime = 2;
		final int maxBurstTime = 10;
		final int maxArrivalsPerTick = 2;
		final double probArrival = 0.75;
		
		final int timeQuantum = 2;

		boolean[] preemptionOptions = {false, true};

		for (boolean isPreemptive: preemptionOptions) {

			for (int i = 0; i < numSimulations; i++) {
				System.out.println("Running " + ((isPreemptive) ? "preemptive" : "non-preemptive")
					+ " simulation #" + i);

				scheduler.runNewSimulation(
					isPreemptive, timeQuantum,
					numProcesses,
					minBurstTime, maxBurstTime,
					maxArrivalsPerTick, probArrival);

				System.out.println("Simulation results:"
					+ "\n" + "----------------------");	
				scheduler.printResults();

				System.out.println("\n");
			}
		}		
		
	}
	
}