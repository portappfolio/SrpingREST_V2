package com.portappfolio.app.email;

import com.portappfolio.app.appUser.phone.whatsapp.WhatsAppService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@AllArgsConstructor
public class EmailService {

    private static SendGrid sendGrid;
    private final SengridConfig sengridConfig;

    public String sendEmailVerification(String email, String customerName, String urlApiConfirmation, String s, String templateId){

        Email from = new Email("admin@portappfolio.com");
        from.setName("Panchita de Portappfolio");
        String subject = s;
        Email to = new Email(email);
        String urlApiVerification = urlApiConfirmation;

        Content content = new Content("text/html", "I'm replacing the <strong>body tag</strong>");

        Mail mail = new Mail(from, subject, to, content);
        mail.personalization.get(0).addDynamicTemplateData("emailConfirmationLink",urlApiVerification);
        mail.personalization.get(0).addDynamicTemplateData("customerName",customerName);
        mail.setTemplateId(templateId);

        SendGrid sg = sengridConfig.getSendGrid();
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            return request.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

}
