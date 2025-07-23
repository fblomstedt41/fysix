package net.sf.janalogtv;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

public class Input {
    public static BufferedImage createFrameBuffer() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    }
    public void setupSync(boolean a, boolean b) {}
    public void blitFrameBuffer(BufferedImage img) {}
}
