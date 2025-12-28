package net.force2dev.fysix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.force2dev.fysix.engine.Environment;
import net.force2dev.fysix.engine.FysixEngine;
import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.game.GameManager;
import net.force2dev.fysix.game.GameState;
import net.force2dev.fysix.game.Player;
import net.force2dev.fysix.game.Projectile;
import net.force2dev.fysix.game.ProjectileManager;
import net.force2dev.fysix.game.WallCollisionHandler;
import net.force2dev.fysix.level.Level;
import net.force2dev.fysix.level.LevelHandler;
import net.force2dev.fysix.level.Wall;
import net.force2dev.fysix.sound.PlaySound;
import net.force2dev.fysix.ui.HUD;
import net.force2dev.fysix.ui.MainMenu;
import net.force2dev.fysix.ui.RenderHelper;
import net.force2dev.fysix.ui.SettingsMenu;
import net.force2dev.fysix.util.FrameLimiter;
import net.force2dev.fysix.camera.AutoZoomManager;
import net.force2dev.fysix.effects.EffectManager;


public class FysixMain {

    static boolean running;
    static boolean menuUpPressed = false;
    static boolean menuDownPressed = false;
    static boolean menuSelectPressed = false;
    
    public static void main(String args[]) {
    	Renderer r = new Renderer();
    	
        FysixEngine fe = FysixEngine.GetContext();
        
        // Initialize game manager and level
        GameManager gameManager = new GameManager();
        LevelHandler levelHandler = new LevelHandler();
        Level level;
        
        try {
            level = levelHandler.loadLevel("default_arena.json");
        } catch (Exception e) {
            System.out.println("Could not load level file, using default level: " + e.getMessage());
            level = levelHandler.createDefaultLevel();
        }
        
        final Level finalLevel = level; // Make final for inner class
        
        // Initialize spatial hash for collision optimization (if many objects)
        fe.initializeSpatialHash(100, finalLevel.getWidth(), finalLevel.getHeight());
        
        // Main menu and settings menu
        MainMenu mainMenu = new MainMenu();
        SettingsMenu settingsMenu = new SettingsMenu();
        gameManager.setState(GameState.MENU);
        
        // Effect manager for particles and screen shake
        EffectManager effectManager = new EffectManager();
        
        // Wall collision handler
        WallCollisionHandler wallCollisionHandler = new WallCollisionHandler(finalLevel);
        
        // Setup environment
        Environment env = new Environment() {
            public Vector2d getEnvironmentAccelerationAtPoint(Point2d p) {
                return new Vector2d(0.0, 0.0);
            }
			public double getEnvironmentalResistanceAtPoint(Point2d p) {
                return finalLevel.getDefaultEnvironment().getResistance();
			}
        };
        
        r.Initialise();        
        
        InputManager.Initialise(r.GetComponent());
        InputManager.MapKey(KeyEvent.VK_ESCAPE, "QUIT");
        InputManager.MapKey(KeyEvent.VK_DOWN, "THRUST");
        InputManager.MapKey(KeyEvent.VK_LEFT, "LEFT");
        InputManager.MapKey(KeyEvent.VK_RIGHT, "RIGHT");
        InputManager.MapKey(KeyEvent.VK_UP, "FIRE");
        InputManager.MapKey(KeyEvent.VK_F1, "SCALE_IN");
        InputManager.MapKey(KeyEvent.VK_F2, "SCALE_OUT");
        InputManager.MapKey(KeyEvent.VK_F3, "TOGGLE_TV");
        InputManager.MapKey(KeyEvent.VK_P, "PAUSE");
        InputManager.MapKey(KeyEvent.VK_W, "MENU_UP");  // W for menu up
        InputManager.MapKey(KeyEvent.VK_S, "MENU_DOWN"); // S for menu down
        InputManager.MapKey(KeyEvent.VK_ENTER, "MENU_SELECT");
        
        // Create ship polygon
        Polygon ship = new Polygon();
        ship.addPoint(-4, -4);
        ship.addPoint(-4, 4);
        ship.addPoint(7, 0);
        
        // Create planet and moon for gravity
        List<FysixObject> levelObjects = new ArrayList<>();
        List<net.force2dev.fysix.level.GravityWell> gravityWells = finalLevel.getGravityWells();
        
        // First pass: create all objects
        for (net.force2dev.fysix.level.GravityWell gw : gravityWells) {
            FysixObject planet = (FysixObject) fe.AddObject(gw.getX(), gw.getY(), gw.getMass(), null);
            // Make sure moons are visible - use at least radius 15 for small objects
            int radius = (int)gw.getRadius();
            if (radius < 15 && gw.getMass() < 100000) {
                radius = 15; // Minimum visible size for moons
            }
            Polygon planetCircle = createCirclePolygon(radius, 16);
            planet.setBoundingArea(planetCircle);
            // Use different colors: gray for large planets, light gray for moons
            if (gw.getMass() > 100000) {
                planet.color = Color.GRAY; // Large planet
            } else {
                planet.color = Color.LIGHT_GRAY; // Moon or small object
            }
            levelObjects.add(planet);
        }
        
        // Second pass: set up moon orbit if we have exactly 2 gravity wells (planet + moon)
        if (gravityWells.size() == 2) {
            // Find the smaller object (moon) and larger object (planet)
            int moonIndex = -1;
            int planetIndex = -1;
            double minMass = Double.MAX_VALUE;
            double maxMass = 0;
            
            for (int i = 0; i < gravityWells.size(); i++) {
                double mass = gravityWells.get(i).getMass();
                if (mass < minMass) {
                    minMass = mass;
                    moonIndex = i;
                }
                if (mass > maxMass) {
                    maxMass = mass;
                    planetIndex = i;
                }
            }
            
            if (moonIndex >= 0 && planetIndex >= 0 && moonIndex != planetIndex) {
                net.force2dev.fysix.level.GravityWell moonGW = gravityWells.get(moonIndex);
                net.force2dev.fysix.level.GravityWell planetGW = gravityWells.get(planetIndex);
                FysixObject moon = levelObjects.get(moonIndex);
                
                // Calculate distance between planet and moon centers
                double dx = moonGW.getX() - planetGW.getX();
                double dy = moonGW.getY() - planetGW.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 0.1) { // Avoid division by zero
                    // Calculate orbital velocity for circular orbit: v = sqrt(G * M / r)
                    // Use same G as in FysixEngine for consistency
                    double G = 50.0; // Gravitational constant (game-appropriate, not realistic)
                    double planetMass = planetGW.getMass();
                    double stableOrbitalVelocity = Math.sqrt(G * planetMass / distance);
                    
                    // Make moon rotate 10 times slower for better visual effect
                    // Use the slower velocity directly - gravity will keep it in orbit
                    double orbitalVelocity = stableOrbitalVelocity / 10.0;
                    
                    // Calculate perpendicular direction (90° counterclockwise)
                    // From planet to moon: (dx, dy)
                    // Perpendicular (rotate 90° CCW): (-dy, dx)
                    double perpX = -dy / distance; // Normalized
                    double perpY = dx / distance;  // Normalized
                    
                    // Set moon's initial velocity (perpendicular to planet-moon line)
                    Vector2d moonVelocity = new Vector2d(perpX * orbitalVelocity, perpY * orbitalVelocity);
                    moon.setVelocity(moonVelocity);
                    
                    // Planet stays at rest (much larger mass, practically stationary)
                    FysixObject planet = levelObjects.get(planetIndex);
                    planet.setVelocity(new Vector2d(0, 0));
                }
            }
        }
        
        // Create wall objects for rendering (collision handled separately)
        List<FysixObject> wallObjects = new ArrayList<>();
        for (Wall wall : finalLevel.getWalls()) {
            Polygon wallPoly = wall.toPolygon();
            if (wallPoly.npoints > 0) {
                // Create a dummy object for rendering walls
                FysixObject wallObj = new FysixObject();
                wallObj.setBoundingArea(wallPoly);
                wallObj.setPosition(new Point2d(0, 0)); // Walls are in world coordinates
                wallObjects.add(wallObj);
            }
        }
        
        // Projectile manager
        ProjectileManager projectileManager = new ProjectileManager(fe);
        projectileManager.setHitCallback(new ProjectileManager.HitCallback() {
            @Override
            public void onHit(double x, double y, boolean hitPlayer) {
                if (hitPlayer) {
                    // Hit on player - create sparks
                    effectManager.createHitSparks(x, y);
                    PlaySound.hit();
                } else {
                    // Hit on wall - create explosion
                    effectManager.createExplosion(x, y, Color.ORANGE);
                    PlaySound.hit();
                }
            }
        });
        
        // Camera/viewport
        Point2d viewCoordUL = new Point2d();
        double scaleFactor = 1.0;
        double theta = 0.0;
        Player player = null; // Will be initialized when game starts
        
        Dimension frameSize = r.getSize();
        boolean tvToggleDown = false;
        
        // Auto zoom manager
        AutoZoomManager autoZoom = new AutoZoomManager(frameSize);
        
        // Frame limiter for consistent frame rate (60 FPS)
        FrameLimiter frameLimiter = new FrameLimiter(60.0);
        
        // Track previous game state for music control
        GameState previousState = GameState.MENU;
        
        running = true;
        
        while (running) {
            try {
                // Use frame limiter for consistent timing
                double deltaTimeSeconds = frameLimiter.waitForNextFrame();
                
                // Update game manager (convert to milliseconds for compatibility)
                if (gameManager.getCurrentState() == GameState.PLAYING) {
                    gameManager.update((long)(deltaTimeSeconds * 1000.0));
                }
                
                // Input handling
                if (InputManager.isKeyDown("QUIT")) {
                    System.exit(0);
                }
                
                if (InputManager.isKeyDown("PAUSE")) {
                    if (gameManager.getCurrentState() == GameState.PLAYING) {
                        gameManager.pause();
                    } else if (gameManager.getCurrentState() == GameState.PAUSED) {
                        gameManager.resume();
                    }
                }
                
                if (InputManager.isKeyDown("TOGGLE_TV") && !tvToggleDown) {
                	tvToggleDown = true;
                	r.setTvEffect(!r.isTvEffect());
                	Dimension oldSize = frameSize;
                	frameSize = r.getSize();
                    if (oldSize.width > 0) {
                	scaleFactor /= oldSize.width / (double) frameSize.width;
                    }
                } else if (!InputManager.isKeyDown("TOGGLE_TV") && tvToggleDown) {
                	tvToggleDown = false;
                }
                
                // Player controls (only if alive and playing)
                player = gameManager.getLocalPlayer();
                if (gameManager.getCurrentState() == GameState.PLAYING && player != null && player.isAlive()) {
                    FysixObject playerShip = player.getShipObject();
                    if (playerShip != null) {
                if (InputManager.isKeyDown("LEFT")) {
                    theta -= Math.PI/20;
                            playerShip.setDirection(theta);
                }
                if (InputManager.isKeyDown("RIGHT")) {
                    theta += Math.PI/20;
                            playerShip.setDirection(theta);
                }
                        Vector2d dir = playerShip.getDirection();
                        
                        if (InputManager.isKeyDown("THRUST")) {
                	PlaySound.thrust();
                            Vector2d newAcc = new Vector2d(dir);
                	newAcc.scale(120);
                            playerShip.setAcceleration(newAcc);
                            
                            // Create engine trail particles when thrusting
                            // Create multiple particles per frame for visible trail
                            double shipAngle = Math.atan2(dir.y, dir.x);
                            double shipSpeed = playerShip.getVelocity().length();
                            Point2d shipPos = playerShip.getPosition();
                            // Position trail behind ship
                            double trailX = shipPos.x - Math.cos(shipAngle) * 5;
                            double trailY = shipPos.y - Math.sin(shipAngle) * 5;
                            // Create 3-4 particles per frame for better visibility
                            for (int i = 0; i < 4; i++) {
                                effectManager.createEngineTrail(trailX, trailY, shipAngle, shipSpeed);
                            }
                        } else {
                            playerShip.setAcceleration(new Vector2d(0, 0));
                        }
                        
                        // Fire weapon
                        if (InputManager.isKeyDown("FIRE")) {
                            if (player.getWeapon().fire()) {
                                double angle = Math.atan2(dir.y, dir.x);
                                projectileManager.createProjectile(
                                    playerShip.getPosition().x,
                                    playerShip.getPosition().y,
                                    angle,
                                    player.getWeapon().getProjectileSpeed(),
                                    player.getWeapon().getDamage(),
                                    player.getPlayerId()
                                );
                                player.fireWeapon();
                                PlaySound.fire();
                            }
                        }
                    }
                }
                
                // Manual zoom override (optional - can be removed if you only want auto-zoom)
                // Auto-zoom will override these changes on next update anyway
                /*
                if (InputManager.isKeyDown("SCALE_IN")) {
                    scaleFactor += 0.01;
                    if(scaleFactor >= (frameSize.width/400.0)){
                    	scaleFactor = (frameSize.width/400.0);
                    }
                }
                if (InputManager.isKeyDown("SCALE_OUT")) {
                    scaleFactor -= 0.01;
                    if(scaleFactor <= (frameSize.width/5400.0)){
                    	scaleFactor = (frameSize.width/5400.0);
                    }
                }
                */
                
                // Update physics (pass deltaTime in seconds)
                if (gameManager.getCurrentState() == GameState.PLAYING) {
                    // Pass shockwave force provider to physics engine for physics effects
                    fe.Tick(env, deltaTimeSeconds, effectManager);
                    
                    // Update effects (particles, screen shake, etc.)
                    effectManager.update(deltaTimeSeconds);
                }
                
                // Menu handling (only handle menu input in menu state)
                if (gameManager.getCurrentState() == GameState.MENU) {
                    if (InputManager.isKeyDown("MENU_UP") && !menuUpPressed) {
                        mainMenu.navigateUp();
                        menuUpPressed = true;
                    } else if (!InputManager.isKeyDown("MENU_UP")) {
                        menuUpPressed = false;
                    }
                    
                    if (InputManager.isKeyDown("MENU_DOWN") && !menuDownPressed) {
                        mainMenu.navigateDown();
                        menuDownPressed = true;
                    } else if (!InputManager.isKeyDown("MENU_DOWN")) {
                        menuDownPressed = false;
                    }
                    
                    if (InputManager.isKeyDown("MENU_SELECT") && !menuSelectPressed) {
                        menuSelectPressed = true;
                        String selected = mainMenu.getSelectedItemText();
                        if ("Start Game".equals(selected)) {
                            gameManager.startGame(finalLevel);
                            player = gameManager.getLocalPlayer();
                            if (player != null && player.getShipObject() != null) {
                                player.getShipObject().setBoundingArea(ship);
                            }
                            theta = 0.0;
                        } else if ("Settings".equals(selected)) {
                            gameManager.setState(GameState.SETTINGS);
                        } else if ("Exit".equals(selected)) {
                            System.exit(0);
                        }
                    } else if (!InputManager.isKeyDown("MENU_SELECT")) {
                        menuSelectPressed = false;
                    }
                }
                
                // Settings menu handling
                if (gameManager.getCurrentState() == GameState.SETTINGS) {
                    if (InputManager.isKeyDown("MENU_UP") && !menuUpPressed) {
                        settingsMenu.navigateUp();
                        menuUpPressed = true;
                    } else if (!InputManager.isKeyDown("MENU_UP")) {
                        menuUpPressed = false;
                    }
                    
                    if (InputManager.isKeyDown("MENU_DOWN") && !menuDownPressed) {
                        settingsMenu.navigateDown();
                        menuDownPressed = true;
                    } else if (!InputManager.isKeyDown("MENU_DOWN")) {
                        menuDownPressed = false;
                    }
                    
                    if (InputManager.isKeyDown("MENU_SELECT") && !menuSelectPressed) {
                        menuSelectPressed = true;
                        if (settingsMenu.handleSelect()) {
                            // Back to main menu
                            gameManager.setState(GameState.MENU);
                        }
                    } else if (!InputManager.isKeyDown("MENU_SELECT")) {
                        menuSelectPressed = false;
                    }
                    
                    if (InputManager.isKeyDown("QUIT")) {
                        // ESC goes back to main menu
                        gameManager.setState(GameState.MENU);
                    }
                }
                
                // Track previous alive state to detect death
                boolean wasAlive = (player != null && player.isAlive());
                
                // Update projectiles and check collisions
                if (gameManager.getCurrentState() == GameState.PLAYING) {
                    projectileManager.update(gameManager.getPlayers(), finalLevel);
                    
                    // Check wall collision for player
                    player = gameManager.getLocalPlayer();
                    if (player != null && player.isAlive()) {
                        FysixObject playerShip = player.getShipObject();
                        if (playerShip != null) {
                            Wall hitWall = wallCollisionHandler.checkCollision(playerShip, finalLevel);
                            if (hitWall != null) {
                                // Capture velocity before collision response (for shockwave intensity)
                                Vector2d impactVelocity = playerShip.getVelocity();
                                double impactSpeed = impactVelocity.length();
                                Point2d impactPoint = playerShip.getPosition();
                                
                                // Player hit wall - take damage and bounce
                                wallCollisionHandler.handleCollision(playerShip, hitWall);
                                player.takeDamage(hitWall.getDamageOnCollision() > 0 ? hitWall.getDamageOnCollision() : 20);
                                
                                // Create shockwave effect at impact point
                                // Use impact speed to determine intensity (minimum 50 to ensure visible wave)
                                effectManager.createShockwave(impactPoint.x, impactPoint.y, Math.max(50.0, impactSpeed));
                            }
                        }
                    }
                    
                    // Check if player just died (transition from alive to dead)
                    if (wasAlive && player != null && !player.isAlive()) {
                        // Player just died - create explosion effect
                        FysixObject playerShip = player.getShipObject();
                        if (playerShip != null) {
                            Point2d pos = playerShip.getPosition();
                            effectManager.createLargeExplosion(pos.x, pos.y);
                            PlaySound.explosion();
                        }
                    }
                }
                
                // Handle background music when game state changes
                GameState currentState = gameManager.getCurrentState();
                if (currentState != previousState) {
                    if (currentState == GameState.PLAYING) {
                        PlaySound.startBackgroundMusic();
                    } else if (currentState == GameState.MENU || currentState == GameState.GAME_OVER || 
                               currentState == GameState.VICTORY) {
                        PlaySound.stopBackgroundMusic();
                    }
                    previousState = currentState;
                }
                
                // Update camera/viewport to follow player
                if (currentState == GameState.PLAYING) {
                    player = gameManager.getLocalPlayer();
                    if (player != null && player.isAlive()) {
                        FysixObject playerShip = player.getShipObject();
                        if (playerShip != null) {
                            // Calculate optimal zoom based on nearby walls and large objects
                            double optimalZoom = autoZoom.calculateOptimalZoom(
                                playerShip.getPosition(),
                                finalLevel.getWalls(),
                                levelObjects,
                                frameSize
                            );
                            
                            // Smooth zoom interpolation
                            double newScaleFactor = autoZoom.updateZoom(optimalZoom, deltaTimeSeconds);
                            
                            // Only update if autozoom is actually returning a different value
                            // This ensures autozoom is working
                            if (newScaleFactor > 0.1 && newScaleFactor < 100.0) {
                                scaleFactor = newScaleFactor;
                            }
                            
                            // Wrap around boundaries
                            if(viewCoordUL.x == 0 && playerShip.getPosition().x < 0){
                                playerShip.getPosition().x = finalLevel.getWidth() - 1;
                            }
                            if(viewCoordUL.y == 0 && playerShip.getPosition().y < 0){
                                playerShip.getPosition().y = finalLevel.getHeight() - 1;
                            }
                            if(viewCoordUL.x == (finalLevel.getWidth() * scaleFactor) - frameSize.width && playerShip.getPosition().x > finalLevel.getWidth()){
                                playerShip.getPosition().x = 1;
                            }
                            if(viewCoordUL.y == (finalLevel.getHeight() * scaleFactor) - frameSize.height && playerShip.getPosition().y > finalLevel.getHeight()){
                                playerShip.getPosition().y = 1;
                            }
                            
                            viewCoordUL.x = playerShip.getPosition().x * scaleFactor - frameSize.width / 2.0;
                            viewCoordUL.y = playerShip.getPosition().y * scaleFactor - frameSize.height / 2.0;
                            
                            // Apply screen shake offset
                            Point2d shakeOffset = effectManager.getScreenShakeOffset();
                            viewCoordUL.x += shakeOffset.x;
                            viewCoordUL.y += shakeOffset.y;
                            
                            if(viewCoordUL.x < 0) viewCoordUL.x = 0;
                            if(viewCoordUL.y < 0) viewCoordUL.y = 0;
                            if(viewCoordUL.x > (finalLevel.getWidth() * scaleFactor - frameSize.width)) 
                                viewCoordUL.x = finalLevel.getWidth() * scaleFactor - frameSize.width;
                            if(viewCoordUL.y > (finalLevel.getHeight() * scaleFactor - frameSize.height)) 
                                viewCoordUL.y = finalLevel.getHeight() * scaleFactor - frameSize.height;
                        }
                    }
                }

                // Rendering
                Graphics2D g2d = r.BeginRender();
                
                // Only render game world if not in menu
                if (gameManager.getCurrentState() != GameState.MENU) {
                    // Draw level walls
                    AffineTransform wallTransform = RenderHelper.getTransform();
                    wallTransform.translate(-viewCoordUL.x, -viewCoordUL.y);
                    wallTransform.scale(scaleFactor, scaleFactor);
                    g2d.setTransform(wallTransform);
                    g2d.setColor(Color.GRAY);
                    for (Wall wall : finalLevel.getWalls()) {
                        Polygon wallPoly = wall.toPolygon();
                        g2d.fillPolygon(wallPoly);
                    }
                    
                    // Draw gravity wells (planets and moons) with distortion effect
                    for (FysixObject planet : levelObjects) {
                        Point2d planetPos = planet.getPosition();
                        // Get distortion offset from shockwaves
                        double[] distortion = effectManager.getDistortion(planetPos.x, planetPos.y);
                        double offsetX = distortion[0] * scaleFactor;
                        double offsetY = distortion[1] * scaleFactor;
                        
                        AffineTransform planetTransform = RenderHelper.getTransform();
                        planetTransform.translate((planetPos.x * scaleFactor - viewCoordUL.x + offsetX), 
                                                 (planetPos.y * scaleFactor - viewCoordUL.y + offsetY));
                        planetTransform.scale(scaleFactor, scaleFactor);
                        g2d.setTransform(planetTransform);
                        g2d.setColor(planet.color);
                        Polygon bounds = planet.getBoundingArea();
                        // Always use fillPolygon for all gravity wells (planets and moons) for visibility
                        if (bounds != null) {
                            g2d.fillPolygon(bounds);
                        }
                    }
                    
                    // Draw player ship with distortion effect
                    player = gameManager.getLocalPlayer();
                    if (player != null && player.isAlive()) {
                        FysixObject playerShipObj = player.getShipObject();
                        if (playerShipObj != null) {
                            Point2d shipPos = playerShipObj.getPosition();
                            // Get distortion offset from shockwaves
                            double[] distortion = effectManager.getDistortion(shipPos.x, shipPos.y);
                            double offsetX = distortion[0] * scaleFactor;
                            double offsetY = distortion[1] * scaleFactor;
                            
                            AffineTransform shipTransform = RenderHelper.getTransform();
                            shipTransform.translate((shipPos.x * scaleFactor - viewCoordUL.x + offsetX), 
                                                   (shipPos.y * scaleFactor - viewCoordUL.y + offsetY));
                            shipTransform.rotate(theta);
                            shipTransform.scale(scaleFactor, scaleFactor);
                            g2d.setTransform(shipTransform);
                            g2d.setColor(Color.GREEN);
                            g2d.drawPolygon(ship);
                        }
                    }
                    
                    // Draw projectiles with distortion effect
                    g2d.setColor(Color.YELLOW);
                    AffineTransform projBaseTransform = RenderHelper.getTransform();
                    projBaseTransform.scale(scaleFactor, scaleFactor);
                    for (Projectile proj : projectileManager.getProjectiles()) {
                        FysixObject projObj = proj.getPhysicsObject();
                        Point2d projPos = projObj.getPosition();
                        // Get distortion offset from shockwaves
                        double[] distortion = effectManager.getDistortion(projPos.x, projPos.y);
                        double offsetX = distortion[0] * scaleFactor;
                        double offsetY = distortion[1] * scaleFactor;
                        
                        AffineTransform projTransform = RenderHelper.getTransform();
                        projTransform.translate((projPos.x * scaleFactor - viewCoordUL.x + offsetX), 
                                               (projPos.y * scaleFactor - viewCoordUL.y + offsetY));
                        projTransform.scale(scaleFactor, scaleFactor);
                        g2d.setTransform(projTransform);
                        g2d.fillRect(-2, -2, 4, 4);
                    }
                    
                    // Draw particle effects
                    effectManager.render(g2d, scaleFactor, viewCoordUL.x, viewCoordUL.y);
                }
                
                // Draw UI based on game state (reset transform to identity for UI)
                g2d.setTransform(RenderHelper.getTransform());
                if (gameManager.getCurrentState() == GameState.MENU) {
                    mainMenu.draw(g2d, frameSize);
                } else if (gameManager.getCurrentState() == GameState.SETTINGS) {
                    settingsMenu.draw(g2d, frameSize);
                } else if (gameManager.getCurrentState() == GameState.PLAYING) {
                    player = gameManager.getLocalPlayer();
                    if (player != null) {
                        HUD.drawHUD(g2d, player, frameSize);
                        if (!player.isAlive()) {
                            HUD.drawRespawnCountdown(g2d, player, frameSize);
                        }
                    }
                } else if (gameManager.getCurrentState() == GameState.GAME_OVER) {
                    player = gameManager.getLocalPlayer();
                    if (player != null) {
                        HUD.drawGameOver(g2d, player, frameSize);
                    }
                } else if (gameManager.getCurrentState() == GameState.VICTORY) {
                    player = gameManager.getLocalPlayer();
                    if (player != null) {
                        HUD.drawVictory(g2d, player, frameSize);
                    }
                } else if (gameManager.getCurrentState() == GameState.PAUSED) {
                    HUD.drawPause(g2d, frameSize);
                }
                
                r.EndRender();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
        
        r.Destroy();
    }
    
    private static Polygon createCirclePolygon(int radius, int points) {
        Polygon circle = new Polygon();
        for (int i = 0; i < points; i++) {
            double angle = 2.0 * Math.PI * i / points;
            int x = (int) (radius * Math.cos(angle));
            int y = (int) (radius * Math.sin(angle));
            circle.addPoint(x, y);
        }
        return circle;
    }
}
