import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Table {
    private final List<Philosopher> philosophers = new ArrayList<>();
    private int seatedPhilosophers = 0;
    private ReentrantLock[] forks;

    public Table(int numberOfSeats) {
        forks = new ReentrantLock[numberOfSeats];
        for (int i = 0; i < numberOfSeats; i++) {
            forks[i] = new ReentrantLock();
            philosophers.add(null);
        }
    }

    public boolean isFull() {
        return seatedPhilosophers == forks.length;
    }

    public boolean isAvailable(int index) {
        return philosophers.get(index) == null;
    }

    public Philosopher getPhilosopher(int index) {
        return philosophers.get(index);
    }

    public void seatPhilosopher(Philosopher philosopher, int seat) {
        if (philosophers.get(seat) == null) {
            philosophers.set(seat, philosopher);
        } else {
            throw new IllegalArgumentException("Seat " + seat + " is already occupied");
        }
    }

    public void removePhilosopher(Philosopher philosopher) {
        int index = philosophers.indexOf(philosopher);
        if (index == -1)
            throw new IllegalArgumentException(philosopher.getName() + " is not seated at this table");
        philosophers.set(index, null);
    }

    public boolean isDeadlocked() {
        for (Philosopher philosopher : philosophers) {
            if (philosopher == null || !philosopher.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    public ReentrantLock getLeftFork(int seatNumber) {
        return forks[seatNumber];
    }

    public ReentrantLock getRightFork(int seatNumber) {
        return forks[(seatNumber + 1) % forks.length];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Philosopher philosopher : philosophers) {
            sb.append(philosopher == null ? "Empty" : philosopher).append("\t");
        }
        return sb.toString();
    }
}
