package top.lisicheng.lock锁;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock锁实现方式
 * ReentrantLock可重入锁，默认非公平锁
 */
public class Demo2 {


    public static void main(String[] args) {

        Ticket2 ticket2 = new Ticket2(3);

        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                ticket2.buyTick();
            },String.valueOf(i)).start();
        }

    }

}

class Ticket2 {

    private int num;

    private Lock lock = new ReentrantLock();

    public Ticket2(int num) {
        this.num = num;
    }

    /**
     * 加锁解锁必须固定格式
     */
    public void buyTick() {

        // 加锁
        lock.lock();
        try {
            if (num-- > 0) {
                System.out.println(Thread.currentThread().getName() + "买了一张票");
            } else {
                System.out.println(Thread.currentThread().getName() + "已经卖完了，下次再来吧");
            }
        } finally {
            // 解锁
            lock.unlock();
        }

    }
}