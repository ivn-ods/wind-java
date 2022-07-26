package ua.od.wind.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.env.MockEnvironment;
import ua.od.wind.dao.WindDAOimpl;
import ua.od.wind.model.Sensor;
import ua.od.wind.model.Wind;
import ua.od.wind.model.WindProcessed;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class DataServiceTest {
    @Mock
    WindDAOimpl windDAOimpl;
    @Mock
    UserService userService;

    DataService dataService;

    @Before
    public  void setUp() {

         dataService = new DataService(
                windDAOimpl,
                new MockEnvironment()
                        .withProperty("data_limit", "1")
                        .withProperty("data_offset", "1")
                        .withProperty("img_generated_folder", ""),
                userService);
    }

    @Test
    public void getProcessedWindDataTest() {
        Wind wind = new Wind();
        wind.setTimestamp(2524611661L);
        wind.setMax(900);
        wind.setMid(500);
        wind.setMin(0);
        wind.setTemp(150);
        wind.setDir(45);
        wind.setV0(10);
        wind.setV1(20);
        wind.setSensorId(1);

        Mockito.when(windDAOimpl.getRawWindData(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn( Arrays.asList(wind) );

        Sensor sensor = new Sensor();
        sensor.setId(1);
        sensor.setDirFactor(0);
        sensor.setSpeedFactor(1);


        List<WindProcessed> windProcessed = dataService.getProcessedWindData(sensor,1,1);
        assertNotEquals(null, windProcessed);
        assertEquals(90, windProcessed.get(0).getMax(), 0.000001);
        assertEquals(50, windProcessed.get(0).getMid(), 0.000001);
        assertEquals(0, windProcessed.get(0).getMin(), 0.000001);
        assertEquals(50, windProcessed.get(0).getTemp());
        assertEquals(0.75, windProcessed.get(0).getDir(), 0.000001);
        assertEquals(1, windProcessed.get(0).getV0(), 0.000001);
        assertEquals(2, windProcessed.get(0).getV1(), 0.000001);
        assertEquals("02", windProcessed.get(0).getHourStr());
        assertEquals("01", windProcessed.get(0).getMinuteStr());
        assertEquals("January", windProcessed.get(0).getMonthStr());

    }
}