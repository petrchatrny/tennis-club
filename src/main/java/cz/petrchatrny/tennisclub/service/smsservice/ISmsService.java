package cz.petrchatrny.tennisclub.service.smsservice;

public interface ISmsService {
    void sendRegistrationMessage(String phoneNumber, String password);
}
