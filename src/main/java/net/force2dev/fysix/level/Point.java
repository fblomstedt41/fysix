package net.force2dev.fysix.level;

/**
 * Represents a 2D point with integer coordinates (pixel-perfect for retro style)
 */
public record Point(int x, int y) {
    public Point(double x, double y) {
        this((int) Math.round(x), (int) Math.round(y));
    }
}

