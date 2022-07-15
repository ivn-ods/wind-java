package ua.od.wind.service;

import com.liqpay.LiqPay;
import com.liqpay.LiqPayUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.od.wind.dao.UserDAO;
import ua.od.wind.model.User;
import ua.od.wind.model.UserStatus;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    //private final UserRepository userRepository;
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LiqPay liqpay;
    private final Environment env;
    private final JSONParser jsonParser = new JSONParser();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
   // private  final Logger logger = LogManager.getLogger(UserService.class.getName());


    public UserService(UserDAO userDAO, BCryptPasswordEncoder passwordEncoder, Environment env) {
        this.env = env;
        this.liqpay = new LiqPay(this.env.getRequiredProperty("public_key"), this.env.getRequiredProperty("private_key"));
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }




    public String saveNewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String date =new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        user.setRegisterDate(date);
        String paymentId = UUID.randomUUID().toString();
        user.setPaymentIdBase(paymentId);
        user.setUserStatus(UserStatus.NOTPAYED);
        userDAO.saveUser(user);
        return paymentId;

    }

    public void saveUser(User user){
        userDAO.saveUser(user);
    }

    public String getPaymentIdForThisYear(String  PaymentIdBase) {
        return PaymentIdBase + "-" + new SimpleDateFormat("yyyy").format(new Date());
    }


    public String getPaymentButton(User user) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "pay");
        params.put("amount", env.getRequiredProperty("payment_amount"));
        params.put("currency", "UAH");
        params.put("description", "Subscribe for "+ user.getUsername() +
                " for year "+
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
     * @param data
     * @param signature
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public void processCallback(String data, String signature) throws ParseException, java.text.ParseException {
        logger.warn("Data:" + data);
        logger.warn("Signature:" + signature);

        String keyDatakey = env.getRequiredProperty("private_key") + data + env.getRequiredProperty("private_key");
        String signatureCalculated = LiqPayUtil.base64_encode(LiqPayUtil.sha1(keyDatakey));
        String dataInJSON = new String(Base64.getDecoder().decode(data));
        JSONObject dataInJSONObj = (JSONObject)jsonParser.parse(dataInJSON);
        HashMap<String, Object> dataInMap = LiqPayUtil.parseJson(dataInJSONObj);
        User user = userDAO.findByPaymentId((String)dataInMap.get("order_id")).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
        if (!signature.equals(signatureCalculated)) {
            logger.warn((String)dataInMap.get("order_id") + " Signatures not equal");

        }
        if (!env.getRequiredProperty("public_key").equals((String)dataInMap.get("public_key"))) {
            logger.warn((String)dataInMap.get("order_id") + " Keys not equal");

        }
        if ( !((String)dataInMap.get("status")).equals("success")
                && !((String)dataInMap.get("status")).equals("sandbox")) {
            logger.warn((String)dataInMap.get("order_id") + " Status not success or sandbox");

        }
        user.setPayDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        user.setPhone( (String)dataInMap.get("sender_phone") );
        user.setTransactionId( String.valueOf(dataInMap.get("transaction_id")) );
        user.setUserStatus(UserStatus.PAYED);
        userDAO.saveUser(user);
        logger.warn((String)dataInMap.get("order_id") + " payment confirmed");

    }

    public String getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        if (name.equals("anonymousUser")) name = "Guest";
        return name;
    }




    public Optional<User> getOptionalUserByUsername(String username) {
        return userDAO.getUserListByUsername(username).stream().findFirst();

    }

    // Check if account not expired every login.
    // Ovaerwise set user NOTPAYED
    public void checkUserStatus(Authentication authentication) {



        User user = userDAO.getUserByUsername(authentication.getName()).orElseThrow(() ->
                new UsernameNotFoundException("User doesn't exists"));
//        User user = userDAO.findByPaymentId((String)dataInMap.get("order_id")).orElseThrow(() ->
//                new UsernameNotFoundException("User doesn't exists"));


        if (user.getPayDate() != null && !user.getPayDate().equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate payDate = LocalDate.parse(user.getPayDate(), formatter);
            LocalDate expiryDate = payDate.plusYears(1);
            boolean expire =  !expiryDate.isAfter(LocalDate.now());

           //  Update user status in DB if today it was expired
            if (expire && user.getUserStatus() == UserStatus.PAYED) {
                user.setUserStatus(UserStatus.NOTPAYED);
                userDAO.mergeUser(user);
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
