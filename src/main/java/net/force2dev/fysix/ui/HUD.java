package net.force2dev.fysix.ui;

import net.force2dev.fysix.game.Player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Heads-Up Display for rendering game UI
 */
public class HUD {
    private static final Font DEFAULT_FONT = new Font("Monospaced", Font.BOLD, 14);
    private static final Font LARGE_FONT = new Font("Monospaced", Font.BOLD, 20);
    
    /**
     * Draw HUD for a player
     */
    public static void drawHUD(Graphics2D g2d, Player player, Dimension screenSize) {
        if (player == null || !player.isAlive()) {
            return;
        }
        
        g2d.setFont(DEFAULT_FONT);
        
        // Health bar
        drawHealthBar(g2d, player, 10, 10, 200, 20);
        
        // Shield bar
        if (player.getShield() != null) {
            drawShieldBar(g2d, player, 10, 35, 200, 15);
        }
        
        // Ammo (if weapon has limited ammo)
        if (player.getWeapon() != null && player.getWeapon().getMaxAmmo() > 0) {
            drawAmmo(g2d, player, 10, 55);
        }
        
        // Score
        drawScore(g2d, player, screenSize.width - 150, 10);
        
        // Position (debug info)
        if (player.getShipObject() != null) {
            drawPosition(g2d, player, 10, screenSize.height - 30);
        }
    }
    
    private static void drawHealthBar(Graphics2D g2d, Player player, int x, int y, int width, int height) {
        double healthPercent = player.getHealth().getHealthPercentage();
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(x, y, width, height);
        
        // Health fill
        int healthWidth = (int) (width * healthPercent);
        Color healthColor = healthPercent > 0.5 ? Color.GREEN : 
                           healthPercent > 0.25 ? Color.YELLOW : Color.RED;
        g2d.setColor(healthColor);
        g2d.fillRect(x, y, healthWidth, height);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, width, height);
        
        // Text
        g2d.setColor(Color.WHITE);
        String text = String.format("HP: %d/%d", 
            player.getHealth().getCurrentHealth(), 
            player.getHealth().getMaxHealth());
        g2d.drawString(text, x + 5, y + height - 4);
    }
    
    private static void drawShieldBar(Graphics2D g2d, Player player, int x, int y, int width, int height) {
        double shieldPercent = player.getShield().getShieldPercentage();
        
        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(x, y, width, height);
        
        // Shield fill
        int shieldWidth = (int) (width * shieldPercent);
        g2d.setColor(Color.CYAN);
        g2d.fillRect(x, y, shieldWidth, height);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, width, height);
        
        // Text
        g2d.setColor(Color.WHITE);
        String text = String.format("Shield: %d/%d", 
            player.getShield().getCurrentShield(), 
            player.getShield().getMaxShield());
        g2d.drawString(text, x + 5, y + height - 4);
    }
    
    private static void drawAmmo(Graphics2D g2d, Player player, int x, int y) {
        g2d.setColor(Color.WHITE);
        String text = String.format("Ammo: %d/%d", 
            player.getWeapon().getAmmo(), 
            player.getWeapon().getMaxAmmo());
        g2d.drawString(text, x, y);
    }
    
    private static void drawScore(Graphics2D g2d, Player player, int x, int y) {
        g2d.setFont(LARGE_FONT);
        g2d.setColor(Color.YELLOW);
        
        String scoreText = String.format("Score: %d", player.getScore().getScore());
        g2d.drawString(scoreText, x, y);
        
        g2d.setFont(DEFAULT_FONT);
        g2d.setColor(Color.WHITE);
        String statsText = String.format("Kills: %d  Deaths: %d  K/D: %.2f", 
            player.getScore().getKills(),
            player.getScore().getDeaths(),
            player.getScore().getKDRatio());
        g2d.drawString(statsText, x, y + 20);
    }
    
    private static void drawPosition(Graphics2D g2d, Player player, int x, int y) {
        g2d.setColor(Color.BLUE);
        double px = player.getShipObject().getPosition().x;
        double py = player.getShipObject().getPosition().y;
        String text = String.format("X: %d Y: %d", (int) px, (int) py);
        g2d.drawString(text, x, y);
    }
    
    /**
     * Draw game over screen
     */
    public static void drawGameOver(Graphics2D g2d, Player player, Dimension screenSize) {
        g2d.setFont(LARGE_FONT);
        g2d.setColor(Color.WHITE);
        
        String text = "GAME OVER";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (screenSize.width - textWidth) / 2, screenSize.height / 2);
        
        if (player != null) {
            g2d.setFont(DEFAULT_FONT);
            String scoreText = String.format("Final Score: %d", player.getScore().getScore());
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreText);
            g2d.drawString(scoreText, (screenSize.width - scoreWidth) / 2, screenSize.height / 2 + 40);
        }
    }
    
    /**
     * Draw victory screen
     */
    public static void drawVictory(Graphics2D g2d, Player player, Dimension screenSize) {
        g2d.setFont(LARGE_FONT);
        g2d.setColor(Color.YELLOW);
        
        String text = "VICTORY!";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (screenSize.width - textWidth) / 2, screenSize.height / 2);
        
        if (player != null) {
            g2d.setFont(DEFAULT_FONT);
            g2d.setColor(Color.WHITE);
            String scoreText = String.format("Score: %d  Kills: %d", 
                player.getScore().getScore(),
                player.getScore().getKills());
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreText);
            g2d.drawString(scoreText, (screenSize.width - scoreWidth) / 2, screenSize.height / 2 + 40);
        }
    }
    
    /**
     * Draw pause screen
     */
    public static void drawPause(Graphics2D g2d, Dimension screenSize) {
        g2d.setFont(LARGE_FONT);
        g2d.setColor(Color.WHITE);
        
        String text = "PAUSED";
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (screenSize.width - textWidth) / 2, screenSize.height / 2);
        
        g2d.setFont(DEFAULT_FONT);
        String pressP = "Press P to resume";
        int pressPWidth = g2d.getFontMetrics().stringWidth(pressP);
        g2d.drawString(pressP, (screenSize.width - pressPWidth) / 2, screenSize.height / 2 + 40);
    }
    
    /**
     * Draw respawn countdown
     */
    public static void drawRespawnCountdown(Graphics2D g2d, Player player, Dimension screenSize) {
        if (player.isAlive()) return;
        
        g2d.setFont(LARGE_FONT);
        g2d.setColor(Color.YELLOW);
        
        // Calculate time until respawn
        long respawnTime = player.getRespawnTime();
        long currentTime = System.currentTimeMillis();
        long timeLeft = Math.max(0, respawnTime - currentTime);
        int seconds = (int)(timeLeft / 1000) + 1;
        
        String text = "RESPAWNING IN: " + seconds;
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, (screenSize.width - textWidth) / 2, screenSize.height / 2);
    }
}

