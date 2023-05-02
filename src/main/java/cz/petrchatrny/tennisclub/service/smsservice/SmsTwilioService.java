package cz.petrchatrny.tennisclub.service.smsservice;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import cz.petrchatrny.tennisclub.config.TwilioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsTwilioService implements ISmsService {
    private final TwilioConfig twilioConfig;

    private final Logger LOGGER = LoggerFactory.getLogger(SmsTwilioService.class);

    public SmsTwilioService(TwilioConfig twilioConfiguration) {
        this.twilioConfig = twilioConfiguration;
        Twilio.init(
                twilioConfiguration.getAccountSid(),
                twilioConfiguration.getAuthToken()
        );
        LOGGER.info("Twilio initialized with account " + twilioConfig.getAccountSid());
    }

    @Override
    public void sendRegistrationMessage(String phoneNumber, String password) {
        String message = "Successfully registered in our tennis club. Your credentials: \n" +
                "username: " + phoneNumber + "\n" +
                "password: " + password;

        PhoneNumber sender = new PhoneNumber(twilioConfig.getTrialNumber());
        PhoneNumber receiver = new PhoneNumber(phoneNumber);

        try {
            Message.creator(receiver, sender, message).create();
            LOGGER.debug("Twilio SMS sent successfully");
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
        }
    }
}
