package me.cometkaizo.util;

import me.cometkaizo.Main;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ImageUtils {

    public static BufferedImage readImage(String path) {
        try {
            URL resourceURL = Main.class.getResource(path);
            return resourceURL == null ? null : ImageIO.read(resourceURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Future<BufferedImage> readImage(ExecutorService executor, String path) {
        return executor.submit(() -> readImage(path));
    }
    public static BufferedImage rotateDegrees(BufferedImage src, BufferedImage dst, double deg) {
        return rotateRadians(src, dst, Math.toRadians(deg));
    }
    public static BufferedImage rotateRadians(BufferedImage src, BufferedImage dst, double rads) {
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int dstW = (int) Math.floor(src.getWidth() * cos + src.getHeight() * sin);
        final int dstH = (int) Math.floor(src.getHeight() * cos + src.getWidth() * sin);
        if (dst == null) dst = new BufferedImage(dstW, dstH, src.getType());
        AffineTransform transform = new AffineTransform();
        transform.translate((int) (dstW / 2F), (int) (dstH / 2F));
        transform.rotate(rads,0, 0);
        transform.translate((int) (-src.getWidth() / 2F), (int) (-src.getHeight() / 2F));
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(src, dst);
    }

    private ImageUtils() {
        throw new AssertionError("No ImageUtils instances for you!");
    }

}
