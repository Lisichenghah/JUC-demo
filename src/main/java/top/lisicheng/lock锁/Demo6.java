package top.lisicheng.lock锁;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 唤醒某个线程
 *
 * @author Lisicheng
 */
public class Demo6 {

    public static void main(String[] args) {

        Data3 data3 = new Data3();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data3.printA();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data3.printB();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data3.printC();
            }
        }, "C").start();
    }

}

class Data3 {

    private int num;

    private final Lock lock = new ReentrantLock();

    private final Condition conditionA = lock.newCondition();

    private final Condition conditionB = lock.newCondition();

    private final Condition conditionC = lock.newCondition();


    public void printA() {

        lock.lock();

        try {
            while (num != 0) {
                try {
                    conditionA.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            num = 1;
            System.out.println(Thread.currentThread().getName() + "");
            conditionB.signalAll();
        } finally {
            lock.unlock();
        }


    }

    public void printB() {

        lock.lock();

        try {
            while (num != 1) {
                try {
                    conditionB.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            num = 2;
            System.out.println(Thread.currentThread().getName() + "");
            conditionC.signalAll();
        } finally {
            lock.unlock();
        }

    }

    public void printC() {

        lock.lock();

        try {
            while (num != 2) {
                try {
                    conditionC.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            num = 0;
            System.out.println(Thread.currentThread().getName() + "");
            conditionA.signalAll();
        } finally {
            lock.unlock();
        }

    }

}
