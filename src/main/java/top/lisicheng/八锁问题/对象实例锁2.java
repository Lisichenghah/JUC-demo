package top.lisicheng.八锁问题;

import java.util.concurrent.TimeUnit;

/**
 * 对象锁（实例锁）
 * 在同一个实例下调用上锁的实例方法，总会只有一个线程拿到锁
 */
public class 对象实例锁2 {

    public static void main(String[] args) throws InterruptedException {

        Phone2 phone1 = new Phone2();
        Phone2 phone2 = new Phone2();

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

class Phone2 {

    public synchronized void call() {
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