package net.force2dev.fysix;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

/**
 *
 */
public class InputManager {

	private static Hashtable keyToMap = new Hashtable();
    private static Hashtable mapStatus = new Hashtable();
    
    public static void Initialise(Component c) {
        c.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

			public void keyPressed(KeyEvent e) {
                if (keyToMap.containsKey(new Integer(e.getKeyCode()))) {
                    String mapStr = (String)keyToMap.get(new Integer(e.getKeyCode()));
                    mapStatus.put(mapStr, Boolean.TRUE);
                }
            }

			public void keyReleased(KeyEvent e) {
                if (keyToMap.containsKey(new Integer(e.getKeyCode()))) {
                    String mapStr = (String)keyToMap.get(new Integer(e.getKeyCode()));
                    mapStatus.put(mapStr, Boolean.FALSE);
                }
            }
        });
    }
    
	public static void MapKey(int keyCode, String mapStr) {
        keyToMap.put(new Integer(keyCode), mapStr);
        mapStatus.put(mapStr, Boolean.FALSE);
    }
    
    public static boolean isKeyDown(String mapStr) {
        if (mapStatus.containsKey(mapStr)) {
            return ((Boolean)mapStatus.get(mapStr)).booleanValue();
        }
        
        return false;
    }
}
