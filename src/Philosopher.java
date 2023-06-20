import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

public class Philosopher implements Runnable {
    enum state{
        THINKING, HUNGRY, EATING, WAITING}

    private final String name;
    private Lock leftFork;
    private Lock rightFork;
    private int seat;
    private state curState;

    public Future task;

    public Philosopher(String name) {
        this.name = name;
    }

    public void seat(Table table, int seat){
        this.leftFork = table.getLeftFork(seat);
        this.rightFork = table.getRightFork(seat);
        this.seat = seat;
        table.seatPhilosopher(this, seat);
    }

    public String getName() {
        return name;
    }

    private boolean isWaiting = false;

    public boolean isWaiting() {
        return isWaiting;
    }

    private boolean hasLeftFork = false;
    private boolean hasRightFork = false;

    @Override
    public void run() {
        try {
            while (true) {
                think();
                pickUpForks();
                eat();
                putDownForks();
            }
        } catch (InterruptedException e) {
            putDownForks();
            isWaiting = false;
//            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        curState = state.THINKING;
        System.out.println(name + " is thinking");
        Thread.sleep((long) (Math.random() * 10 * Main.millisecond));
    }

    private void pickUpForks() {
        isWaiting = true;
        leftFork.lock();
        hasLeftFork = true;
        isWaiting = false;

        try {
            Thread.sleep(4 * Main.millisecond);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        isWaiting = true;
        rightFork.lock();
        hasRightFork = true;
        isWaiting = false;
    }

    public void putDownForks() {
        if (hasLeftFork) {
            leftFork.unlock();
            hasLeftFork = false;
        }
        if (hasRightFork) {
            rightFork.unlock();
            hasRightFork = false;
        }
    }

    private void eat() throws InterruptedException {
        System.out.println(name + " is eating");
        Thread.sleep((long) (Math.random() * 5 * Main.millisecond));
    }
}
