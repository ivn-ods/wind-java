package ua.od.wind.ImageGenerators;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArrowSmall {

    public static byte[] makeImage(float dir) throws IOException {

        int wx = 30;
        int wy = 30;
        int x = wx / 2;
        int y = wy / 2;
        int k = (int) (wx * 0.07);
        dir += 6;


        int xl1 = (int) (k * 3.72 * Math.sin(Math.toRadians(dir * 30 + 15)));
        int yl1 = (int) (k * 3.72 * Math.cos(Math.toRadians(dir * 30 + 15)));

        int xl2 = (int) (k * 4.92 * Math.sin(Math.toRadians(dir * 30 + 43)));
        int yl2 = (int) (k * 4.92 * Math.cos(Math.toRadians(dir * 30 + 43)));

        int xl3 = (int) (k * 6.93 * Math.sin(Math.toRadians(dir * 30)));
        int yl3 = (int) (k * 6.93 * Math.cos(Math.toRadians(dir * 30)));

        int xl4 = (int) (k * 6.48 * Math.sin(Math.toRadians(dir * 30 + 172)));
        int yl4 = (int) (k * 6.48 * Math.cos(Math.toRadians(dir * 30 + 172)));

        int xr1 = (int) (k * 3.72 * Math.sin(Math.toRadians(dir * 30 - 15)));
        int yr1 = (int) (k * 3.72 * Math.cos(Math.toRadians(dir * 30 - 15)));

        int xr2 = (int) (k * 4.92 * Math.sin(Math.toRadians(dir * 30 - 43)));
        int yr2 = (int) (k * 4.92 * Math.cos(Math.toRadians(dir * 30 - 43)));

        int xr4 = (int) (k * 6.48 * Math.sin(Math.toRadians(dir * 30 - 172)));
        int yr4 = (int) (k * 6.48 * Math.cos(Math.toRadians(dir * 30 - 172)));


        BufferedImage image = new BufferedImage(wx, wy, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.BLACK);


        int[] pointsX = {x + xl1, x + xl2, x + xl3, x + xr2, x + xr1, x + xr4, x + xl4};
        int[] pointsY = {y - yl1, y - yl2, y - yl3, y - yr2, y - yr1, y - yr4, y - yl4};

        Polygon polygon = new Polygon(pointsX, pointsY, 7);
        g2d.fill(polygon);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
        //ImageIO.write(image, "PNG", new File("C:\\java\\wind\\src\\main\\java\\ua\\od\\wind\\ImageGenerators\\sources\\map_1x.png"));


    }
}
