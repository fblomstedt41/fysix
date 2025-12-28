package net.force2dev.fysix.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import net.force2dev.fysix.sound.PlaySound;

/**
 * Settings menu for game configuration
 */
public class SettingsMenu {
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 32);
    private static final Font ITEM_FONT = new Font("Monospaced", Font.PLAIN, 20);
    private static final Font VALUE_FONT = new Font("Monospaced", Font.PLAIN, 18);
    
    private int selectedIndex = 0;
    private String[] menuItems = {
        "Sound: ON/OFF",
        "Master Volume",
        "Auto Zoom: ON/OFF",
        "Back"
    };
    
    // Settings values
    private boolean soundEnabled = PlaySound.isEnabled();
    private float masterVolume = PlaySound.getMasterVolume();
    private boolean autoZoomEnabled = true; // Will be managed by camera system
    
    public SettingsMenu() {
        updateMenuTexts();
    }
    
    /**
     * Update menu item texts to reflect current settings
     */
    private void updateMenuTexts() {
        menuItems[0] = "Sound: " + (soundEnabled ? "ON" : "OFF");
        menuItems[1] = "Master Volume: " + (int)(masterVolume * 100) + "%";
        menuItems[2] = "Auto Zoom: " + (autoZoomEnabled ? "ON" : "OFF");
    }
    
    /**
     * Navigate up in menu
     */
    public void navigateUp() {
        selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
    }
    
    /**
     * Navigate down in menu
     */
    public void navigateDown() {
        selectedIndex = (selectedIndex + 1) % menuItems.length;
    }
    
    /**
     * Handle selection/action on current item
     */
    public boolean handleSelect() {
        switch (selectedIndex) {
            case 0: // Sound ON/OFF
                soundEnabled = !soundEnabled;
                PlaySound.setEnabled(soundEnabled);
                updateMenuTexts();
                return false; // Don't exit
                
            case 1: // Master Volume
                // Cycle through volume levels: 0%, 25%, 50%, 75%, 100%
                if (masterVolume < 0.25f) {
                    masterVolume = 0.25f;
                } else if (masterVolume < 0.5f) {
                    masterVolume = 0.5f;
                } else if (masterVolume < 0.75f) {
                    masterVolume = 0.75f;
                } else if (masterVolume < 1.0f) {
                    masterVolume = 1.0f;
                } else {
                    masterVolume = 0.0f; // Loop back to 0%
                }
                PlaySound.setMasterVolume(masterVolume);
                updateMenuTexts();
                return false; // Don't exit
                
            case 2: // Auto Zoom
                autoZoomEnabled = !autoZoomEnabled;
                updateMenuTexts();
                return false; // Don't exit
                
            case 3: // Back
                return true; // Exit settings
                
            default:
                return false;
        }
    }
    
    /**
     * Handle left/right navigation for adjusting values
     */
    public void handleAdjust(int direction) {
        switch (selectedIndex) {
            case 1: // Master Volume
                masterVolume += direction * 0.1f;
                masterVolume = Math.max(0.0f, Math.min(1.0f, masterVolume));
                PlaySound.setMasterVolume(masterVolume);
                updateMenuTexts();
                break;
        }
    }
    
    /**
     * Get selected item text
     */
    public String getSelectedItemText() {
        if (selectedIndex >= 0 && selectedIndex < menuItems.length) {
            return menuItems[selectedIndex];
        }
        return "";
    }
    
    /**
     * Get auto zoom enabled state
     */
    public boolean isAutoZoomEnabled() {
        return autoZoomEnabled;
    }
    
    /**
     * Draw the settings menu
     */
    public void draw(Graphics2D g2d, Dimension screenSize) {
        // Draw title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(Color.WHITE);
        String title = "SETTINGS";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (screenSize.width - titleWidth) / 2, 80);
        
        // Draw menu items
        g2d.setFont(ITEM_FONT);
        int startY = 150;
        int lineHeight = 40;
        
        for (int i = 0; i < menuItems.length; i++) {
            Color itemColor = (i == selectedIndex) ? Color.YELLOW : Color.WHITE;
            g2d.setColor(itemColor);
            
            // Draw selection indicator
            if (i == selectedIndex) {
                g2d.drawString(">", 200, startY + i * lineHeight);
            }
            
            // Draw menu item
            int itemWidth = g2d.getFontMetrics().stringWidth(menuItems[i]);
            g2d.drawString(menuItems[i], (screenSize.width - itemWidth) / 2, startY + i * lineHeight);
        }
        
        // Draw instructions
        g2d.setFont(VALUE_FONT);
        g2d.setColor(Color.GRAY);
        String instructions = "Use UP/DOWN to navigate, ENTER to toggle, ESC to go back";
        int instWidth = g2d.getFontMetrics().stringWidth(instructions);
        g2d.drawString(instructions, (screenSize.width - instWidth) / 2, screenSize.height - 50);
    }
}

