package top.lisicheng.wait与sleep的区别;

public class Demo1 {

    public static void main(String[] args) {

        String s = "123";
        synchronized (s){
            try {
                s.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
