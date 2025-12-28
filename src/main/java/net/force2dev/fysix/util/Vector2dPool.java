package net.force2dev.fysix.util;

import javax.vecmath.Vector2d;

/**
 * Object pool for Vector2d to reduce allocations
 */
public class Vector2dPool {
    private final ObjectPool<Vector2d> pool;
    
    public Vector2dPool() {
        this.pool = new ObjectPool<>(
            new ObjectPool.PoolableFactory<Vector2d>() {
                @Override
                public Vector2d create() {
                    return new Vector2d();
                }
                
                @Override
                public void reset(Vector2d obj) {
                    obj.set(0.0, 0.0);
                }
            },
            50,  // Initial size
            200  // Max size
        );
    }
    
    public Vector2d obtain() {
        return pool.obtain();
    }
    
    public Vector2d obtain(double x, double y) {
        Vector2d v = pool.obtain();
        v.set(x, y);
        return v;
    }
    
    public void free(Vector2d v) {
        pool.free(v);
    }
    
    public void clear() {
        pool.clear();
    }
}

