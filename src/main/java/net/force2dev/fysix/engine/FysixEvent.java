package net.force2dev.fysix.engine;

/**
 *
 * @author xnvmakl
 */
public class FysixEvent {

    public static int EVENT_ID_OBJECT_ADDED = 1;
    public static int EVENT_ID_OBJECT_REMOVED = 2;
    public static int EVENT_ID_COLLISION_DETECTED = 3;
    // ...
    // Custom events must start from EVENT_ID_CUSTOM_BASE
    public static int EVENT_ID_CUSTOM_BASE = 1000;
    
    public int eventId;
    public Object eventParam;
}
