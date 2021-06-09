package top.lisicheng.八锁问题;

import java.util.concurrent.TimeUnit;

/**
 * 类锁与实例锁
 * 类锁是实例锁是两把锁，类锁是指Class<Phone>
 * 实例锁是Phone的具体实例
 */
public class 类锁与实例锁 {

    public static void main(String[] args) throws InterruptedException {

        Phone3 phone1 = new Phone3();
        Phone3 phone2 = new Phone3();

        // 线程A启动
        new Thread(() -> {
            phone1.call();
        }, "A").start();

        // 休眠500毫秒，为了让线程A始终在线程B前面启动
        // 如果不加休眠，由于线程调度问题，线程A和线程B谁先启动，并不是由代码顺序决定的
        // 而是由CPU调度决定
        TimeUnit.MILLISECONDS.sleep(500);

        // 线程B启动
        new Thread(() -> {
            phone2.sms();
        }, "B").start();


    }

}

class Phone3 {

    public static synchronized void call() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "打电话");
    }

    public synchronized void sms() {
        System.out.println(Thread.currentThread().getName() + "发短信");
    }

}