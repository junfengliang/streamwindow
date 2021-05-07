package cn.genlei.stream.window;
import java.util.List;


/**
 * @author nid
 */
public interface WindowHandler<T> {
    /**
     * handle a batch of data
     * @param list
     */
    public void handle(List<T> list);
}