package ua.od.wind.service;

import com.liqpay.LiqPay;
import com.liqpay.LiqPayUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.od.wind.dao.UserDAO;
import ua.od.wind.model.User;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class LiqPayPayment {

    private final LiqPay liqpay;
    private final JSONParser jsonParser = new JSONParser();
    private final Environment env;


    @Autowired
    public LiqPayPayment(Environment env) {
        this.liqpay = new LiqPay(env.getRequiredProperty("public_key"), env.getRequiredProperty("private_key"));
        this.env = env;
    }




}
