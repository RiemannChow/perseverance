package com.riemann.service;

public interface MailService {

    public boolean sendTextMessage(String addressee, String subject, String messageContent);

}
