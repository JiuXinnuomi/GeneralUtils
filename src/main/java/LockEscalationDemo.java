import org.openjdk.jol.info.ClassLayout;

public class LockEscalationDemo {

     public static void main(String[] args) throws InterruptedException {
         
         System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());

         Thread.sleep(4000);
         Object obj = new Object();
         new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"开始执行。。。\n"
                        +ClassLayout.parseInstance(obj).toPrintable());
                synchronized (obj){
                    System.out.println(Thread.currentThread().getName()+"获取锁执行中。。。\n"
                            +ClassLayout.parseInstance(obj).toPrintable());
                    }
                System.out.println(Thread.currentThread().getName()+"释放锁。。。\n"
                        +ClassLayout.parseInstance(obj).toPrintable());
                }
         },"thread1").start();
         Thread.sleep(5000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName()+"开始执行。。。\n"
                        +ClassLayout.parseInstance(obj).toPrintable());
                synchronized (obj){
                    System.out.println(Thread.currentThread().getName()+"获取锁执行中。。。\n"
                            +ClassLayout.parseInstance(obj).toPrintable());
                    }
                System.out.println(Thread.currentThread().getName()+"释放锁。。。\n"
                        +ClassLayout.parseInstance(obj).toPrintable());
                }
        },"thread2").start();
    System.out.println(ClassLayout.parseInstance(obj).toPrintable());
    }
}