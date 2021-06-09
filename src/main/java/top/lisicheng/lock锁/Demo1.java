package top.lisicheng.lock锁;

/**
 * 传统synchronized写法
 */
public class Demo1 {

    public static void main(String[] args) {

        final Ticket ticket = new Ticket(3);

        for (int i = 0; i < 4; i++) {

            new Thread(() -> {
                ticket.buyTick();
            }, String.valueOf(i)).start();

        }

    }

}

class Ticket {

    private int num;

    public Ticket(int num) {
        this.num = num;
    }

    public synchronized void buyTick() {

        if (num-- > 0) {
            System.out.println(Thread.currentThread().getName() + "买了一张票");
        } else {
            System.out.println(Thread.currentThread().getName() + "已经卖完了，下次再来吧");
        }

    }
}
