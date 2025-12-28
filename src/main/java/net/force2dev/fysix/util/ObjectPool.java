package net.force2dev.fysix.util;

import java.util.Stack;

/**
 * Generic object pool for reusing objects to reduce allocations
 */
public class ObjectPool<T> {
    private final Stack<T> pool = new Stack<>();
    private final PoolableFactory<T> factory;
    private int maxSize;
    
    public interface PoolableFactory<T> {
        T create();
        void reset(T obj);
    }
    
    public ObjectPool(PoolableFactory<T> factory, int initialSize, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        for (int i = 0; i < initialSize; i++) {
            pool.push(factory.create());
        }
    }
    
    public ObjectPool(PoolableFactory<T> factory) {
        this(factory, 10, 100);
    }
    
    /**
     * Obtain an object from the pool
     */
    public T obtain() {
        if (pool.isEmpty()) {
            return factory.create();
        }
        T obj = pool.pop();
        factory.reset(obj);
        return obj;
    }
    
    /**
     * Return an object to the pool
     */
    public void free(T obj) {
        if (pool.size() < maxSize) {
            factory.reset(obj);
            pool.push(obj);
        }
    }
    
    public void clear() {
        pool.clear();
    }
}

