package com.example.jakarta.hello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;

public class ImageUtil {

    private ImageUtil() {}

    public static byte[] generateImageBytes(String formatName) {
        return generateImageBytes(formatName, 320, 320);
    }

    public static byte[] generateImageBytes(String formatName, int width, int height) {
        BufferedImage image = generateImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(image, formatName, baos);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public static BufferedImage generateImage() {
        return generateImage(320, 320);
    }

    public static BufferedImage generateImage(final int width, final int height) {
        BufferedImage image = null;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) image.getGraphics();

        Random rnd = new Random();

        Color background = getRandomColorInHSB(rnd, 0f, 0.1f, 0.9f, 1f);
        Color foreground = getRandomColorInHSB(rnd, 0.4f, 1f, 0.7f, 1f);

        // fill background
        g.setColor(background);
        g.fillRect(0, 0, width, height);

        // draw face
        int faceWidth = getRandomIntBetween(rnd, width * 0.8, width * 0.9);
        int faceHeight = getRandomIntBetween(rnd, height * 0.8, height * 0.9);
        g.setColor(foreground);
        g.fillOval((width - faceWidth) / 2, (height - faceHeight) / 2, faceWidth, faceHeight);

        // draw eyes
        g.setColor(Color.WHITE);
        g.fillOval((int)(width * 0.25), (int)(height * 0.25), (int)(width * 0.2), (int)(height * 0.3));
        g.fillOval((int)(width * 0.55), (int)(height * 0.25), (int)(width * 0.2), (int)(height * 0.3));

        // draw pupils
        g.setColor(Color.BLACK);
        g.fillOval((int)(width * 0.325), (int)(height * 0.35), (int)(width * 0.1), (int)(height * 0.15));
        g.fillOval((int)(width * 0.575), (int)(height * 0.35), (int)(width * 0.1), (int)(height * 0.15));

        // draw mouth
        CubicCurve2D c = new CubicCurve2D.Double();
        c.setCurve(
            getRandomPointInRect(rnd, width * 0.2, height * 0.55, width * 0.1, height * 0.2),
            getRandomPointInRect(rnd, width * 0.3, height * 0.65, width * 0.1, height * 0.2),
            getRandomPointInRect(rnd, width * 0.6, height * 0.65, width * 0.1, height * 0.2),
            getRandomPointInRect(rnd, width * 0.7, height * 0.55, width * 0.1, height * 0.2)
        );

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke((int)(width * 0.01)));
        g.draw(c);

        return image;
    }

    public static int getRandomIntBetween(Random rnd, double x, double y) {
        return getRandomIntBetween(rnd, (int)x, (int)y);
    }

    public static int getRandomIntBetween(Random rnd, int x, int y) {
        return rnd.nextInt(y - x) + x;
    }

    public static Point2D getRandomPointInRect(Random rnd, double x, double y, double width, double height) {
        double randomX = rnd.nextDouble(width) + x;
        double randomY = rnd.nextDouble(height) + y;

        return new Point2D.Double(randomX, randomY);
    }

    public static Color getRandomColorInHSB(Random rnd, float minSaturation, float maxSaturation, float minBrightness, float maxBrightness) {
        float hue = rnd.nextFloat();
        float sturation = rnd.nextFloat() * (maxSaturation - minSaturation) + minSaturation;
        float brightness = rnd.nextFloat() * (maxBrightness - minBrightness) + minBrightness;
        int rgb = Color.HSBtoRGB(hue, sturation, brightness);
        return new Color(rgb);
    }

    public static Color getRandomColorBetween(Random rnd, Color c1, Color c2) {
        int r1 = c1.getRed();
        int g1 = c1.getGreen();
        int b1 = c1.getBlue();

        int r2 = c2.getRed();
        int g2 = c2.getGreen();
        int b2 = c2.getBlue();

        int r = signum(r2 - r1) * rnd.nextInt(Math.abs(r2 - r1)) + r1;
        int g = signum(g2 - g1) * rnd.nextInt(Math.abs(g2 - g1)) + g1;
        int b = signum(b2 - b1) * rnd.nextInt(Math.abs(b2 - b1)) + b1;

        return new Color(r, g, b);
    }

    public static int signum(int value) {
        if (value > 0) {
            return 1;
        } else if (value < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
