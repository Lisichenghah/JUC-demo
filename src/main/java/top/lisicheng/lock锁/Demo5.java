package top.lisicheng.lock锁;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition实现线程通信
 *
 * @author Lisicheng
 */
public class Demo5 {

    public static void main(String[] args) {

        Data2 data2 = new Data2();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data2.sub();
            }

        }, "A").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data2.add();
            }

        }, "B").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data2.add();
            }

        }, "C").start();


        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data2.sub();
            }

        }, "D").start();

    }

}

class Data2 {

    private int num = 0;

    private final Lock lock = new ReentrantLock();

    private final Condition compute = lock.newCondition();


    public void add() {

        lock.lock();

        try {
            while (num != 0) {
                try {
                    compute.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            num++;
            System.out.println(Thread.currentThread().getName() + "+1");
            compute.signalAll();
        } finally {
            lock.unlock();
        }


    }

    public void sub() {

        lock.lock();

        try {
            while (num == 0) {
                try {
                    compute.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            num--;
            System.out.println(Thread.currentThread().getName() + "-1");
            compute.signalAll();
        } finally {
            lock.unlock();
        }

    }
}