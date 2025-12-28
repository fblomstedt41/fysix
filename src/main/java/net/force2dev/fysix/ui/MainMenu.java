package net.force2dev.fysix.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Main menu rendering and state management
 */
public class MainMenu {
    private int selectedItem = 0;
    private String[] menuItems = {"Start Game", "Settings", "Exit"};
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 48);
    private static final Font MENU_FONT = new Font("Monospaced", Font.BOLD, 24);
    
    /**
     * Draw the main menu
     */
    public void draw(Graphics2D g2d, Dimension screenSize) {
        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenSize.width, screenSize.height);
        
        // Title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(Color.YELLOW);
        String title = "FYSIX";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (screenSize.width - titleWidth) / 2, screenSize.height / 4);
        
        // Subtitle
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g2d.setColor(Color.GRAY);
        String subtitle = "Retro Pixel Shooter";
        int subtitleWidth = g2d.getFontMetrics().stringWidth(subtitle);
        g2d.drawString(subtitle, (screenSize.width - subtitleWidth) / 2, screenSize.height / 4 + 40);
        
        // Menu items
        g2d.setFont(MENU_FONT);
        int startY = screenSize.height / 2;
        int spacing = 50;
        
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedItem) {
                g2d.setColor(Color.YELLOW);
                // Draw selection indicator
                g2d.drawString("> ", (screenSize.width - 300) / 2, startY + i * spacing);
            } else {
                g2d.setColor(Color.WHITE);
            }
            
            int itemWidth = g2d.getFontMetrics().stringWidth(menuItems[i]);
            g2d.drawString(menuItems[i], (screenSize.width - itemWidth) / 2, startY + i * spacing);
        }
        
        // Instructions
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g2d.setColor(Color.GRAY);
        String instructions = "Use UP/DOWN to navigate, ENTER to select";
        int instWidth = g2d.getFontMetrics().stringWidth(instructions);
        g2d.drawString(instructions, (screenSize.width - instWidth) / 2, screenSize.height - 50);
    }
    
    /**
     * Handle menu navigation
     */
    public void navigateUp() {
        selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
    }
    
    public void navigateDown() {
        selectedItem = (selectedItem + 1) % menuItems.length;
    }
    
    public int getSelectedItem() {
        return selectedItem;
    }
    
    public String getSelectedItemText() {
        return menuItems[selectedItem];
    }
    
    public void reset() {
        selectedItem = 0;
    }
}

