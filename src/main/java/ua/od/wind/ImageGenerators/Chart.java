package ua.od.wind.ImageGenerators;

import org.springframework.stereotype.Component;
import ua.od.wind.models.WindProcessed;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

@Component
public class Chart {

    public static HashMap<String, Double> getMaxValuesFromWindDataPoints(List<WindProcessed> windDataPoints) {

        double minMax=0;
        double midMax=0;
        double maxMax=0;
        for ( WindProcessed windDataPoint :  windDataPoints) {
            if ( windDataPoint.getMin() >  minMax) {
                minMax =  windDataPoint.getMin();
            }
            if ( windDataPoint.getMid() >  midMax) {
                midMax =  windDataPoint.getMid();
            }
            if ( windDataPoint.getMax() >  maxMax) {
                maxMax =  windDataPoint.getMax();
            }
        }
        return new HashMap<String, Double>(Map.of("minMax", minMax, "midMax", midMax, "maxMax", maxMax)) ;
    }



    public static void generate(List<WindProcessed> windDataPoints, String path) throws IOException {

        int h_set = 0;
        int mobile = 0;
        int W = 0;
        int text_size = 0;
        int dir_enabled = 1;
// Задаем изменяемые значения #######################################
// Размер изображения
        if ( mobile == 1) {
            W = 400;
            text_size = 5;
        } else {
            W = 600;
            text_size = 4;
        }

        int H = 250;
        int max = 0;
// Отступы
        int paddingBottom = 20; // Нижний
        int paddingLeft = 17; // Левый
        int paddingTop = 22; // Верхний
        int paddingRight = 7; // Правый
        int  x_grid_prev = 0;
        int numOfYNumbers = 0;
// Они меньше, так как там нет текста
// Ширина одного символа
        int fontHeight = 12;
        int fontWidth = (int)(fontHeight*0.4);
// Подсчитаем количество элементов (точек) на графике
        int numOfPoint = windDataPoints.size();

        // Работа с изображением ############################################
// Создадим изображение
        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

// Цвет фона (белый)
        Color bg0 = new Color(255, 255, 255);
// Цвет задней грани графика (светло-серый)
        Color bg1 = new Color(220, 220, 255);
// Цвет левой грани графика (серый)
        Color bg2 = new Color(212, 212, 212);
// Цвет сетки (серый, темнее)
        Color c = new Color(170, 170, 170);
// Цвет текста (темно-серый)
        Color text = new Color( 0, 0, 0);
// Цвета для линий графиков
        Color[] bar = new Color[3];
        bar[0] = new Color(200, 30, 30);
        bar[1] = new Color(30, 200, 30);
        bar[2] = new Color( 200, 30, 200);
        int text_width = 0;

// Подравняем левую границу с учетом ширины подписей по оси Y
        paddingLeft +=  text_width;
// Посчитаем реальные размеры графика (за вычетом подписей и
// отступов)
        int realWidth =  W -  paddingLeft -  paddingRight;
        int realHeight =  H -  paddingBottom -  paddingTop;
// Посчитаем координаты нуля
        int X0 =  paddingLeft;
        int Y0 =  H -  paddingBottom;


// Вывод главной рамки графика
        g2d.setColor(bg0);
        g2d.fillRect(0,  0, W, H);
        g2d.setColor(bg1);
        g2d.fillRect(X0,  Y0-realHeight, realWidth, realHeight);
        g2d.setColor(c);
        g2d.drawRect(X0,  Y0 - realWidth, realWidth, realHeight);


        if ( numOfPoint == 0)  {
            numOfPoint = 1;
        } else {


            // Добавим по две точки справа и слева от графиков. Значения в
            // этих точках примем равными крайним.
            windDataPoints.add(0,windDataPoints.get(0));
            windDataPoints.add(0,windDataPoints.get(0));
            windDataPoints.add(windDataPoints.get(windDataPoints.size()-1));
            windDataPoints.add(windDataPoints.get(windDataPoints.size()-1));


            // Сглаживание графики методом усреднения соседних значений
            //  for (int i = 2;  i <  count;  i++) {
            //   windDataPoints.get(i).min = ( windDataPoints.get(i-1).min +  windDataPoints.get(i).min +  windDataPoints.get(i+1).min) / 3;
            //   windDataPoints.get(i).mid = ( windDataPoints.get(i-1).mid +  windDataPoints.get(i).mid +  windDataPoints.get(i+1).mid) / 3;
            //   windDataPoints.get(i).max = ( windDataPoints.get(i-1).max +  windDataPoints.get(i).max +  windDataPoints.get(i+1).max) / 3;
            //if ( windDataPoints.max.get(i)> max)  max= windDataPoints.max.get(i);
            //    }
            HashMap<String, Double> maxValues = getMaxValuesFromWindDataPoints( windDataPoints);
            if ( maxValues.get("minMax") == 0)  windDataPoints.get(0).setMax(0.01f);
            if ( maxValues.get("midMax") == 0)  windDataPoints.get(0).setMid(0.01f);
            if ( maxValues.get("maxMax") == 0)  windDataPoints.get(0).setMin(0.01f);

// Подсчитаем максимальное значение
            max = (int)( maxValues.get("maxMax") * 1.2);
// Количество подписей и горизонтальных линий
// сетки по оси Y.
            //  max = round( max, 0);
            if ( max <= 17)  numOfYNumbers =  max;
            if ( max > 17)  numOfYNumbers =  max / 2;
            if ( max < 1) {
                numOfYNumbers = 1;
                max = 1;
            }

            int step =  realHeight /  numOfYNumbers;

// Вывод сетки по оси Y
            for (int i = 1;  i <=  numOfYNumbers;  i++) {
                int y =  Y0 -  step *  i;
                g2d.setColor(c);
                g2d.drawLine(X0,  y,  X0 +  realWidth,  y);
                g2d.setColor(text);
                g2d.drawLine(  X0,  y,  X0 - ( paddingLeft -  text_width) / 4,  y);
            }
            // Вывод подписей по оси Y
            for (int i = 1;  i <=  numOfYNumbers;  i++) {
                int strl = String.valueOf(( max /  numOfYNumbers) *  i).length() *  fontWidth;
                if ( strl >  text_width)  text_width =  strl;
            }
// Вывод сетки по оси X
// Вывод изменяемой сетки

// Вывод линий графика
            int dx = ( realWidth /  numOfPoint) / 2;
            int pi =  (int)(Y0 - ( realHeight /  max *  windDataPoints.get(0).getMin()));
            int po =  (int)(Y0 - ( realHeight /  max *  windDataPoints.get(0).getMid()));
            int pu =  (int)(Y0 - ( realHeight /  max *  windDataPoints.get(0).getMax()));
            int px =  X0 +  dx;
            int dirnext = 1;

            for (int i = 1;  i <  numOfPoint;  i++) {
                int x = (int)( X0 +  i * ( realWidth /  numOfPoint) +  dx);
                int y = (int)(Y0 - ( realHeight /  max *  windDataPoints.get(i).getMin()));
                g2d.setColor(bar[0]);
                g2d.drawLine( px,  pi,  x,  y);
                g2d.drawLine( px,  pi + 1,  x,  y + 1);

                pi =  y;
                y =  (int)(Y0 - ( realHeight /  max *  windDataPoints.get(i).getMid()));
                g2d.setColor(bar[1]);
                g2d.drawLine(px,  po,  x,  y);
                g2d.drawLine(px,  po + 1,  x,  y + 1);
                g2d.drawLine(px,  po + 2,  x,  y + 2);
                g2d.drawLine(px,  po + 3,  x,  y + 3);
                po =  y;
                y =  (int)(Y0 - ( realHeight /  max *  windDataPoints.get(i).getMax()));
                g2d.setColor(bar[2]);
                g2d.drawLine(px,  pu,  x,  y);
                g2d.drawLine(px,  pu + 1,  x,  y + 1);
//imageline( im, px, pu+2, x, y+2, bar[2]);

                if ( dir_enabled == 1) {
                    if ( i ==  dirnext) {


                        int xdir = (int) (10 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30)));
                        int ydir = (int) (10 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30)));
                        g2d.setColor(text);
                        g2d.drawLine(px +  xdir, 10 -  ydir,  px -  xdir, 10 +  ydir);
                        g2d.drawLine(px +  xdir + 1, 10 -  ydir + 1,  px -  xdir + 1, 10 +  ydir + 1);
                        int x_narrow_l = (int) (6 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30+50)));
                        int y_narrow_l = (int) (6 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30+50)));
                        int x_narrow_r = (int) (6 * Math.sin(Math.toRadians(windDataPoints.get(i).getDir() * 30-50)));
                        int y_narrow_r = (int) (6 * Math.cos(Math.toRadians(windDataPoints.get(i).getDir() * 30-50)));
                        g2d.drawLine( px -  x_narrow_l, 10 +  y_narrow_l,  px -  xdir, 10 +  ydir);
                        g2d.drawLine(px -  x_narrow_l + 1, 10 +  y_narrow_l + 1,  px -  xdir + 1, 10 +  ydir + 1);
                        g2d.drawLine(px -  x_narrow_r, 10 +  y_narrow_r,  px -  xdir, 10 +  ydir);
                        g2d.drawLine(px -  x_narrow_r + 1, 10 +  y_narrow_r + 1,  px -  xdir + 1, 10 +  ydir + 1);


                        if ( mobile == 1)  dirnext =  i + 3; else  dirnext =  i + 2;

                    }
                }

                int delta_h =  windDataPoints.get(i).getHour() -  windDataPoints.get(i-1).getHour();
                int delta_d =  windDataPoints.get(i).getDay() -  windDataPoints.get(i-1).getDay() ;
                if (( delta_h != 0) && ( delta_d == 0) && ( h_set >= 3)) {//imageline( im, px,0, px, H, bar[1]); imageline( im, x,0, x, H, bar[1]);
                    int delta_m = 60 -  windDataPoints.get(i).getMinute() +  windDataPoints.get(i-1).getMinute();
                    int x_grid =  px + (int)(windDataPoints.get(i-1).getMinute() * ( x -  px) /  delta_m);
                    int delta_grid =  x_grid -  x_grid_prev;
                    //imagestring( im,4,  x_grid,  Y0-70, delta_grid, text);
                    g2d.setColor(text);
                    g2d.drawLine(x_grid,  Y0,  x_grid,  Y0 + 5);
                    g2d.setColor(c);
                    g2d.drawLine(x_grid,  Y0,  x_grid,  Y0 -  realHeight);

                    String str =  String.valueOf(windDataPoints.get(i).getHour());
                    str += ":00";
                    if ( delta_grid > 45) {
                        x_grid_prev =  x_grid;
                        g2d.setColor(text);
                        g2d.setFont(new Font("Monospaced", Font.PLAIN, fontHeight));
                        g2d.drawString(str, x_grid - (str.length() *  fontWidth) / 2 - 2,  Y0 + (int)(fontHeight*1.5));
                    }
                    h_set = 0;

                }
                h_set++;
                pu =  y;
                px =  x;

            }
// Уменьшение и пересчет координат
            paddingLeft -=  text_width;

// Вывод подписей по оси Y
            for (int i = 1;  i <=  numOfYNumbers;  i++) {
                String str = String.valueOf( max /  numOfYNumbers *  i);
                g2d.setColor(text);
                g2d.setFont(new Font("Monospaced", Font.PLAIN, fontHeight));
                g2d.drawString(str, X0 - str.length() *  fontWidth -  paddingLeft / 4 - 7,  Y0 -  step *  i + (int)(fontHeight*0.4));
            }

        }

        ImageIO.write(image, "PNG", new File(path));

        Path currentRelativePath = Paths.get("");
        System.out.println(currentRelativePath);
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println(s);

    }
}
