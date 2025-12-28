package net.force2dev.fysix.game;

import net.force2dev.fysix.engine.FysixEngine;
import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.equipment.Shield;
import net.force2dev.fysix.equipment.Weapon;
import net.force2dev.fysix.level.Level;
import net.force2dev.fysix.level.LevelHandler;
import net.force2dev.fysix.level.SpawnPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the overall game state and players
 */
public class GameManager {
    private GameState currentState;
    private LevelHandler levelHandler;
    private Level currentLevel;
    private List<Player> players;
    private FysixEngine physicsEngine;
    private Player localPlayer; // The main player (for single player)
    
    // Game mode settings
    private GameMode gameMode;
    private int targetKills = 10; // For deathmatch
    private long gameStartTime;
    private long gameTimeLimit = 0; // 0 = no time limit
    
    public enum GameMode {
        DEATHMATCH,
        SURVIVAL,
        RACE,
        CAPTURE_THE_FLAG
    }
    
    public GameManager() {
        this.currentState = GameState.MENU;
        this.levelHandler = new LevelHandler();
        this.players = new ArrayList<>();
        this.physicsEngine = FysixEngine.GetContext();
        this.gameMode = GameMode.DEATHMATCH;
    }
    
    /**
     * Initialize a new game
     */
    public void startGame(Level level) {
        this.currentLevel = level;
        this.currentState = GameState.PLAYING;
        this.gameStartTime = System.currentTimeMillis();
        this.players.clear();
        
        // Create default player
        Player player = new Player("Player 1", 1, 1);
        player.setWeapon(Weapon.createDefaultCannon());
        player.setShield(Shield.createDefaultShield());
        players.add(player);
        localPlayer = player;
        
        // Spawn players
        spawnPlayers();
    }
    
    /**
     * Start game with default level
     */
    public void startGame() {
        Level defaultLevel = levelHandler.createDefaultLevel();
        startGame(defaultLevel);
    }
    
    /**
     * Spawn all players at their spawn points
     */
    private void spawnPlayers() {
        if (currentLevel == null) return;
        
        for (Player player : players) {
            if (!player.isAlive()) {
                SpawnPoint spawnPoint = currentLevel.getSpawnPoint(player.getTeam());
                
                // Create or reuse ship object
                FysixObject ship = player.getShipObject();
                if (ship == null) {
                    ship = (FysixObject) physicsEngine.AddObject(
                        spawnPoint.getX(), 
                        spawnPoint.getY(), 
                        150.0, 
                        null
                    );
                    player.setShipObject(ship);
                }
                
                player.spawn(spawnPoint, ship);
            }
        }
    }
    
    /**
     * Update game (called every frame)
     */
    public void update(long deltaTimeMs) {
        if (currentState != GameState.PLAYING) {
            return;
        }
        
        // Update shields
        for (Player player : players) {
            if (player.getShield() != null) {
                player.getShield().update(deltaTimeMs);
            }
            
            // Check for respawn
            if (player.canRespawn()) {
                spawnPlayers();
                net.force2dev.fysix.sound.PlaySound.respawn();
            }
        }
        
        // Check win conditions
        checkWinConditions();
    }
    
    /**
     * Check if win conditions are met
     */
    private void checkWinConditions() {
        if (gameMode == GameMode.DEATHMATCH) {
            for (Player player : players) {
                if (player.getScore().getKills() >= targetKills) {
                    currentState = GameState.VICTORY;
                    return;
                }
            }
        }
        
        // Check time limit
        if (gameTimeLimit > 0) {
            long elapsed = System.currentTimeMillis() - gameStartTime;
            if (elapsed >= gameTimeLimit) {
                // Game time limit reached - determine winner
                currentState = GameState.VICTORY;
            }
        }
    }
    
    /**
     * Get current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * Set game state
     */
    public void setState(GameState state) {
        this.currentState = state;
    }
    
    /**
     * Get all players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Get local player
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }
    
    /**
     * Get current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Pause game
     */
    public void pause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
        }
    }
    
    /**
     * Resume game
     */
    public void resume() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
        }
    }
    
    /**
     * Game over
     */
    public void gameOver() {
        currentState = GameState.GAME_OVER;
    }
    
    // Getters and setters
    public GameMode getGameMode() { return gameMode; }
    public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }
    
    public int getTargetKills() { return targetKills; }
    public void setTargetKills(int targetKills) { this.targetKills = targetKills; }
    
    public long getGameTimeLimit() { return gameTimeLimit; }
    public void setGameTimeLimit(long gameTimeLimit) { this.gameTimeLimit = gameTimeLimit; }
    
    public LevelHandler getLevelHandler() { return levelHandler; }
}

