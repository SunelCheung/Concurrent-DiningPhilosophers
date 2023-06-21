import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {
    enum state{
        NONE, THINKING, HUNGRY, EATING, WAITING}

    private final String name;
    private ReentrantLock leftFork;
    private ReentrantLock rightFork;
    private int seat;
    private state curState = state.NONE;
    public Future task;

    public Philosopher(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " " + curState + " " + (leftFork.isHeldByCurrentThread()?"L":" ") + seat + (rightFork.isHeldByCurrentThread()?"R":" ");
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

    public boolean isWaiting() {
        return curState == state.WAITING;
    }

//    private AtomicBoolean hasLeftFork = new AtomicBoolean(false);
//    private AtomicBoolean hasRightFork = new AtomicBoolean(false);

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
//            while(true){
                think();
                pickUpForks();
                eat();
                putDownForks();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            putDownForks();
            return;
//            System.out.println("xxxxxxx");
        }
    }

    private void think() throws InterruptedException {
        curState = state.THINKING;
        System.out.println(name + " is thinking");
        Thread.sleep((long) (Math.random() * 10 * Main.millisecond));
    }

    private void pickUpForks() throws InterruptedException {
        curState = state.WAITING;
        leftFork.lockInterruptibly();
//        hasLeftFork.set(true);
        curState = state.HUNGRY;
        Thread.sleep(4 * Main.millisecond);
        curState = state.WAITING;
        rightFork.lockInterruptibly();
//        hasRightFork.set(true);
    }

    public void putDownForks(){
        if (leftFork.isHeldByCurrentThread()) {
            leftFork.unlock();
        }
        if (leftFork.isHeldByCurrentThread()) {
            rightFork.unlock();
        }
        curState = state.NONE;
    }

    private void eat() throws InterruptedException {
        curState = state.EATING;
        System.out.println(name + " is eating");
        Thread.sleep((long) (Math.random() * 5 * Main.millisecond));
    }
}
