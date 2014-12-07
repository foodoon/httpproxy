package guda.httpproxy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by well on 2014/12/6.
 */
public class PoolQueue<E> {
    private int maxCount = 30;

    private LinkedList<E> queue = new LinkedList<E>();

    public synchronized void add(E e){
        if(queue.size() ==maxCount){
            queue.poll();
        }
        queue.add(e);
    }

    public synchronized List<E> peekAll(){
        List<E> list = new ArrayList<E>(queue.size());
        list.addAll(queue);
        Collections.reverse(list);
        return list;
    }
}
