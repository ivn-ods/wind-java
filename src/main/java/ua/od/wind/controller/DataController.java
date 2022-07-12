package ua.od.wind.controller;

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
import ua.od.wind.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;



@PropertySource("classpath:application.properties")
@Controller
public class DataController {
    private final ServiceLayer serviceLayer;
    private final UserService userService;
    private final int dataLimit;
    private final String imgFolder;

    @Autowired
    public DataController(ServiceLayer serviceLayer, UserService userService, Environment env) {
        this.serviceLayer = serviceLayer;
        this.userService = userService;
        this.dataLimit = Integer.parseInt(Objects.requireNonNull(env.getProperty("data_limit")));
        this.imgFolder = Objects.requireNonNull(env.getProperty("img_generated_folder"));
    }

    @GetMapping("/about")
    public String about() {  return "about"; }

    @GetMapping("/contact")
    public String contact() {  return "contact"; }




    @GetMapping("/main")
    public String index(Model model) {
        //Data will be shown with offset for not active users and guests
        int offset = serviceLayer.getDataOffset();

        List<Sensor> enabledSensors = serviceLayer.getEnabledSensors();
        HashMap<Integer, List<WindProcessed>> windsMap = new HashMap<>();
        HashMap<Integer, Sensor> sensorsMap = new HashMap<>();
        for (Sensor sensor: enabledSensors) {
            //We need only one last datapoint to show in view
            windsMap.put(sensor.getId(), serviceLayer.getProcessedWindData(sensor, 1, offset ));
            sensorsMap.put(sensor.getId(), sensor);
        }
        model.addAttribute("offset", offset);
        model.addAttribute("winds", windsMap);
        model.addAttribute("sensors", sensorsMap);
        model.addAttribute("username", userService.getUserFromContext());
        return "main";
    }

    //test with this:
    //http://localhost/save?p0=2790&p1=UI000000000000000&p2=124148177815252292104000
    @GetMapping(value ="/save", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String saveWindData(@RequestParam(required = true) int p0, @RequestParam(required = true) String p1, @RequestParam(required = true) String p2) throws  IOException {
        return serviceLayer.storeWindDataAndGenerate4PNG(p0, p1, p2);
    }

    @GetMapping("/table/{id}")
    public String getTable(Model model, @PathVariable("id") int id) {
        Sensor sensor = serviceLayer.getSensorById(id);
        model.addAttribute("winds", serviceLayer.getProcessedWindData(sensor, dataLimit, serviceLayer.getDataOffset()));
        model.addAttribute("sensor", sensor);
        model.addAttribute("username", userService.getUserFromContext());
        return "table";
    }

    @GetMapping(value = "/arrowsmall/{dir}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getArrowSmall(@PathVariable("dir") float dir) throws IOException {
        return  ArrowSmall.makeImage(dir);

    }

    @GetMapping(value = "/arrow/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getArrow(@PathVariable("id") int id, HttpServletRequest request) throws IOException {
        //TODO: make offset for users who not payed
        String path ="";
        if (serviceLayer.getDataOffset() == 0) {
             path = imgFolder + "/arrows_on_maps/map_" + id +".png";
        }
        else {
             path = imgFolder + "/arrows_on_maps/map_offset_" + id +".png";
        }
        return  serviceLayer.getImagePNG(path);

    }

    @GetMapping(value = "/chart/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getChart(@PathVariable("id") int id) throws IOException {
        String path ="";
        if (serviceLayer.getDataOffset() == 0) {
            path = imgFolder + "/charts/chart_" + id +".png";
        }
        else {
            path = imgFolder + "/charts/chart_offset_" + id +".png";
        }
        return  serviceLayer.getImagePNG(path);

    }
}
