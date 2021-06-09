package top.lisicheng.lock锁;

import java.util.Date;

/**
 * wait()/notify()实现线程通信
 *
 * @author Lisicheng
 */
public class Demo4 {


    public static void main(String[] args) {
        Data data = new Data();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data.sub();
            }

        }, "A").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data.add();
            }

        }, "B").start();

        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data.add();
            }

        }, "C").start();


        new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                data.sub();
            }

        }, "D").start();
    }


}


class Data {

    private int num = 0;

    public synchronized void add() {

        // wait释放锁的时候，应该强制在自选结构内（循环），如果在if条件内
        // 会存在虚假唤醒，可以在这里将while改掉if，多跑几次，会发现有时候
        // 会存在程序结束不了的情况，就是因为发生了虚假唤醒
        while (num != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        num++;
        System.out.println(Thread.currentThread().getName() + "+1");
        this.notifyAll();

    }

    public synchronized void sub() {

        while (num == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        num--;
        System.out.println(Thread.currentThread().getName() + "-1");
        this.notifyAll();

    }
}