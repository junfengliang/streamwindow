package cn.genlei.stream.window;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author nid
 */
public class StreamWindow<T> implements Closeable {

    /**
     * span millisecond
     */
    final long span = 1000;
    final long waitTimeout = 500;
    final  int batchSize = 100;

    static volatile int threadNum =1;

    List<WindowHandler> handlers = new ArrayList<>();
    LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
    Thread thread = null;


    volatile boolean closed = false;

    public StreamWindow(){
        thread = new Thread(runnable);
        thread.setName("stream-window-" + threadNum++);
        thread.setDaemon(true);
        thread.start();
    }
    public void add(T t){
        if(closed){
            throw new IllegalStateException("Stream window already closed.");
        }
        queue.add(t);
    }

    public void addHandler(WindowHandler handler){
        handlers.add(handler);
    }

    public int size(){
        return queue.size();
    }

    @Override public void close() throws IOException {
        closed = true;
    }
    Runnable runnable = new Runnable() {
        long lastTime = System.currentTimeMillis();
        List<T> list = new ArrayList<>();
        @Override public void run() {
            while (true){
                if(closed && queue.size()==0){
                    break;
                }
                loop();
            }
        }
        private void loop(){
            try {
                T t = queue.poll(waitTimeout, TimeUnit.MILLISECONDS);
                if(t!=null) {
                    list.add(t);
                }
                if(list.size()>=batchSize || timeReach()){
                    doOnePatch();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean timeReach() {
            return System.currentTimeMillis() - lastTime >= span;
        }

        private void doOnePatch() {
            if(list.size()==0){
                lastTime = System.currentTimeMillis();
                return;
            }
            for(WindowHandler windowHandler:handlers){
                windowHandler.handle(list);
            }
            list.clear();
            lastTime = System.currentTimeMillis();
        }
    };
}



