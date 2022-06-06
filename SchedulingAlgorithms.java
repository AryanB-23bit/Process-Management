import java.io.File;
import java.io.IOException;
import java.util.*;


import javax.swing.JFileChooser;

public class SchedulingAlgorithms {
    String dataFilePath;
    String dataFileName;
    int[] onGoingProcesses;
    int quantum = 100;
    int priority_latest = 1;
    int nProcesses;
    int nPriorities;
    int[] processTime;
    int[] priority;
    int[] arrivalTime;
    int[] waitTime;
    int[] turnaroundTime;
    int[] processed;
    // statistics
    double[] averageWaitTime;

    double[] averageTRTime;
    int time;
    // Bonus problem data structures
    int[] timeSlice = {50, 75, 100, 125, 150, 175, 200, 225, 250};
    double[] bonusWaitTime = new double[9];
    double[] bonusTurnaroundTime = new double[9];

    public SchedulingAlgorithms() {
        nProcesses = 0;
        nPriorities = 0;
    }

    public void openDataFile() {
        dataFilePath = null;
        dataFileName = null;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Open Data File");

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dataFilePath = chooser.getSelectedFile().getPath();
            dataFileName = chooser.getSelectedFile().getName();
        }
        // read data file and copy it to original array
        try {
            readFileIntoArray(dataFilePath);
        } catch (IOException ioe) {
            System.exit(0);
        }
    }

    public void readFileIntoArray(String filePath) throws IOException {
        if (filePath != null) {
            int index = 0;
            averageWaitTime = new double[5];
            averageTRTime = new double[5];

            Scanner integerTextFile = new Scanner(new File(filePath));
            nProcesses = integerTextFile.nextInt();
            processTime = new int[nProcesses];
            priority = new int[nProcesses];
            arrivalTime = new int[nProcesses];
            waitTime = new int[nProcesses];
            turnaroundTime = new int[nProcesses];
            processed = new int[nProcesses];
            onGoingProcesses = new int[nProcesses];


            while (integerTextFile.hasNext()) {
                int i = integerTextFile.nextInt();

                priority[index] = integerTextFile.nextInt();
                if (priority[index] > nPriorities)
                    nPriorities = priority[index];
                arrivalTime[index] = integerTextFile.nextInt();
                processTime[index] = integerTextFile.nextInt();
                index++;
            }
            //  end of file detected
            integerTextFile.close();
        }
    }

    public void FCFS() {
        time = 0;
        double av = 0;
        double tr = 0;
        for (int i = 0; i < nProcesses; i++) {
            turnaroundTime[i] = 0;
            waitTime[i] = 0;
            processed[i] = 0;
        }
        for (int i = 0; i < nProcesses; i++) {
            waitTime[i] = time - arrivalTime[i];
            av += waitTime[i];
            turnaroundTime[i] = time + processTime[i] - arrivalTime[i];
            tr += turnaroundTime[i];

            time = processTime[i] + time;
        }

        averageWaitTime[0] = Math.round(av / nProcesses);
        averageTRTime[0] = Math.round(tr / nProcesses);
    }

    public void SJN() {
        time = 0;
        double av = 0;
        double tr = 0;
        Arrays.fill(processed, 0);
        for (int i = 0; i < nProcesses; i++) {
            int nextShortestProcess = findNextShortest();
            waitTime[nextShortestProcess] = time - arrivalTime[nextShortestProcess];
            av += waitTime[nextShortestProcess];
            turnaroundTime[nextShortestProcess] = time + processTime[nextShortestProcess] - arrivalTime[nextShortestProcess];
            tr += turnaroundTime[nextShortestProcess];

            time = processTime[nextShortestProcess] + time;
            processed[nextShortestProcess] = 1;
        }
        averageWaitTime[1] = Math.round(av / nProcesses);
        averageTRTime[1] = Math.round(tr / nProcesses);
    }

    public int findNextShortest() {
        int shortestProcessTime = 999999999;

        int nextShortestProcess = 0;

        for (int i = 1; i < nProcesses; i++) {
            if (arrivalTime[i] <= time && processed[i] == 0 && processTime[i] < shortestProcessTime) {
                nextShortestProcess = i;
                shortestProcessTime = processTime[i];
            }
        }
        return nextShortestProcess;
    }

    public void roundRobin() {
        int process_done = 0;
        time = 0;
        double av = 0;
        double tr = 0;
        int[] wait = new int[nProcesses];
        int[] time_remaining = new int[nProcesses];

        System.arraycopy(processTime, 0, time_remaining, 0, nProcesses);
        Arrays.fill(processed, 0);

        while (process_done < nProcesses) {
            process_done = 0;
            for (int i = 0; i < nProcesses; i++) {
                if (time_remaining[i] == 0) {
                    process_done++;
                    continue;
                }
                if (processed[i] != 1) {
                    waitTime[i] = time - arrivalTime[i];
                    av += waitTime[i];
                    processed[i] = 1;
                }
                if (time_remaining[i] > quantum) {
                    time += quantum;
                    time_remaining[i] -= quantum;
                } else {
                    time += time_remaining[i];
                    wait[i] = time - processTime[i] - arrivalTime[i];
                    time_remaining[i] = 0;
                    turnaroundTime[i] = wait[i] + processTime[i];
                    tr += turnaroundTime[i];
                }
            }
        }
        averageWaitTime[2] = Math.round(av / nProcesses);
        averageTRTime[2] = Math.round(tr / nProcesses);
    }
    public void priorityQueue() {
        int process_done = 0;
        time = 0;
        double av = 0;
        double tr = 0;
        int[] wait = new int[nProcesses];
        int[] time_remaining = new int[nProcesses];

        System.arraycopy(processTime, 0, time_remaining, 0, nProcesses);
        Arrays.fill(processed, 0);

        while (process_done < nProcesses) {
            int itr = 0;
            Arrays.fill(onGoingProcesses, -1);
            for (int i = 0; i < nProcesses; i++) {
                if (priority[i] == priority_latest) {
                    onGoingProcesses[itr++] = i;
                }
            }
            priority_latest++;

            int process_track = 0;
            while (itr > 0 && process_track < itr) {
                for (int x = 0; x < itr; x++) {
                    int i = onGoingProcesses[x];
                    if (time_remaining[i] == 0 || arrivalTime[i] > time) {
                        continue;
                    }
                    if (processed[i] != 1) {
                        waitTime[i] = time - arrivalTime[i];
                        av += waitTime[i];
                        processed[i] = 1;
                    }
                    if (time_remaining[i] > quantum) {
                        time += quantum;
                        time_remaining[i] -= quantum;
                    } else {
                        time += time_remaining[i];
                        wait[i] = time - processTime[i] - arrivalTime[i];
                        time_remaining[i] = 0;
                        turnaroundTime[i] = wait[i] + processTime[i];
                        tr += turnaroundTime[i];
                        process_track++;
                    }
                }
            }
            process_done += process_track;
        }
        averageWaitTime[3] = Math.round(av / nProcesses);
        averageTRTime[3] = Math.round(tr / nProcesses);
    }
    public void roundRobinBonus() {

    }
}