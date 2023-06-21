import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public final static int millisecond = 1000;
    private final static int numberOfTables = 5;
    private final static int numberOfSeats = 5;
    private final static List<Table> tables = new ArrayList<>();
    private static Philosopher lastMovedPhilosopher = null;
    private static ExecutorService executor;

    public static Random random = new Random();

    public static void startSimulation() {
        for (int i = 0; i < numberOfTables + 1; i++) {
            tables.add(new Table(numberOfSeats));
        }
        executor = Executors.newFixedThreadPool(numberOfTables * numberOfSeats);

        for (int i = 0; i < numberOfTables; i++) {
            for (int j = 0; j < numberOfSeats; j++) {
                Philosopher philosopher = new Philosopher("Phi " + (char)('A' + i * numberOfSeats + j));
                philosopher.seat(tables.get(i), j);
                philosopher.task = executor.submit(philosopher);
            }
        }
        Long startTime = System.currentTimeMillis();
        try {
            while (true) {
                for (Table table : tables) {
                    if (table.isDeadlocked()) {
                        if(table == tables.get(5)) {
                            System.out.println("The sixth table is deadlocked. The last philosopher to move was " + lastMovedPhilosopher.getName());
                            Long endTime = System.currentTimeMillis();
                            float elapsedTime = (endTime - startTime) / 1000;
                            System.out.println("The total elapsed Time is " + elapsedTime + "s");
                            System.exit(0);
                        }

                        Philosopher philosopher = table.getPhilosopher(random.nextInt(5));
                        if(philosopher.task.cancel(true)) {
                            movePhilosopher(philosopher, table, tables.get(numberOfTables));
                        }
                        else{
                            System.out.println("Failed to cancel the task of " + philosopher.getName());
                        }
                    }

//                    if(table != tables.get(5))
//                        System.out.println(table);
                }
//                System.out.println("");

                Thread.sleep(millisecond);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Unexpectedly exited");
        System.exit(0);
    }

    static void movePhilosopher(Philosopher philosopher, Table oldTable, Table newTable) {
        oldTable.removePhilosopher(philosopher);
        if(newTable.isFull()){
            System.out.println("The sixth table is full");
            return;
        }
        int seat;
        do
            seat = random.nextInt(5);
        while (!newTable.isAvailable(seat));
        philosopher.seat(newTable, seat);
        lastMovedPhilosopher = philosopher;
        System.out.println("Moved " + philosopher.getName() + " to the sixth table");
        philosopher.task = executor.submit(philosopher);
    }

    public static void main(String[] args) {
        startSimulation();
    }
}