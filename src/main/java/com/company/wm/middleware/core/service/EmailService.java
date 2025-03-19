package com.company.wm.middleware.core.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.wm.middleware.core.util.GlobalVariables;

@Service
public class EmailService {

    @Autowired 
    private HttpService httpService;

    private HashMap<String, Object> headerList;
    private String emailSenderUrlField;

    public EmailService(HttpService httpService) {
        this.httpService = httpService;
        this.emailSenderUrlField = "EmailSender_URL";
        headerList = new HashMap<>();
        headerList.put("Content-Type", "application/json; utf-8");
        headerList.put("Accept", "application/json");

    }

    public void errorNotificationEmail(String serviceName, String message) {

        String url = GlobalVariables.getSystemVariables().get(emailSenderUrlField);

        String requestBodyString = "{\"SenderEmail\": \"svcwm@akbank.de\"" +
                ",\"Password\": \"Akbank#2023#wm\"" +
                ",\"To\": \"parvin.akhundov@akbank.de\"" +
                ",\"Subject\": \"Error\"" +
                ",\"Body\": \"Service: " + serviceName + ",\nError: " + message.replace("\"", "'") + "\"" +
                ",\"SendAsEmail\": \"\"" +
                ",\"CC\": \"\"" +
                ",\"BCC\": \"\"" +
                ",\"Attachments\": \"[]\"";
        httpService.makeHttpCall(url, "POST", requestBodyString, headerList);
    }

    public void sucessNotificationEmail(String serviceName, String message) {
        String url = GlobalVariables.getSystemVariables().get(emailSenderUrlField);

        String requestBodyString = "{\"SenderEmail\": \"svcwm@akbank.de\"" +
                ",\"Password\": \"Akbank#2023#wm\"" +
                ",\"To\": \"parvin.akhundov@akbank.de\"" +
                ",\"Subject\": \"Sucess\"" +
                ",\"Body\": \"Service: " + serviceName + ",\nMessage: " + message.replace("\"", "'") + "\"" +
                ",\"SendAsEmail\": \"\"" +
                ",\"CC\": \"\"" +
                ",\"BCC\": \"\"" +
                ",\"Attachments\": \"[]\"}";

        httpService.makeHttpCall(url, "POST", requestBodyString, headerList);
    }

    public void sendEmail(Map<String, HashMap<String, String>> attachments, String body, String subject, String to,
            String cc, String bcc) {
        String url = GlobalVariables.getSystemVariables().get(emailSenderUrlField);

        String requestBodyString = "{\"SenderEmail\": \"svcwm@akbank.de\"" +
                ",\"Password\": \"Akbank#2023#wm\"" +
                ",\"To\": \"" + to + "\"" +
                ",\"Subject\": \"" + subject + "\"" +
                ",\"Body\": \"" + body + "\"" +
                ",\"SendAsEmail\": \"\"" +
                ",\"CC\": \"" + cc + "\"" +
                ",\"BCC\": \"" + bcc + "\"" +
                ",\"Attachments\": [{\"ContentBytes\":\"" + attachments.get("Attachments").get("ContentBytes")
                + "\",\"Name\":\"" + attachments.get("Attachments").get("Name") + "\"}]}";

        httpService.makeHttpCall(url, "POST", requestBodyString, headerList);
    }

}
