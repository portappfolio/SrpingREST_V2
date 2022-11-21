package com.portappfolio.app.appUser.phone.whatsapp;

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
public class WhatsAppService {

    private String sid;

    private String token;

    private String fromPhone;

    @Autowired
    public WhatsAppService(
            @Value("${twilio.sid}")String sid,
            @Value("${twilio.token}")String token,
            @Value("${twilio.whatsapp}")String fromPhone
    ){
        this.sid = sid;
        this.token = token;
        this.fromPhone = fromPhone;
    }

    public Message sendWhatsapp(String phoneNumber, String message){
        Twilio.init(sid,token);
        Twilio.setAccountSid(sid);
        Twilio.setUsername(sid);
        Twilio.setPassword(token);
        Message whatsApp = Message.creator(
                new PhoneNumber("whatsapp:"+phoneNumber),
                new PhoneNumber("whatsapp:"+fromPhone),
                message
        ).create();

        return whatsApp;
    }
}
