### JUC学习笔记

------



#### 1.什么是JUC

------

`juc`是`java.util.concurrent`目录的缩写，也是常说的`java`并发包



#### 2.线程的状态

------

线程的状态有==6==种，分别为==**创建（NEW）**==，==**运行中(RUNNABLE)**==，==**阻塞（BLOCKED）**==，==**等待（WAITING）**==，==**超时等待（TIMED_WAITING）**==，==**终止（TERMINATED）**==

```java
public enum State {
    /**
    * Thread state for a thread which has not yet started.
    * 创建
    */
    NEW,

    /**
    * Thread state for a runnable thread.  A thread in the runnable
    * state is executing in the Java virtual machine but it may
    * be waiting for other resources from the operating system
    * such as processor.
    * 运行中
    */
    RUNNABLE,

    /**
    * Thread state for a thread blocked waiting for a monitor lock.
    * A thread in the blocked state is waiting for a monitor lock
    * to enter a synchronized block/method or
    * reenter a synchronized block/method after calling
    * {@link Object#wait() Object.wait}.
    * 阻塞
    */
    BLOCKED,

    /**
    * Thread state for a waiting thread.
    * A thread is in the waiting state due to calling one of the
    * following methods:
    * <ul>
    *   <li>{@link Object#wait() Object.wait} with no timeout</li>
    *   <li>{@link #join() Thread.join} with no timeout</li>
    *   <li>{@link LockSupport#park() LockSupport.park}</li>
    * </ul>
    *
    * <p>A thread in the waiting state is waiting for another thread to
    * perform a particular action.
    *
    * For example, a thread that has called <tt>Object.wait()</tt>
    * on an object is waiting for another thread to call
    * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
    * that object. A thread that has called <tt>Thread.join()</tt>
    * is waiting for a specified thread to terminate.
    * 等待
    */
    WAITING,
    
    /**
    * Thread state for a waiting thread with a specified waiting time.
    * A thread is in the timed waiting state due to calling one of
    * the following methods with a specified positive waiting time:
    * <ul>
    *   <li>{@link #sleep Thread.sleep}</li>
    *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
    *   <li>{@link #join(long) Thread.join} with timeout</li>
    *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
    *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
    * </ul>
    * 超时等待
    */
    TIMED_WAITING,
    
    /**
    * Thread state for a terminated thread.
    * The thread has completed execution.
    * 终止
    */
    TERMINATED;
}
```



#### 3.wait/sleep的区别

------

1. **来自不同的类**

   ```java
   public class Object {
   	public final void wait() throws InterruptedException {
           wait(0);
       }
   }
   ```

   ```java
   public class Thread implements Runnable {
   	public static native void sleep(long millis) throws InterruptedException;
   }
   ```

   

2. **使用范围**

   > *wait()*需要在同步代码块才行执行，否则是抛出以下异常。也就是必须先拿到锁才能释放锁

   ```java
   String s = "123";
   synchronized (s){
       try {
           s.wait();
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
   
   
   // 去除synchronized代码块
   
   Exception in thread "main" java.lang.IllegalMonitorStateException
   	at java.lang.Object.wait(Native Method)
   	at java.lang.Object.wait(Object.java:502)
   ```

   > sleep()在任何地方都可以执行

   

3. **锁的释放**

   > *`wait()`*是释放锁，而*`sleep()`*表示当前线程休眠，如果没有设置休眠时间就会一直睡下去



#### 4.Lock锁

***

##### :one:.写法

> 传统`synchronize`写法

```java
// 模拟多人买票
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
```



> Lock锁

```java
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
```

##### :two:区别

> synchronized和Lock锁的区别

1. `synchronized`是关键字，`Lock`是java类
2. `synchronized`会自动释放锁，`Lock`需要手动释放，否则会死锁。（可以理解为手动挡和自动挡）

##### :three:线程通信

> *wait()*/*notify()*

```java
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
```

> Lock.Condition

```java
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
```

> 一个类似技术的出现并不是为了替代以前技术，而是为了补充。*wait()*  / *notify()*无法精准唤醒某个线程，而`Lock.Condition`可手动执行唤醒某个线程

```java
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
```



#### 5. 锁问题

**什么是锁，*锁的是谁***

**深刻理解锁问题**

##### :one:对象锁/实例锁

> 多个线程调用==同个==实例下加锁方法

```java
/**
 * 对象锁（实例锁）
 * 在同一个实例下调用上锁的实例方法，总会只有一个线程拿到锁
 */
public class 对象实例锁 {


    public static void main(String[] args) throws InterruptedException {

        Phone phone = new Phone();

        // 线程A启动
        new Thread(() -> {
            phone.call();
        }, "A").start();

        // 休眠500毫秒，为了让线程A始终在线程B前面启动
        // 如果不加休眠，由于线程调度问题，线程A和线程B谁先启动，并不是由代码顺序决定的
        // 而是由CPU调度决定
        TimeUnit.MILLISECONDS.sleep(500);

        // 线程B启动
        new Thread(() -> {
            phone.sms();
        }, "B").start();

        
    }

}


class Phone {

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
```

##### :two:对象锁/实例锁2

> 多个线程调用==不同==实例下加锁方法

```java
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
```

##### :three:类锁与实例锁

> 多个线程调用==不同==实例，==静态==和==非静态==加锁方法

```java
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
```

##### :four:类锁

> 多个线程调用==不同==实例，==静态加锁==方法

```java
/**
 * 类锁
 */
public class 类锁 {

    public static void main(String[] args) throws InterruptedException {

        Phone4 phone1 = new Phone4();
        Phone4 phone2 = new Phone4();

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

class Phone4 {

    public static synchronized void call() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "打电话");
    }

    public static synchronized void sms() {
        System.out.println(Thread.currentThread().getName() + "发短信");
    }

}
```

#### 6.并发容器

##### 	:one:List

​		