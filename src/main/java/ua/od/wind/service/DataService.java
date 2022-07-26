package ua.od.wind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ua.od.wind.ImageGenerators.Arrow;
import ua.od.wind.ImageGenerators.Chart;
import ua.od.wind.dao.WindDAO;
import ua.od.wind.dao.WindDAOimpl;
import ua.od.wind.model.Sensor;
import ua.od.wind.model.Wind;
import ua.od.wind.model.WindProcessed;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@PropertySource("classpath:application.properties")
@Service
public class DataService {
    private final WindDAO windDAOimpl;
    private final int dataLimit;
    private final int dataOffset;
    private final String imgFolder;
    private final UserService userService;
    //private final Environment env;

    @Autowired
    public DataService(WindDAO windDAOimpl,
                       Environment env,
                       UserService userService) {
        this.windDAOimpl = windDAOimpl;
        this.dataLimit = Integer.parseInt(Objects.requireNonNull(env.getProperty("data_limit")));
        this.dataOffset = Integer.parseInt(Objects.requireNonNull(env.getProperty("data_offset")));
        this.imgFolder = Objects.requireNonNull(env.getProperty("img_generated_folder"));
        this.userService = userService;
    }


    public String storeWindDataAndGenerate4PNG(int hash, String imei, String status) throws IOException {

        int min = Integer.parseInt(status.substring(0, 3));
        int mid = Integer.parseInt(status.substring(3, 6));
        int max = Integer.parseInt(status.substring(6, 9));
        int dir = Integer.parseInt(status.substring(9, 12));
        int v0 = Integer.parseInt(status.substring(12, 15));
        int v1 = Integer.parseInt(status.substring(15, 18));
        int temp = Integer.parseInt(status.substring(18, 21));
        int res = Integer.parseInt(status.substring(21, 24));
        int hashCalculated = 0;

        for (int i = 0; i < imei.length(); i++) {
            int symbol = imei.getBytes(StandardCharsets.UTF_8)[i];
            hashCalculated += symbol;
        }
        hashCalculated += min + mid + max + dir + v0 + v1 + temp + res;

        Sensor sensor = windDAOimpl.getSensorByImei(imei);
        if (hashCalculated == hash) {
            if (sensor != null) {
                Wind wind = new Wind();
                wind.setTimestamp((int) ZonedDateTime.now().toEpochSecond());
                wind.setMax(max);
                wind.setMid(mid);
                wind.setMin(min);
                wind.setTemp(temp);
                wind.setDir(dir);
                wind.setV0(v0);
                wind.setV1(v1);
                wind.setSensorId(sensor.getId());
                windDAOimpl.saveWind(wind);
                // remove old data not to flood database
                windDAOimpl.removeWind(sensor.getId());

                this.generateCharts(sensor);
                this.generateArrows(sensor);

                return "inserted ok";
            } else {
                return "sensor null";
            }
        } else {
            return "not inserted, wrong hash";
        }


    }

    public void generateCharts(Sensor sensor) throws IOException {

        List<WindProcessed> windsProcessed = this.getProcessedWindData(sensor, dataLimit, 0);
        String path = imgFolder + "/charts/chart_" + sensor.getId() + ".png";
        Chart.generate(windsProcessed, path);

        windsProcessed = this.getProcessedWindData(sensor, dataLimit, dataOffset);
        path = imgFolder + "/charts/chart_offset_" + sensor.getId() + ".png";
        Chart.generate(windsProcessed, path);

    }

    public void generateArrows(Sensor sensor) throws IOException {

        List<WindProcessed> windsProcessed = this.getProcessedWindData(sensor, 1, 0);
        String path = imgFolder + "/arrows_on_maps/map_" + sensor.getId() + ".png";
        Arrow.generate(windsProcessed, path, imgFolder);

        windsProcessed = this.getProcessedWindData(sensor, 1, dataOffset);
        path = imgFolder + "/arrows_on_maps/map_offset_" + sensor.getId() + ".png";
        Arrow.generate(windsProcessed, path, imgFolder);

    }

    public String getDate(long timestamp) {
        LocalDateTime ldt = this.getLDT(timestamp);
        return ldt.getDayOfMonth() + "-" +
                ldt.getMonthValue() + "-" +
                ldt.getYear();
    }

    private LocalDateTime getLDT(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Paris"));
    }

    public Sensor getSensorById(int id) {
        return windDAOimpl.getSensorById(id);
    }

    public List<Sensor> getEnabledSensors() {
        return windDAOimpl.getEnabledSensors();
    }

    public byte[] getImagePNG(String path) throws IOException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();

    }


    public List<WindProcessed> getProcessedWindData(Sensor sensor, int dataLimit, int dataOffset) {

        List<Wind> windRaw = windDAOimpl.getRawWindData(sensor.getId(), dataLimit, dataOffset);

        ArrayList<WindProcessed> windsProcessed = new ArrayList<>();
        for (Wind wind : windRaw) {
            WindProcessed windProcessed = new WindProcessed();
            windProcessed.setId(wind.getId());
            windProcessed.setSensorId(wind.getSensorId());
            windProcessed.setTemp(wind.getTemp() - 100);
            windProcessed.setMin(((float) (wind.getMin() / sensor.getSpeedFactor())) / 10);
            windProcessed.setMid(((float) (wind.getMid() / sensor.getSpeedFactor())) / 10);
            windProcessed.setMax(((float) (wind.getMax() / sensor.getSpeedFactor())) / 10);
            windProcessed.setDay(this.getLDT(wind.getTimestamp()).getDayOfMonth());

            int hour = this.getLDT(wind.getTimestamp()).getHour();
            windProcessed.setHour(hour);
            windProcessed.setHourStr(String.format("%02d", hour));

            int minute = this.getLDT(wind.getTimestamp()).getMinute();
            windProcessed.setMinute(minute);
            windProcessed.setMinuteStr(String.format("%02d", minute));

            windProcessed.setDate(this.getDate(wind.getTimestamp()));
            float dirProcesed;
            int dir = wind.getDir();
            if (dir > 500) {
                dirProcesed = 16 - (int) ((999 - dir) / 4.5) / 10;
            } else {
                dirProcesed = (int) (dir / 4.5) / 10;
            }
            String monthsStr = this.getLDT(wind.getTimestamp()).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            windProcessed.setMonthStr(monthsStr);

            windProcessed.setDir((float) ((dirProcesed + sensor.getDirFactor()) * 0.75));
            windProcessed.setV0(wind.getV0() / 10);
            windProcessed.setV1(wind.getV1() / 10);
            windsProcessed.add(windProcessed);
        }
        return windsProcessed;

    }


    public int getDataOffset() {
        if (userService.isUserPayed()) return 0;
        else return this.dataOffset;
    }

    //If sensor not send data for a long time we not show data from them
    public boolean isSensorAlive(Sensor sensor, int dataLimit, int dataOffset) {
        List<Wind> windRaw = windDAOimpl.getRawWindData(sensor.getId(), dataLimit, dataOffset);
        long timeDelay =  ZonedDateTime.now().toEpochSecond() - windRaw.get(0).getTimestamp();
        return timeDelay < 3600 * 3;
    }
}