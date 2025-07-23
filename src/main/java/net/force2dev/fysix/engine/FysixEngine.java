package net.force2dev.fysix.engine;

import java.awt.Color;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class FysixEngine {

    private long lastTime = System.currentTimeMillis();
    private static FysixEngine engine;
    private FysixWorld fWorld = new FysixWorld();

    public static FysixEngine GetContext() {
        if (engine == null) {
            engine = new FysixEngine();
        }
        return engine;
    }

	public IControllable AddObject(double posX, double posY, double mass, IEventCallback cb) {
        FysixObject newObj = new FysixObject();
        newObj.setMass(mass);
        newObj.setPosition(new Point2d(posX, posY));
        newObj.registerEventCallback(cb);
        fWorld.addObject(newObj);
        return newObj;
    }
	
    public void RemoveObject(IControllable obj) {
        fWorld.removeObject((FysixObject) obj);
    }

	public void Tick(Environment env) {
        long deltaTime = System.currentTimeMillis() - lastTime;
        lastTime += deltaTime;
        
        /* TODO:
         *  - Calculate elastic collision velocity
         *  - Calculate gravity affect acceleration on each object
         *  - Calculate new velocity on each object
         *    * Consider environmental parameters
         *  - Do collision detection (using new velocity)
         *  - Update position on each object
         */
        
        // TODO: Gravity detection 
        for (int i = 0; i < fWorld.objects.size(); i++) {
        	FysixObject fo1 = (FysixObject) fWorld.objects.get(i);        	     		    		
        	for (int j = i+1; j < fWorld.objects.size(); j++) {
            	FysixObject fo2 = (FysixObject) fWorld.objects.get(j);
            	Vector2d dist = new Vector2d(0,0);
            	dist.sub(fo2.getPosition(), fo1.getPosition());
            	int len = (int)dist.length();
            	if(len>0 && len < 250){ // Affect area...
            	  //Vector2d alfa = new Vector2d(fo1.getPosition());
            	  FysixObject foA;
            	  FysixObject foB;
            	  if(fo1.getMass() <= fo2.getMass()){
            	  	foA = fo1;
            	  	foB = fo2;
            	  } else {
            	  	foA = fo2;
            	  	foB = fo1;            	  	
            	  }
            	
            	  double A = Math.abs(foB.getPosition().y-foA.getPosition().y);
            	  double B = Math.abs(foB.getPosition().x-foA.getPosition().x);
            	  double a = Math.atan(A/B);             	  
            	  
            	  if(foA.getPosition().x >= foB.getPosition().x &&
               	  	 foA.getPosition().y >= foB.getPosition().y )
               	  {
               	    a = Math.PI - a;
               	  }
            	  else if(foA.getPosition().x >= foB.getPosition().x &&
               	  	 foA.getPosition().y <= foB.getPosition().y )
               	  {
               	    a = Math.PI + a;
               	  }
            	  else if(foA.getPosition().x <= foB.getPosition().x &&
               	  	 foA.getPosition().y <= foB.getPosition().y )
               	  {
               	    a = 2*Math.PI - a;
               	  }
            	  
            	  double G = 0.0000006;
            	  double gravForce = G*foA.getMass()*foB.getMass()/len*len;
            	  
            	  Vector2d gravAcc = new Vector2d(gravForce*Math.cos(-a), -gravForce*Math.sin(a));            	 
                  Vector2d gravVelocity = new Vector2d(0,0);
                  
                  // v = v0 + a * dt
                  gravAcc.scale(deltaTime / 1000.0);
                  gravVelocity.add(foA.getVelocity());
                  gravVelocity.add(gravAcc);
                  foA.setVelocity(gravVelocity);
            	}
        	}
        }
        
        for (Iterator i = fWorld.getAllObjects(); i.hasNext(); ) {
            FysixObject fo = (FysixObject) i.next();
            Vector2d totAcc = env.getEnvironmentAccelerationAtPoint(fo.getPosition());
            Vector2d newVelocity = new Vector2d(0,0);
            
            // v = v0 + a * dt
            totAcc.add(fo.getAcceleration());
            totAcc.scale(deltaTime / 1000.0);
            newVelocity.add(fo.getVelocity());
            newVelocity.add(totAcc);
            newVelocity.scale(env.getEnvironmentalResistanceAtPoint(fo.getPosition()));
            fo.setVelocity(newVelocity);
        }
        
        // TODO: Collision detection
        for (int i = 0; i < fWorld.objects.size(); i++) {
        	FysixObject fo1 = (FysixObject) fWorld.objects.get(i);
        	for (int j = i+1; j < fWorld.objects.size(); j++) {
            	FysixObject fo2 = (FysixObject) fWorld.objects.get(j);
        		if (FysixCollisionDetector.checkCollision(fo1, fo2)) {
        	        // TODO: Elastic collision detection
        			// vn1 = 2*m2*vo2+vo1*(m1-m2) / (m2+m1) 
        			// vn2 = 2*m1*vo1+vo2*(m2-m1) / (m1+m2)
        			
        			/*fo1.getVelocity().x = 2*fo2.getMass()*fo2.getVelocity().x+fo1.getVelocity().x*(fo1.getMass()-fo2.getMass())/(fo1.getMass()+fo2.getMass());
        			fo1.getVelocity().y = 2*fo2.getMass()*fo2.getVelocity().y+fo1.getVelocity().y*(fo1.getMass()-fo2.getMass())/(fo1.getMass()+fo2.getMass());
        			
        			fo2.getVelocity().x = 2*fo1.getMass()*fo1.getVelocity().x+fo2.getVelocity().x*(fo2.getMass()-fo1.getMass())/(fo1.getMass()+fo2.getMass());
        			fo2.getVelocity().y = 2*fo1.getMass()*fo1.getVelocity().y+fo2.getVelocity().y*(fo2.getMass()-fo1.getMass())/(fo1.getMass()+fo2.getMass());
        			*/

        			fo1.color = Color.RED;
        			fo2.color = Color.RED;
        		} else {
        			fo1.color = Color.GREEN;
        			fo2.color = Color.GREEN;
        		}
        	}
        }               
        
        for (Iterator i = fWorld.getAllObjects(); i.hasNext(); ) {
            FysixObject fo = (FysixObject) i.next();
            Vector2d move;
            
            // s = v * dt
            move = (Vector2d) fo.getVelocity().clone();
            move.scale(deltaTime / 1000.0);
            fo.getPosition().add(move);
            fWorld.updateObject(fo);
        }
    }
}
