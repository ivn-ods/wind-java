package ua.od.wind.service;

import com.liqpay.LiqPay;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


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
