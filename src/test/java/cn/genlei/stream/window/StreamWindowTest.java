package cn.genlei.stream.window;

/**
 * @author junfeng
 */
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class StreamWindowTest {

    StreamWindow<String> streamWindow = new StreamWindow<>();
    static Logger log = Logger.getLogger("stream-window-test");

    public static void main(String[] args){
        StreamWindowTest streamWindowTest = new StreamWindowTest();
        streamWindowTest.basicTest();

        StreamWindowTest st = new StreamWindowTest();
        st.basicTest();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void basicTest(){
        for(int i=1;i<=10;i++) {
            Thread genThread = new Thread(runnable);
            genThread.setDaemon(true);
            genThread.start();
        }
        streamWindow.addHandler(windowHandler);
    }
    WindowHandler<String> windowHandler = new WindowHandler<String>() {
        @Override public void handle(List<String> list) {
            String message = String.format("handle %s,%s,waiting size:%s",
                    list.size(),Thread.currentThread().getId(),streamWindow.size());
            log.info(message);
        }
    };
    Runnable runnable = new Runnable() {
        @Override public void run() {
            Random random = new Random();
            while (true) {
                streamWindow.add("tick " + System.currentTimeMillis());
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
