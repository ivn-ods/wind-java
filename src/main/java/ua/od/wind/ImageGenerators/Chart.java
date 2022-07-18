package ua.od.wind.ImageGenerators;

import org.springframework.stereotype.Component;
import ua.od.wind.model.WindProcessed;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Chart {

    // Utility class to calculate maximum values in list of datasets
    public static HashMap<String, Double> getMaxValuesFromWindDataPoints(List<WindProcessed> windDataPoints) {
        // TO prevent division by zero if all points in dataset = 0
        double minMax = 0.01F;
        double midMax = 0.01F;
        double maxMax = 0.01F;
        for (WindProcessed windDataPoint : windDataPoints) {
            if (windDataPoint.getMin() > minMax) {
                minMax = windDataPoint.getMin();
            }
            if (windDataPoint.getMid() > midMax) {
                midMax = windDataPoint.getMid();
            }
            if (windDataPoint.getMax() > maxMax) {
                maxMax = windDataPoint.getMax();
            }
        }
        return new HashMap<>(Map.of("minMax", minMax, "midMax", midMax, "maxMax", maxMax));
    }


    public static void generate(List<WindProcessed> windDataPoints, String path) throws IOException {

        int h_set = 0;
        int mobile = 0;
        int W = 0;
        int text_size = 0;
        int dir_enabled = 1;

        //image size
        if (mobile == 1) {
            W = 400;
            text_size = 5;
        } else {
            W = 600;
            text_size = 4;
        }

        int H = 250;
        int max = 0;

        //Paddings
        int paddingBottom = 20;
        int paddingLeft = 25;
        int paddingTop = 22;
        int paddingRight = 7;

        int x_grid_prev = 0;
        int numOfYNumbers = 0;
        //Font size
        int fontHeight = 12;
        int fontWidth = (int) (fontHeight * 0.4);
        //number of points on chart
        int numOfPoint = windDataPoints.size();

        //Creating image
        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        //Set colors
        Color canvasColor = new Color(255, 255, 255);
        Color chartBackgroundColor = new Color(220, 220, 255);
        Color chartGridColor = new Color(170, 170, 170);
        Color textColor = new Color(0, 0, 0);
        Color[] chartsColors = new Color[3];
        chartsColors[0] = new Color(200, 30, 30);
        chartsColors[1] = new Color(30, 200, 30);
        chartsColors[2] = new Color(200, 30, 200);
        int text_width = 0;

        // increase left padding with Y axis test width
        paddingLeft += text_width;
        // Calculate the actual size of the chart (minus the labels and
        // padding)
        int realWidth = W - paddingLeft - paddingRight;
        int realHeight = H - paddingBottom - paddingTop;
        //Calculate coordinates of zero
        int X0 = paddingLeft;
        int Y0 = H - paddingBottom;


        //draw chart frame
        g2d.setColor(canvasColor);
        g2d.fillRect(0, 0, W, H);
        g2d.setColor(chartBackgroundColor);
        g2d.fillRect(X0, Y0 - realHeight, realWidth, realHeight);
        g2d.setColor(chartGridColor);
        g2d.drawRect(X0, Y0 - realWidth, realWidth, realHeight);


        if (numOfPoint == 0) {
            numOfPoint = 1;
        }

        // Add two points to the right and left of the charts. Values in
        // these points will be taken equal to the extreme ones.
        windDataPoints.add(0, windDataPoints.get(0));
        windDataPoints.add(0, windDataPoints.get(0));
        windDataPoints.add(windDataPoints.get(windDataPoints.size() - 1));
        windDataPoints.add(windDataPoints.get(windDataPoints.size() - 1));


        //  Charts smoothing
        for (int i = 1; i < numOfPoint - 1; i++) {
            windDataPoints.get(i).setMin((windDataPoints.get(i - 1).getMin() + windDataPoints.get(i).getMin() + windDataPoints.get(i + 1).getMin()) / 3);
            windDataPoints.get(i).setMid((windDataPoints.get(i - 1).getMid() + windDataPoints.get(i).getMid() + windDataPoints.get(i + 1).getMid()) / 3);
            windDataPoints.get(i).setMax((windDataPoints.get(i - 1).getMax() + windDataPoints.get(i).getMax() + windDataPoints.get(i + 1).getMax()) / 3);
            //if ( windDataPoints.max.get(i)> max)  max= windDataPoints.max.get(i);
        }
        HashMap<String, Double> maxValues = getMaxValuesFromWindDataPoints(windDataPoints);

        //Calculate max value
        max = (int) (maxValues.get("maxMax") * 1.2);
        // Number of labels and horizontal lines
        // grids along the Y axis.
        //  max = round( max, 0);
        if (max <= 17) numOfYNumbers = max;
        if (max > 17) numOfYNumbers = max / 2;
        if (max < 1) {
            numOfYNumbers = 1;
            max = 1;
        }

        int step = realHeight / numOfYNumbers;

        // Draw the grid along the Y axis
        for (int i = 1; i <= numOfYNumbers; i++) {
            int y = Y0 - step * i;
            g2d.setColor(chartGridColor);
            g2d.drawLine(X0, y, X0 + realWidth, y);
            g2d.setColor(textColor);
            g2d.drawLine(X0, y, X0 - (paddingLeft - text_width) / 4, y);
        }
        // Output labels on the Y axis
        for (int i = 1; i <= numOfYNumbers; i++) {
            int strl = String.valueOf((max / numOfYNumbers) * i).length() * fontWidth;
            if (strl > text_width) text_width = strl;
        }
        // Draw the grid along the X axis
        // Output of the resizable grid

        // Draw chart lines
        int dx = (realWidth / numOfPoint) / 2;
        int pi = (int) (Y0 - (realHeight / max * windDataPoints.get(0).getMin()));
        int po = (int) (Y0 - (realHeight / max * windDataPoints.get(0).getMid()));
        int pu = (int) (Y0 - (realHeight / max * windDataPoints.get(0).getMax()));
        int px = X0 + dx;
        int dirnext = 1;

        for (int i = 1; i < numOfPoint; i++) {
            int x = (int) (X0 + i * (realWidth / numOfPoint) + dx);
            int y = (int) (Y0 - (realHeight / max * windDataPoints.get(i).getMin()));
            g2d.setColor(chartsColors[0]);
            g2d.drawLine(px, pi, x, y);
            g2d.drawLine(px, pi + 1, x, y + 1);

            pi = y;
            y = (int) (Y0 - (realHeight / max * windDataPoints.get(i).getMid()));
            g2d.setColor(chartsColors[1]);
            g2d.drawLine(px, po, x, y);
            g2d.drawLine(px, po + 1, x, y + 1);
            g2d.drawLine(px, po + 2, x, y + 2);
            g2d.drawLine(px, po + 3, x, y + 3);
            po = y;
            y = (int) (Y0 - (realHeight / max * windDataPoints.get(i).getMax()));
            g2d.setColor(chartsColors[2]);
            g2d.drawLine(px, pu, x, y);
            g2d.drawLine(px, pu + 1, x, y + 1);

            // Draw of arrow in top side of chart
            if (dir_enabled == 1) {
                if (i == dirnext) {


                    int xdir = (int) (10 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30)));
                    int ydir = (int) (10 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30)));
                    g2d.setColor(textColor);
                    g2d.drawLine(px + xdir, 10 - ydir, px - xdir, 10 + ydir);
                    g2d.drawLine(px + xdir + 1, 10 - ydir + 1, px - xdir + 1, 10 + ydir + 1);
                    int x_narrow_l = (int) (6 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30 + 50)));
                    int y_narrow_l = (int) (6 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30 + 50)));
                    int x_narrow_r = (int) (6 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30 - 50)));
                    int y_narrow_r = (int) (6 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30 - 50)));
                    g2d.drawLine(px - x_narrow_l, 10 + y_narrow_l, px - xdir, 10 + ydir);
                    g2d.drawLine(px - x_narrow_l + 1, 10 + y_narrow_l + 1, px - xdir + 1, 10 + ydir + 1);
                    g2d.drawLine(px - x_narrow_r, 10 + y_narrow_r, px - xdir, 10 + ydir);
                    g2d.drawLine(px - x_narrow_r + 1, 10 + y_narrow_r + 1, px - xdir + 1, 10 + ydir + 1);


                    if (mobile == 1) dirnext = i + 3;
                    else dirnext = i + 2;

                }
            }

            int delta_h = windDataPoints.get(i).getHour() - windDataPoints.get(i - 1).getHour();
            int delta_d = windDataPoints.get(i).getDay() - windDataPoints.get(i - 1).getDay();
            if ((delta_h != 0) && (delta_d == 0) && (h_set >= 3)) {
                int delta_m = 60 - windDataPoints.get(i).getMinute() + windDataPoints.get(i - 1).getMinute();
                int x_grid = px + (int) (windDataPoints.get(i - 1).getMinute() * (x - px) / delta_m);
                int delta_grid = x_grid - x_grid_prev;
                g2d.setColor(textColor);
                g2d.drawLine(x_grid, Y0, x_grid, Y0 + 5);
                g2d.setColor(chartGridColor);
                g2d.drawLine(x_grid, Y0, x_grid, Y0 - realHeight);

                String str = String.valueOf(windDataPoints.get(i).getHour());
                str += ":00";
                if (delta_grid > 45) {
                    x_grid_prev = x_grid;
                    g2d.setColor(textColor);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, fontHeight));
                    g2d.drawString(str, x_grid - (str.length() * fontWidth) / 2 - 2, Y0 + (int) (fontHeight * 1.5));
                }
                h_set = 0;

            }
            h_set++;
            pu = y;
            px = x;

        }
        // Decreasing and recalculating coordinates
        paddingLeft -= text_width;

        // Output labels on the Y axis
        for (int i = 1; i <= numOfYNumbers; i++) {
            String str = String.valueOf(max / numOfYNumbers * i);
            g2d.setColor(textColor);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, fontHeight));
            g2d.drawString(str, X0 - str.length() * fontWidth - paddingLeft / 4 - 7, Y0 - step * i + (int) (fontHeight * 0.4));
        }


        ImageIO.write(image, "PNG", new File(path));


    }
}
