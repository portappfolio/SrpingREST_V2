package com.portappfolio.app.appUser.phone.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:application.yml")
public class SmsService {

    private static String sid;

    private static String token;

    private String fromPhone;

    @Autowired
    public SmsService(
            @Value("${twilio.sid}")String sid,
            @Value("${twilio.token}")String token,
            @Value("${twilio.sms}")String fromPhone
    ){
        this.sid = sid;
        this.token = token;
        this.fromPhone = fromPhone;

    }

    public Message sendSms(String phoneNumber, String message){
        Twilio.init(sid,token);
        Twilio.setAccountSid(sid);
        Twilio.setUsername(sid);
        Twilio.setPassword(token);
        Message sms = Message.creator(
                new PhoneNumber(phoneNumber)
                , new PhoneNumber(fromPhone)
                , message
        ).create();
        return sms;
    }


}
