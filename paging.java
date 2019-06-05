/* 
 * Operating Systems Demand Paging
 *
 * Description: This simulated demand pager takes in 6 command line arguments when calling the program. This demand pager
 * features the 3 algorithms as specified in the specification.
 *
 * Please see the readme for any further details specific to my program, and error code explainations.
 *
 * Available Replacement Algorithms (for more details see spec, and lecture notes)
 * Last In First Out (LIFO), Least Recently Used (LRU), and Random (RANDOM).
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class paging {

    // Process Class
    static public class Process{
        double param_A;
        double param_B;
        double param_C;
        int residency;
        int evictions;
        int faults;
        int reference;
        int calls;

        // Constructor for Process
        public Process(double param_A, double param_B, double param_C){
            this.param_A = param_A;
            this.param_B = param_B;
            this.param_C = param_C;
            this.residency = 0;
            this.evictions = 0;
            this.faults = 0;
            this.calls = 0;
        }
    }

    public static void main(String[] args) throws IOException{
        // Get the Arguments (Minimum of 6)
        if (args.length < 6){
            System.out.println("Error Type 1.1: You do not have enough arguments!");
            System.exit(0);
        }
        // This warning stays quiet for 7 exactly, as there are 7 args in the spec input. Where the 7th is the level of
        // debugging, which was NOT needed to be implemented.
        // Exceeding 7, we will print an error message.
        else if (args.length > 7){
            System.out.println("Warning Type 1.2: You have more than 7 arguments! Only the first 6 will be considered.\n");
        }

        // Test to make sure first 5 args are only positive non-decimal integers, otherwise gracefully exit now.
        for (int i = 0; i < 5; i++) {
            for (char c: args[i].toCharArray()) {
                if (!Character.isDigit(c)) {
                    // Not a valid digit in string found!
                    System.out.println("Error Type 1.3: You have a invalid integer argument!");
                    System.out.printf("The first offending argument was argument %d.\n", i + 1);
                    System.exit(0);
                }
            }
        }

        // Initalize and Set Program Params based on input.
        int machineSize = Integer.parseInt(args[0]);
        int pageSize = Integer.parseInt(args[1]);
        int processSize = Integer.parseInt(args[2]);
        int jobMix = Integer.parseInt(args[3]);
        int numReferences = Integer.parseInt(args[4]);
        // Little bit of String "pre-treatment", easier to do in the switch.
        String inputAlgo = args[5].toLowerCase();

        // Ensure the inputAlgo is all letters. (No digits)
        for (char c: inputAlgo.toCharArray()) {
            if (Character.isDigit(c)) {
                // Digit in string found! (Will fail the switch)
                System.out.println("Error Type 2.1: Your replacement algorithm string is invalid (Not all letters)!");
                System.exit(0);
            }
        }

        // Convert Input to "Better Variables"
        int sysPages = machineSize / pageSize;

        int replaceAlgo = 0;
        // Convert String into ints (less string comparisons later)
        switch (inputAlgo) {
            case "lifo":
                replaceAlgo = 1;
                break;
            case "lru":
                replaceAlgo = 2;
                break;
            case "random":
                replaceAlgo = 3;
                break;
            default:
                System.out.println("Error Type 2.2: Your replacement algorithm string is invalid (Not lifo, lru, or random)!");
                System.exit(0);
                break;
        }

        // Initialize processes, running, and memory pages lists.
        // memPageList acts as a pseudo Frame Table.
        ArrayList<Integer> memPageList = new ArrayList<Integer>();
        Process[] processList;
        Queue<Process> running = new LinkedList<Process>();
        int processMax;

        // Based on the job mix, initialize processList
        // Must use if/else to init, even if job mix arg is invalid, as errors will be caught later.
        // Java think there is a chance for failure if they were init inside a switch.
        // This always will fully create processList
        if (jobMix == 1) {
            processList = new Process[1];
            processMax = 0;
        }
        else {
            processList = new Process[4];
            processMax = 3;
        }

        // Based on the job mix, initialize A, B, and C for processes. Invalid args will be caught here.
        // Also generate the first references for each process.
        switch (jobMix) {
            case 1:
                Process p1 = new Process(1.0,0.0,0.0);
                p1.reference = (111) % processSize;
                processList[0] = p1;
                break;
            case 2:
                for (int i = 0; i < 4; i++){
                    Process p2 = new Process(1.0,0.0,0.0);
                    p2.reference = (111*i) % processSize;
                    processList[i] = p2;
                }
                break;
            case 3:
                for (int i = 0; i < 4; i++){
                    Process p3 = new Process(0.0,0.0,0.0);
                    p3.reference = (111*i) % processSize;
                    processList[i] = p3;
                }
                break;
            case 4:
                Process p4a = new Process(0.75,0.25,0.0);
                p4a.reference = (111*1) % processSize;
                processList[0] = p4a;
                Process p4b = new Process(0.75,0.0,0.25);
                p4b.reference = (111*2) % processSize;
                processList[1] = p4b;
                Process p4c = new Process(0.75,0.125,0.125);
                p4c.reference = (111*3) % processSize;
                processList[2] = p4c;
                Process p4d = new Process(0.5,0.125,0.125);
                p4d.reference = (111*4) % processSize;
                processList[3] = p4d;
                break;
            default:
                System.out.println("Error Type 3: Your job mix number is invalid!");
                System.exit(0);
                break;
        }

        // Use a scanner (and next) to get the next item in random-numbers in order
        String filename = "random-numbers";
        File tryFile = new File(filename);
        // Check to see if file exists
        if (!tryFile.exists()) {
            // This case is file DOES NOT EXISTS
            System.out.println("Error Type 4: random-numbers file was not found in this directory. See Readme");
            System.exit(0);
        }
        // If file was found above, we can safely create a scanner.
        Scanner scan = new Scanner(new File(filename));

        // Print Out to show program running parameters (arguemnts).
        System.out.printf("The machine size is %d.\n", machineSize);
        System.out.printf("The page size is %d.\n", pageSize);
        System.out.printf("The process size is %d.\n", processSize);
        System.out.printf("The job mix number is %d.\n", jobMix);
        System.out.printf("The number of references per process is %d.\n", numReferences);
        System.out.printf("The replacement algorithm is %s.\n", inputAlgo);
        // Debug message to match output. (There is NO debugging)
        System.out.println("The level of debugging output is 0");

        // Start (add to queue) processes (1 or 4)
        for (int i = 0; i <= processMax; i++){
            running.add(processList[i]);
        }

        // Map to associate page with access times. Used for residency calculations.
        HashMap<Integer, Integer> accessTime = new HashMap<Integer, Integer>();

        // Process Cycles (Quantum = 3, and Which Cycle for the Process? and Which cycle Total for the system?)
        final int quantum = 3;
        int processTurn = 0;
        int countCycle = 0;

        // Totals for Future. No need to add at end. Do in the meanwhile.
        int totalEvictions = 0;
        int totalResidency = 0;

        // Temporary Variables to Track Pages evicted/selected for eviction.
        int justEvicted;
        int victimFrame;

        // Temporary Booleans to Track Page Hits
        boolean pageHit;

        // Temporary Variable to store the currently worked process and associated pointer's memory reference.
        Process curr;
        int currPointer = -1;
        boolean processUnfinished = true;

        // Track the number of active pages (compare to system pages which is all pages)
        int activePages = 0;

        // This while loop hold the main paging algorithm.
        // Keep running the loop while there are pending processes (not complete)
        while (!running.isEmpty()) {
            processUnfinished = true;
            // Provide the 3 access this cycle. If there is
            for (int i = 0; i < quantum && processUnfinished; i++){
                // Set the current process
                curr = processList[processTurn];
                // Use Integer Division to drop the remainder.
                currPointer = (curr.reference / pageSize) * pageSize + processTurn;

                // ----- CHECK TO SEE IF PAGE IS LOADED ALREADY -----
                // Pretend there is a pageMiss
                pageHit = false;
                // Check all the active pages
                for (int j = 0; j < activePages; j++){
                    // Page WAS loaded.
                    if (memPageList.get(j) == currPointer){
                        pageHit = true;
                        // If we are running LRU, we need to refresh the page (make it fresh again)
                        if (replaceAlgo == 2){
                            // Refresh by deleting and adding to end of the list
                            memPageList.remove(j);
                            memPageList.add(currPointer);
                        }
                    }
                }

                // ----- PAGE WAS NOT LOADED -----
                // If this was a page miss, we need to load the frame.
                if (!pageHit){
                    // Page miss, means a fault for the process.
                    curr.faults++;
                    // Prepare to find the victim frame.
                    victimFrame = 0;

                    // There are no free framed (all system frames are active), need to evict
                    // Prepare to EVICT
                    if(activePages == sysPages){

                        // Determine for random, which frame to evict.
                        if(replaceAlgo == 3){
                            victimFrame = (scan.nextInt())%memPageList.size();
                        }
                        // Remove the frame selected.
                        // Random picks a random number from above.
                        // FIFO and LRU want the first frame (so victimFrame is set to 0 above)
                        // in LRU this is the stalest frame, as we add/refresh to the end of the list.
                        // in LIFO this is the newest frame (lifo is last in first out) as we add to the beginning of the list.
                        justEvicted = memPageList.remove(victimFrame);

                        // Track Residency Changes
                        totalResidency += (countCycle - accessTime.get(justEvicted));
                        processList[justEvicted%pageSize].residency += (countCycle - accessTime.get(justEvicted));

                        // Note the evictions and the now freed page.
                        processList[justEvicted%pageSize].evictions++;
                        totalEvictions++;
                        activePages--;
                    }

                    // ALLOCATE into a free frame
                    // Update time as we now allocate a frame.
                    accessTime.put(currPointer, countCycle);
                    switch (replaceAlgo) {
                        case 1:
                            // LIFO adds to the front
                            memPageList.add(0,currPointer);
                            break;
                        case 2:
                            // LRU adds to the BACK
                            memPageList.add(currPointer);
                            break;
                        case 3:
                            // Random adds back to the freed frame.
                            // Or the front if there are still "free" frames.
                            memPageList.add(victimFrame, currPointer);
                            break;
                        default:
                            // Algorithm not match, even though we filtered above.
                            System.out.println("Error Type 5: Memory Corruption. See Readme");
                            System.exit(0);

                    }
                    // Page is allocated, now there is one less free frame.
                    activePages++;
                }

                // ----- GET NEXT MEMORY REFERENCE -----
                // Determine the next reference based on A, B, C and 4 cases outlined in the spec.
                double y = scan.nextInt() / (Integer.MAX_VALUE + 1d);
                // Case 1
                if (y < curr.param_A) {
                    curr.reference =  (curr.reference + 1) % processSize;
                }
                // Case 2
                // Use note 1 (from lab spec, to use correct modulo)
                else if (y < (curr.param_A + curr.param_B)) {
                    curr.reference = (curr.reference - 5 + processSize) % processSize;
                }
                // Case 3
                else if(y < curr.param_A + curr.param_B+ curr.param_C) {
                    curr.reference = (curr.reference + 4) % processSize;
                }
                // Case 4
                else{
                    curr.reference = (scan.nextInt()) % processSize;
                }

                // Reference is now complete, so increment the cycle for the system.
                countCycle++;

                // Also Record the competition of one reference call cycle for the process.
                processList[processTurn].calls++;

                // If the process has made a sufficient number of calls, it will remove itself from the number of
                // running processes. And note that it is now not unfinished (double negative)
                if(processList[processTurn].calls == numReferences) {
                    running.poll();
                    processUnfinished = false;
                }
            }

            // Completed a Process Cycle (running though all process for the quantum)
            if (processTurn == processMax) {
                processTurn = 0;
            }

            // Otherwise there are still processes not yet run for this iteration.
            else {
                processTurn++;
            }
        }
        // Close Scanner at end of simulator
        scan.close();

        // Printout for the completion of the simulation of the pager.
        int sum = 0;
        // Spacer (match output)
        System.out.println();
        // Per Process
        for (int i = 1; i <= processList.length; i++){
            Process p = processList[i-1];
            System.out.printf("Process %d had %d faults", i, p.faults);
            // No Evict Case
            if (p.evictions == 0){
                System.out.printf(".\n     With no evictions, the average residence is undefined.\n");
            }
            // Evict Case
            else{
                double x = (double)  p.residency / p.evictions;
                // Use println to get the same amount of precision as in output
                System.out.println(" and " + x +  " average residency. ");
            }
            sum = sum + p.faults;
        }
        // Spacer (match output)
        System.out.println();
        // Totals
        System.out.printf("The total number of faults is %d", sum);
        // No Evict Case
        if (totalEvictions == 0){
            System.out.printf(".\n     With no evictions, the overall average residency is undefined.\n");
        }
        // Evict Case
        else{
            double y = (double) totalResidency / totalEvictions;
            // Use println to get the same amount of precision as in output
            System.out.println(" and the overall average residency is " + y + ".");
        }
    }
}