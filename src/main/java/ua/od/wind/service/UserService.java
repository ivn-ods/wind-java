package ua.od.wind.service;

import com.liqpay.LiqPay;
import com.liqpay.LiqPayUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.od.wind.dao.UserDAO;
import ua.od.wind.dao.UserDAOimpl;
import ua.od.wind.model.User;
import ua.od.wind.model.UserStatus;
import ua.od.wind.security.SecurityUser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    private final UserDAO userDAOimpl;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LiqPay liqpay;
    private final Environment env;
    private final JSONParser jsonParser = new JSONParser();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserDAO userDAOimpl, BCryptPasswordEncoder passwordEncoder, Environment env) {
        this.env = env;
        this.liqpay = new LiqPay(this.env.getRequiredProperty("public_key"), this.env.getRequiredProperty("private_key"));
        this.userDAOimpl = userDAOimpl;
        this.passwordEncoder = passwordEncoder;
    }

    public void doLogin() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            User user = userDAOimpl.getUserListByUsername(authentication.getName()).stream().findFirst().orElseThrow(() ->
                    new UsernameNotFoundException("User doesn't exists"));
            UserDetails userDetails = SecurityUser.fromUser(user);
            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), null, userDetails.getAuthorities());
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authToken);
        }


    }


    public String saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        user.setRegisterDate(date);
        String paymentId = UUID.randomUUID().toString();
        user.setPaymentIdBase(paymentId);
        user.setUserStatus(UserStatus.NOTPAYED);
        userDAOimpl.saveUser(user);
        return paymentId;

    }

    public void saveUser(User user) {
        userDAOimpl.saveUser(user);
    }

    public String getPaymentIdForThisYear(String PaymentIdBase) {
        return PaymentIdBase + "-" + new SimpleDateFormat("yyyy").format(new Date());
    }


    public String getPaymentButton(User user) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "pay");
        params.put("amount", env.getRequiredProperty("payment_amount"));
        params.put("currency", "UAH");
        params.put("description", "Subscribe for " + user.getUsername() +
                " for year " +
                new SimpleDateFormat("yyyy").format(new Date()) +
                "id=" +
                this.getPaymentIdForThisYear(user.getPaymentIdBase()));
        params.put("order_id", this.getPaymentIdForThisYear(user.getPaymentIdBase()));
        params.put("version", "3");
        params.put("sandbox", env.getRequiredProperty("sandbox"));

        return liqpay.cnb_form(params);
    }

    /**
     * Processing POST callback request from LiqPay and update user payDate and other data
     * see official LiqPay SDK
     */
    public String processCallback(String data, String signature) throws ParseException, java.text.ParseException {
        logger.warn("Data:" + data);
        logger.warn("Signature:" + signature);
        String result;
        String keyDatakey = env.getRequiredProperty("private_key") + data + env.getRequiredProperty("private_key");
        String signatureCalculated = LiqPayUtil.base64_encode(LiqPayUtil.sha1(keyDatakey));
        String dataInJSON = new String(Base64.getDecoder().decode(data));
        JSONObject dataInJSONObj = (JSONObject) jsonParser.parse(dataInJSON);
        HashMap<String, Object> dataInMap = LiqPayUtil.parseJson(dataInJSONObj);
        User user = userDAOimpl.findByPaymentId((String) dataInMap.get("order_id")).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        if (!signature.equals(signatureCalculated)) {
            result = " Signatures not equal";
            logger.warn((String) dataInMap.get("order_id") + result);

        }
        if (!env.getRequiredProperty("public_key").equals((String) dataInMap.get("public_key"))) {
            result = " Keys not equal";
            logger.warn((String) dataInMap.get("order_id") + result);

        }
        if (!((String) dataInMap.get("status")).equals("success")
                && !((String) dataInMap.get("status")).equals("sandbox")) {
            result = " Status not success or sandbox";
            logger.warn((String) dataInMap.get("order_id") + result);

        }
        user.setPayDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        user.setPhone((String) dataInMap.get("sender_phone"));
        user.setTransactionId(String.valueOf(dataInMap.get("transaction_id")));
        user.setUserStatus(UserStatus.PAYED);
        userDAOimpl.saveUser(user);
        result = " Payment confirmed";
        logger.warn((String) dataInMap.get("order_id") + result);
        return result;
    }

    public String getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        if (name.equals("anonymousUser")) name = "Guest";
        return name;
    }


    public Optional<User> getOptionalUserByUsername(String username) {
        return userDAOimpl.getUserListByUsername(username).stream().findFirst();

    }

    // Check every login if account not expired .
    // Ovaerwise set user NOTPAYED
    public void checkUserStatus(Authentication authentication) {

        User user = userDAOimpl.getUserByUsername(authentication.getName()).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));

        if (user.getPayDate() != null && !user.getPayDate().equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate payDate = LocalDate.parse(user.getPayDate(), formatter);
            LocalDate expiryDate = payDate.plusYears(1);
            boolean expire = !expiryDate.isAfter(LocalDate.now());

            //  Update user status in DB if today it was expired
            if (expire && user.getUserStatus() == UserStatus.PAYED) {
                user.setUserStatus(UserStatus.NOTPAYED);
                userDAOimpl.mergeUser(user);
            }
        }
    }

    //registered but not payed
    public boolean isUserLoggedInButNotPayed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                !this.isGuest() &&  //not Guest
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("NOTPAYED"));
    }

    public boolean isUserPayed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null &&
                !this.isGuest() &&  //not Guest
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("PAYED"));

    }

    public boolean isGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ||
                (authentication instanceof AnonymousAuthenticationToken);

    }

}
