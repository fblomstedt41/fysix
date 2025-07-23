package net.force2dev.fysix;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import net.sf.janalogtv.AnalogTV;
import net.sf.janalogtv.Input;
import net.sf.janalogtv.Reception;

/**
 *
 * @author xnvmakl
 */
public class Renderer {
    private DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {

        new DisplayMode(2560, 1600, 32, 60),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(800, 600, 32, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 8, 0)
    };

    private Frame mainFrame;
    private BufferStrategy bufferStrategy;
    private GraphicsDevice device;
    private Rectangle bounds;
	private AnalogTV tv;
	private Input tvInput;
	private Reception tvReception;
	private BufferedImage tvFrameBuffer;
    
	public void Destroy() {
        device.setFullScreenWindow(null);
	}

	public void Initialise() {
    	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	device = env.getDefaultScreenDevice();

    	try {
            int numBuffers = 2;
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            mainFrame.setCursor(null);
//          TODO: Test rendering...            
//            mainFrame.setSize(750,650);
            
            // /*
            device.setFullScreenWindow(mainFrame);
            if (device.isDisplayChangeSupported()) {
                chooseBestDisplayMode(device);
            }    
            // */
            bounds = mainFrame.getBounds();
            
//          TODO: Test rendering...
//            mainFrame.setLocation(750 + bounds.x, 650 + bounds.y);
//            mainFrame.getComponentAt(750, 650);
//            mainFrame.setVisible(true);
            mainFrame.setVisible(true);
            mainFrame.createBufferStrategy(numBuffers);
            bufferStrategy = mainFrame.getBufferStrategy();
            
            tv = new AnalogTV(bounds.width, bounds.height, gc);
            tv.setBrightnessControl(0.02);
            tv.setFlutterHorizDesync(true);
            tvInput = new Input();
            tvInput.setupSync(true, false);
            tvReception = new Reception(tvInput, 0.75);
            tvFrameBuffer = Input.createFrameBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DisplayMode getBestDisplayMode(GraphicsDevice device) {
        for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
            DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth()
                   && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
                   && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()
                   ) {
                    return BEST_DISPLAY_MODES[x];
                }
            }
        }
        return null;
    }
    
    private void chooseBestDisplayMode(GraphicsDevice device) {
        DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }

    private Graphics2D g2d;
	private boolean useTv = false;
    
	public void setTvEffect(boolean tv) {
		useTv = tv;
	}
	
	public boolean isTvEffect() {
		return useTv;
	}
	
	public Dimension getSize() {
		if (useTv) {
			return new Dimension(tvFrameBuffer.getWidth(), tvFrameBuffer.getHeight());
		} else {
			return mainFrame.getSize();
		}
	}
	
	public Graphics2D BeginRender() {
		if (useTv) {
			g2d = tvFrameBuffer.createGraphics();
		} else {
			g2d = (Graphics2D)bufferStrategy.getDrawGraphics();
		}
		Dimension d = getSize();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, d.width, d.height);
		return g2d;
	}

	public void EndRender() {
		g2d.dispose();
		if (false/*useTv*/) {
			tvInput.blitFrameBuffer(tvFrameBuffer);
			tvReception.update();
			tv.initSignal(0.02);
			tv.addSignal(tvReception);
			Graphics g = bufferStrategy.getDrawGraphics();
			tv.draw(g);
			g.dispose();
		}
		if (!bufferStrategy.contentsLost())
		{
			bufferStrategy.show();
		}
	}

	public Component GetComponent() {
		return mainFrame;
	}
}
