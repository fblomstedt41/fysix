package net.force2dev.fysix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.force2dev.fysix.engine.Environment;
import net.force2dev.fysix.engine.FysixEngine;
import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.equipment.Engine;
import net.force2dev.fysix.sound.PlaySound;


public class FysixMain {

    static boolean running;
    
    public static void main(String args[]) {
    	Vector bullets = new Vector();
    	int delay = 0;
    	Renderer r = new Renderer();
    	
        FysixEngine fe = FysixEngine.GetContext();
        Environment env = new Environment() {
            public Vector2d getEnvironmentAccelerationAtPoint(Point2d p) {
                return new Vector2d(0.0,0.0);
            }
			public double getEnvironmentalResistanceAtPoint(Point2d p) {
				return 1.0;
			}
        };
        
        r.Initialise();        
        
        InputManager.Initialise(r.GetComponent());
        InputManager.MapKey(KeyEvent.VK_ESCAPE, "QUIT");
        InputManager.MapKey(KeyEvent.VK_DOWN, "THRUST");
        InputManager.MapKey(KeyEvent.VK_LEFT, "LEFT");
        InputManager.MapKey(KeyEvent.VK_RIGHT, "RIGHT");
        InputManager.MapKey(KeyEvent.VK_UP, "FIRE");
        // Test Scale...
        InputManager.MapKey(KeyEvent.VK_F1, "SCALE_IN");
        InputManager.MapKey(KeyEvent.VK_F2, "SCALE_OUT");
        InputManager.MapKey(KeyEvent.VK_F3, "TOGGLE_TV");
                
        FysixObject fo1 = (FysixObject) fe.AddObject(200, 200, 150.0, null);
        FysixObject fo2 = (FysixObject) fe.AddObject(400, 150, 0.5, null);        
        //FysixObject planet = (FysixObject) fe.AddObject(400, 350, 1000000.0, null);
        //FysixObject moon = (FysixObject) fe.AddObject(580, 200, 50.0, null);
        //FysixObject fo2 = (FysixObject) fe.AddObject(2600, 2400, 0.5, null);        
        FysixObject planet = (FysixObject) fe.AddObject(2600, 2600, 1600000.0, null);
        FysixObject moon = (FysixObject) fe.AddObject(2780, 2550, 50.0, null);
        
        moon.setVelocity(new Vector2d(30.0, 85.0));
        
        running = true;

        //Vector2d accUp = new Vector2d(0,-40);
        Vector2d accNone = new Vector2d(0,0);
        Polygon ship = new Polygon();
        ship.addPoint(-4, -4);
        ship.addPoint(-4, 4);
        ship.addPoint(7, 0);
        fo1.setBoundingArea(ship);
        
        Polygon enemy = new Polygon();
        enemy.addPoint(-4, -4);
        enemy.addPoint(-4, 4);
        enemy.addPoint(7, 0);
        fo2.setBoundingArea(enemy);
        
        Polygon planetCircle = new Polygon();
        // Q1
        planetCircle.addPoint(100,0);
        planetCircle.addPoint(90,-40);
        planetCircle.addPoint(70,-70);
        planetCircle.addPoint(40,-90);
        // Q2
        planetCircle.addPoint(0,-100);
        planetCircle.addPoint(-40,-90);
        planetCircle.addPoint(-70,-70);
        planetCircle.addPoint(-90,-40);
        // Q3
        planetCircle.addPoint(-100,0);
        planetCircle.addPoint(-90,40);
        planetCircle.addPoint(-70,70);
        planetCircle.addPoint(-40,90);
        // Q4
        planetCircle.addPoint(0,100);
        planetCircle.addPoint(40,90);
        planetCircle.addPoint(70,70);
        planetCircle.addPoint(90,40);
        
        planet.setBoundingArea(planetCircle);
        
        Polygon moonCircle = new Polygon();
        // Q1
        moonCircle.addPoint(10,0);
        moonCircle.addPoint(9,-4);
        moonCircle.addPoint(7,-7);
        moonCircle.addPoint(4,-9);
        // Q2
        moonCircle.addPoint(0,-10);
        moonCircle.addPoint(-4,-9);
        moonCircle.addPoint(-7,-7);
        moonCircle.addPoint(-9,-4);
        // Q3
        moonCircle.addPoint(-10,0);
        moonCircle.addPoint(-9,4);
        moonCircle.addPoint(-7,7);
        moonCircle.addPoint(-4,9);
        // Q4
        moonCircle.addPoint(0,10);
        moonCircle.addPoint(4,9);
        moonCircle.addPoint(7,7);
        moonCircle.addPoint(9,4);
        
        moon.setBoundingArea(moonCircle);
        
        Engine mainEngine = new Engine(120, 50, 10, 10000000, 0.000005, 0, 0);
        // Mount to ship...
        
        // Engine dust
        Polygon engineDust = new Polygon();
        engineDust.addPoint(0,0);
        engineDust.addPoint(1,0);
        class DustObject{
        	public int time;
        	FysixObject fo;
        }
        Vector dustVector = new Vector(1000);
        Color dustColor[] = new Color[]{Color.GRAY, Color.RED, Color.ORANGE, Color.YELLOW};
        double theta = 0;
        
        // Test scale
        double scaleFactor = 1.0;
        
        // Outer border
        Polygon borderPoly = new Polygon();
        // Upper side
        borderPoly.addPoint(  2,2);
        borderPoly.addPoint(600,2);
        borderPoly.addPoint(1200,600);
        borderPoly.addPoint(1800,600);
        borderPoly.addPoint(2400,2);
        borderPoly.addPoint(3000,2);
        borderPoly.addPoint(3600,600);
        borderPoly.addPoint(4200,600);
        borderPoly.addPoint(4800,2);
        borderPoly.addPoint(5398,2);
        // Right side
        borderPoly.addPoint(5398,600);
        borderPoly.addPoint(4800,1200);
        borderPoly.addPoint(4800,1800);
        borderPoly.addPoint(5398,2400);
        borderPoly.addPoint(5398,3000);
        borderPoly.addPoint(4800,3600);
        borderPoly.addPoint(4800,4200);
        borderPoly.addPoint(5398,4800);
        borderPoly.addPoint(5398,5398);
        // Lower side
        borderPoly.addPoint(4800,5398);
        borderPoly.addPoint(4200,4800);
        borderPoly.addPoint(3600,4800);
        borderPoly.addPoint(3000,5398);
        borderPoly.addPoint(2400,5398);
        borderPoly.addPoint(1800,4800);
        borderPoly.addPoint(1200,4800);
        borderPoly.addPoint( 600,5398);
        borderPoly.addPoint(   2,5398);        
        // Left side
        borderPoly.addPoint(   2,4800);
        borderPoly.addPoint( 600,4200);
        borderPoly.addPoint( 600,3600);
        borderPoly.addPoint(   2,3000);
        borderPoly.addPoint(   2,2400);
        borderPoly.addPoint( 600,1800);
        borderPoly.addPoint( 600,1200);
        borderPoly.addPoint(   2, 600);
        borderPoly.addPoint(   2,   0);       
        // Border around all sides
        borderPoly.addPoint(   0,   0);
        borderPoly.addPoint(   0,5400);
        borderPoly.addPoint(5400,5400);
        borderPoly.addPoint(5400,   0);
        borderPoly.addPoint(   0,   0);
        
        /*
        borderPoly.addPoint(2,2);
        borderPoly.addPoint(5398,2);
        borderPoly.addPoint(5398,5398);
        borderPoly.addPoint(2,5398);
        borderPoly.addPoint(2,2);
        borderPoly.addPoint(0,0);
        borderPoly.addPoint(0,5400);
        borderPoly.addPoint(5400,5400);
        borderPoly.addPoint(5400,0);
        borderPoly.addPoint(0,0);
        */
        
        // Level border object 1
        Polygon levelPoly01 = new Polygon();
        // TL corner
        levelPoly01.addPoint(1200,1200);
        levelPoly01.addPoint(1800,1200);
        levelPoly01.addPoint(2400, 900);
        levelPoly01.addPoint(3000, 900);
        levelPoly01.addPoint(3600,1200);
        levelPoly01.addPoint(4200,1200);        
        // TR corner
        levelPoly01.addPoint(4200,1800);
        levelPoly01.addPoint(4500,2400);
        levelPoly01.addPoint(4500,3000);
        levelPoly01.addPoint(4200,3600);
        levelPoly01.addPoint(4200,4200);        
        // LL corner
        levelPoly01.addPoint(3600,4200);
        levelPoly01.addPoint(3000,4500);
        levelPoly01.addPoint(2800,4200);        
        // ML corner
        levelPoly01.addPoint(2800,3900);
        levelPoly01.addPoint(3000,3900);
        levelPoly01.addPoint(3600,3600);
        levelPoly01.addPoint(3900,3000);
        levelPoly01.addPoint(3900,2400);
        levelPoly01.addPoint(3600,1800);
        levelPoly01.addPoint(3000,1500);
        levelPoly01.addPoint(2400,1500);
        // Inner UL corner
        levelPoly01.addPoint(1800,1800);
        levelPoly01.addPoint(1500,2400);
        levelPoly01.addPoint(1500,3000);
        levelPoly01.addPoint(1800,3600);
        levelPoly01.addPoint(2400,3900);
        // MR corner
        levelPoly01.addPoint(2600,3900);
        levelPoly01.addPoint(2600,4200);
        levelPoly01.addPoint(2400,4500);
        levelPoly01.addPoint(1800,4200);
        // LR corner
        levelPoly01.addPoint(1200,4200);
        levelPoly01.addPoint(1200,3600);
        levelPoly01.addPoint( 900,3000);
        levelPoly01.addPoint( 900,2400);
        levelPoly01.addPoint(1200,1800);
        levelPoly01.addPoint(1200,1200);
        
        Point2d viewCoordUL = new Point2d();
        viewCoordUL.x = fo1.getPosition().x;
        viewCoordUL.y = fo1.getPosition().y;
        
        Dimension frameSize = r.getSize();
        
        boolean tvToggleDown = false;		// Ugly hack due to InputManager not supporting "single hit" buttons
        
        while (running) {
            try {
                Thread.sleep(10);
                
                if (InputManager.isKeyDown("QUIT")) {
                    System.exit(0);
                }
                if (InputManager.isKeyDown("TOGGLE_TV") && !tvToggleDown) {
                	tvToggleDown = true;
                	r.setTvEffect(!r.isTvEffect());
                	Dimension oldSize = frameSize;
                	frameSize = r.getSize();
                	scaleFactor /= oldSize.width / (double) frameSize.width;
                } else if (!InputManager.isKeyDown("TOGGLE_TV") && tvToggleDown) {
                	tvToggleDown = false;
                }
                if (InputManager.isKeyDown("LEFT")) {
                    theta -= Math.PI/20;
                    fo1.setDirection(theta);
                }
                if (InputManager.isKeyDown("RIGHT")) {
                    theta += Math.PI/20;
                    fo1.setDirection(theta);
                }
                if (InputManager.isKeyDown("THRUST")) {
                	
                	PlaySound.thrust();
                    
//                	Matrix3d mat = new Matrix3d();
//                	mat.rotZ(theta);
//                	Vector3d tmp = new Vector3d(1, 0, 0);
//                	mat.transform(tmp);
//                	tmp.scale(50);
//                  fo.setAcceleration(new Vector2d(tmp.x, tmp.y));
//                	fo1.setDirection(theta);
                	Vector2d newAcc = new Vector2d(fo1.getDirection());
                	newAcc.scale(120);
                	fo1.setAcceleration(newAcc);
                	
                	DustObject dustObj = new DustObject();                	
                	dustObj.time = 39; // 0-39 : 40 units (map against color list)
                	dustObj.fo = (FysixObject) fe.AddObject(fo1.getPosition().x, fo1.getPosition().y, 5, null);
                	double dustA = Math.random()*2.0*Math.PI+1.0;
                	if(dustA >= Math.PI){
                		dustA -= Math.PI;
                	}
                	dustObj.fo.setDirection(dustA+Math.PI);
                	Vector2d dustAcc = new Vector2d(dustObj.fo.getDirection());
                	dustAcc.scale(220);
                	dustObj.fo.setAcceleration(dustAcc);
                	dustVector.add(dustObj);
                	
                } else {
                    fo1.setAcceleration(accNone);
                }
                if (InputManager.isKeyDown("FIRE")) {
                	if (++delay == 6) {
                		delay = 0;
                		FysixObject fo = (FysixObject) fe.AddObject(fo1.getPosition().x, fo1.getPosition().y, 0.000000001, null);
                		Vector2d vel = (Vector2d) fo1.getDirection().clone();
                		vel.scale(450);
                		//vel.add(fo1.getVelocity());
                		fo.setVelocity(vel);
                		Polygon ba = new Polygon();
                		ba.addPoint(-1, 0);
                		ba.addPoint(1, 0);
                		fo.setBoundingArea(ba);
                		bullets.add(fo);
                	}
                }
                if (InputManager.isKeyDown("SCALE_IN")) {
                    scaleFactor += 0.01;
                    if(scaleFactor>= (frameSize.width/400.0)){
                    	scaleFactor = (frameSize.width/400.0);
                    }
                }
                if (InputManager.isKeyDown("SCALE_OUT")) {
                    scaleFactor -= 0.01;
                    if(scaleFactor <= (frameSize.width/5400.0)){
                    	scaleFactor = (frameSize.width/5400.0);
                    }
                }
                
                fe.Tick(env);
                
                if(viewCoordUL.x==0 && fo1.getPosition().x<0){
                	fo1.getPosition().x = 5400-1;
                }
                if(viewCoordUL.y==0 && fo1.getPosition().y<0){
                	fo1.getPosition().y = 5400-1;
                }
                if(viewCoordUL.x==(5400*scaleFactor)-frameSize.width && fo1.getPosition().x>5400){
                	fo1.getPosition().x = 1;
                }
                if(viewCoordUL.y==(5400*scaleFactor)-frameSize.height && fo1.getPosition().y>5400){
                	fo1.getPosition().y = 1;
                }
                
                viewCoordUL.x = fo1.getPosition().x*scaleFactor-frameSize.width/2.0;
                viewCoordUL.y = fo1.getPosition().y*scaleFactor-frameSize.height/2.0;
                

//                System.out.println(" X: " + fo1.getPosition().x*scaleFactor + " , Y: " + fo1.getPosition().y*scaleFactor + " CX: " + fo1.getPosition().x + " , CY: " + fo1.getPosition().y);
                
                if(viewCoordUL.x<0) viewCoordUL.x = 0;
                if(viewCoordUL.y<0) viewCoordUL.y = 0;
                if(viewCoordUL.x>(5400*scaleFactor-frameSize.width)) viewCoordUL.x = 5400*scaleFactor-frameSize.width;
                if(viewCoordUL.y>(5400*scaleFactor-frameSize.height)) viewCoordUL.y = 5400*scaleFactor-frameSize.height;

                Graphics2D g2d = r.BeginRender();
                //AffineTransform af = g2d.getTransform();
                
                // PLAYER SHIP
                g2d.setTransform(new AffineTransform());
                g2d.setColor(fo1.color);                
                g2d.translate((fo1.getPosition().x*scaleFactor-viewCoordUL.x), (fo1.getPosition().y*scaleFactor-viewCoordUL.y));
                g2d.rotate(theta);
                g2d.scale(scaleFactor, scaleFactor);
                g2d.drawPolygon(ship);
                
                // POSITION
                g2d.setTransform(new AffineTransform());
                g2d.setColor(Color.BLUE);                
                 g2d.drawString("X: "+ (int)fo1.getPosition().x + " Y: "+ (int)fo1.getPosition().y, 
                		(int)((fo1.getPosition().x*scaleFactor-viewCoordUL.x+10)), 
                		(int)((fo1.getPosition().y*scaleFactor-viewCoordUL.y+10)) );

                // ENEMY
                g2d.setTransform(new AffineTransform());
                g2d.setColor(fo2.color);
                g2d.translate((fo2.getPosition().x*scaleFactor-viewCoordUL.x), (fo2.getPosition().y*scaleFactor-viewCoordUL.y));
                g2d.scale(scaleFactor, scaleFactor);
                g2d.drawPolygon(enemy);                

                // BULLET
            	g2d.setColor(Color.YELLOW);
                for (Iterator i = bullets.iterator(); i.hasNext(); ) {
                    g2d.setTransform(new AffineTransform());
                	FysixObject fo = (FysixObject) i.next();
                	g2d.translate((fo.getPosition().x-viewCoordUL.x)*scaleFactor, (fo.getPosition().y-viewCoordUL.y)*scaleFactor);
                	g2d.scale(scaleFactor, scaleFactor);
                	g2d.drawRect(-1, -1, 2, 2);                	
                }
                
                // PLANET 
                g2d.setTransform(new AffineTransform());
                g2d.setColor(planet.color);
                g2d.translate((planet.getPosition().x*scaleFactor-viewCoordUL.x), (planet.getPosition().y*scaleFactor-viewCoordUL.y));
                g2d.scale(scaleFactor, scaleFactor);
                g2d.drawPolygon(planetCircle);
				
                // MOON
                g2d.setTransform(new AffineTransform());
                g2d.setColor(moon.color);
                g2d.translate((moon.getPosition().x*scaleFactor-viewCoordUL.x), (moon.getPosition().y*scaleFactor-viewCoordUL.y));
                g2d.scale(scaleFactor, scaleFactor);
                g2d.drawPolygon(moonCircle);                
                
                // Engine Dust
                Vector dustToRemove = new Vector();
                for (Iterator i = dustVector.iterator(); i.hasNext(); ) {
                    g2d.setTransform(new AffineTransform());
                	DustObject dustObj = (DustObject) i.next();
                	g2d.translate((dustObj.fo.getPosition().x*scaleFactor-viewCoordUL.x), (dustObj.fo.getPosition().y*scaleFactor-viewCoordUL.y));
                	g2d.setColor(dustColor[(dustObj.time/10)]);
                	g2d.scale(scaleFactor, scaleFactor);
                	g2d.drawPolygon(engineDust);                	
                	dustObj.time--;
                	if(dustObj.time<=0){
                		dustToRemove.add(dustObj);
                	}
                	if(dustObj.time == 32){
                		dustObj.fo.setAcceleration(accNone);
                	}
                }
                for (Iterator i = dustToRemove.iterator(); i.hasNext(); ) {
                	DustObject dustObj = (DustObject) i.next();
                	fe.RemoveObject(dustObj.fo);                	
                	dustVector.remove(dustObj);                	
                }
                dustToRemove.clear();
				
                // LEVEL BORDER
                AffineTransform af = new AffineTransform();
                af.translate(-viewCoordUL.x,-viewCoordUL.y);
                g2d.setTransform(af);
                g2d.setColor(Color.GRAY);
                g2d.scale(scaleFactor, scaleFactor);
//                g2d.transform(af);
                g2d.fillPolygon(borderPoly);
                
                // LEVEL 2
                g2d.setTransform(new AffineTransform());
                g2d.setColor(Color.GRAY);
                g2d.translate(-viewCoordUL.x,-viewCoordUL.y);
                g2d.scale(scaleFactor, scaleFactor);
                g2d.fillPolygon(levelPoly01);
                
                g2d.setStroke(new BasicStroke(1.0f));
                
                r.EndRender();
            } catch (InterruptedException ex) {
                System.out.println("Error: " + ex.getLocalizedMessage());
            }
        }
        
        r.Destroy();
    }
}
