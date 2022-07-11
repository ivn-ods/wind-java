package ua.od.wind.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.od.wind.ImageGenerators.ArrowSmall;
import ua.od.wind.model.Sensor;
import ua.od.wind.model.WindProcessed;
import ua.od.wind.service.ServiceLayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;



@PropertySource("classpath:application.properties")
@Controller
public class TableController {
    private final ServiceLayer serviceLayer;
    private final int dataLimit;
    private final int dataOffset;

    @Autowired
    public TableController(
            ServiceLayer serviceLayer , Environment env
    ) {
        this.serviceLayer = serviceLayer;
        this.dataLimit = Integer.parseInt(Objects.requireNonNull(env.getProperty("data_limit")));
        this.dataOffset = Integer.parseInt(Objects.requireNonNull(env.getProperty("data_offset")));
    }


    @GetMapping("/temp")
    public String index(Model model) {
        List<Sensor> enabledSensors = serviceLayer.getEnabledSensors();
        HashMap<Integer, List<WindProcessed>> windsMap = new HashMap<>();
        HashMap<Integer, Sensor> sensorsMap = new HashMap<>();
        for (Sensor sensor: enabledSensors) {
            windsMap.put(sensor.getId(), serviceLayer.getProcessedWindData(sensor, dataLimit, 0 ));
            sensorsMap.put(sensor.getId(), sensor);
        }
        model.addAttribute("winds", windsMap);
        model.addAttribute("sensors", sensorsMap);

        return "main";
    }

    //http://localhost/save?p0=2790&p1=UI000000000000000&p2=124148177815252292104000
    @GetMapping(value ="/save", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String saveWindData(@RequestParam(required = true) int p0, @RequestParam(required = true) String p1, @RequestParam(required = true) String p2) throws IOException {
        return serviceLayer.storeWindDataAndGenerate4PNG(p0, p1, p2);
    }

    @GetMapping("/table/{id}")
    public String getTable(Model model, @PathVariable("id") int id) {
        Sensor sensor = serviceLayer.getSensorById(id);
        model.addAttribute("winds", serviceLayer.getProcessedWindData(sensor, dataLimit, 0));
        model.addAttribute("sensor", sensor);
        return "table";
    }

    @GetMapping(value = "/arrowsmall/{dir}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getArrowSmall(@PathVariable("dir") float dir) throws IOException {
        return  ArrowSmall.makeImage(dir);

    }

    @GetMapping(value = "/arrow/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getArrow(@PathVariable("id") int id) throws IOException {
        String path = "C:\\java\\wind\\src\\main\\java\\ua\\od\\wind\\ImageGenerators\\arrows_on_maps\\map_" + id +".png";

        return  serviceLayer.getImagePNG(path);

    }

    @GetMapping("/chart/{id}")
    public @ResponseBody
    byte[] getChart(@PathVariable("id") int id) throws IOException {
        String path = "C:\\java\\wind\\src\\main\\java\\ua\\od\\wind\\ImageGenerators\\charts\\chart_" + id +".png";

        return  serviceLayer.getImagePNG(path);

    }
}
