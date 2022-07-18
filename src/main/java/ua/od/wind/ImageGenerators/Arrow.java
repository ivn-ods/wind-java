package ua.od.wind.ImageGenerators;

import org.springframework.stereotype.Component;
import ua.od.wind.model.WindProcessed;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Arrow {

    public static void generate(List<WindProcessed> windsProcessed, String path, String imgFolder) throws IOException {

        File file = new File(imgFolder + "/sources/map_" + windsProcessed.get(0).getSensorId() + ".png");
        BufferedImage image = ImageIO.read(file);
        Graphics2D g2d = image.createGraphics();

        Color lightOrange = new Color(223, 152, 60);
        Color darkOrange = new Color(147, 70, 1);
        int wx = 130;
        int wy = 130;
        int x = wx / 2;
        int y = wy / 2;
        float dir = windsProcessed.get(0).getDir();
        dir += 6;

        //Left part

        int xl1 = (int) (0.4 * x * Math.sin(Math.toRadians(dir * 30 + 39)));
        int yl1 = (int) (0.4 * y * Math.cos(Math.toRadians(dir * 30 + 39)));

        int xl2 = (int) (0.28 * x * Math.sin(Math.toRadians(dir * 30 + 18)));
        int yl2 = (int) (0.28 * y * Math.cos(Math.toRadians(dir * 30 + 18)));

        int xl3 = (int) (0.91 * x * Math.sin(Math.toRadians(dir * 30 + 8)));
        int yl3 = (int) (0.91 * y * Math.cos(Math.toRadians(dir * 30 + 8)));

        //Right part
        int xr1 = (int) (0.4 * x * Math.sin(Math.toRadians(dir * 30 - 39)));
        int yr1 = (int) (0.4 * y * Math.cos(Math.toRadians(dir * 30 - 39)));

        int xr2 = (int) (0.28 * x * Math.sin(Math.toRadians(dir * 30 - 18)));
        int yr2 = (int) (0.28 * y * Math.cos(Math.toRadians(dir * 30 - 18)));

        int xr3 = (int) (0.91 * x * Math.sin(Math.toRadians(dir * 30 - 8)));
        int yr3 = (int) (0.91 * y * Math.cos(Math.toRadians(dir * 30 - 8)));


        int x1 = (int) (0.2 * x * Math.sin(Math.toRadians(dir * 30)));
        int y1 = (int) (0.2 * y * Math.cos(Math.toRadians(dir * 30)));


        g2d.setColor(lightOrange);
        g2d.drawLine(x, y, x - xr1, y - yr1);

        int[] pointsX = {x, x - xl1, x - xl2, x - xl3, x - xr3, x - xr2, x - xr1};
        int[] pointsY = {y, y - yl1, y - yl2, y - yl3, y - yr3, y - yr2, y - yr1};

        Polygon polygon = new Polygon(pointsX, pointsY, 7);
        g2d.fill(polygon);

        g2d.setColor(darkOrange);
        g2d.drawLine(x, y, x - xr1, y - yr1);
        g2d.drawLine(x + 1, y, x - xr1 + 1, y - yr1);
        g2d.drawLine(x - xr2, y - yr2, x - xr3, y - yr3);
        g2d.drawLine(x - xr2 + 1, y - yr2 + 1, x - xr3, y - yr3);


        ImageIO.write(image, "PNG", new File(path));
    }


}
