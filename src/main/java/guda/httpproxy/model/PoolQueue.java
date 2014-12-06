package guda.httpproxy.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by well on 2014/12/6.
 */
public class PoolQueue<E> {
    private int maxCount = 10;

    private Queue<E> queue = new LinkedList<E>();

    public synchronized void add(E e){
        if(queue.size() ==10){
            queue.poll();
        }
        queue.add(e);
    }

    public synchronized List<E> pollAll(){
        int size = queue.size();
        List<E> list = new ArrayList<E>(size);
        for(int i=0;i<size;++i){
            list.add(queue.poll());
        }
        return list;
    }
}
