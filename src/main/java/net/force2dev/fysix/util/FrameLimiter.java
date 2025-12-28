package net.force2dev.fysix.util;

/**
 * Frame rate limiter using busy-waiting for precision
 */
public class FrameLimiter {
    private long lastFrameTime;
    private long targetFrameTimeNanos;
    private boolean enabled;
    
    public FrameLimiter(double targetFPS) {
        this.targetFrameTimeNanos = (long)(1_000_000_000.0 / targetFPS);
        this.lastFrameTime = System.nanoTime();
        this.enabled = true;
    }
    
    public FrameLimiter() {
        this(60.0); // Default 60 FPS
    }
    
    /**
     * Wait until target frame time has passed
     * Returns actual elapsed time in seconds
     */
    public double waitForNextFrame() {
        if (!enabled) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
            lastFrameTime = currentTime;
            return deltaTime;
        }
        
        long currentTime = System.nanoTime();
        long elapsed = currentTime - lastFrameTime;
        
        if (elapsed < targetFrameTimeNanos) {
            // Busy wait for precision (better than Thread.sleep for frame limiting)
            long waitTime = targetFrameTimeNanos - elapsed;
            long waitUntil = currentTime + waitTime;
            
            // Use Thread.sleep for longer waits, busy-wait for short waits
            if (waitTime > 1_000_000) { // > 1ms
                try {
                    Thread.sleep(waitTime / 1_000_000, (int)(waitTime % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // Busy-wait for remaining time for precision
            while (System.nanoTime() < waitUntil) {
                // Spin-wait
            }
            
            currentTime = System.nanoTime();
        }
        
        double deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
        lastFrameTime = currentTime;
        return deltaTime;
    }
    
    public void setTargetFPS(double fps) {
        this.targetFrameTimeNanos = (long)(1_000_000_000.0 / fps);
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}

