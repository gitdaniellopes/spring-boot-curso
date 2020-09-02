package com.daniellopes.cursomc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;


public class MockEmailService extends AbstractEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sentEmail(SimpleMailMessage message) {
        LOG.info("Simulando serviço de email...");
        LOG.info(message.toString());
        LOG.info("Email enviado");
    }

    @Override
    public void sendHtmlEmail(MimeMessage message) {
        LOG.info("Simulando serviço de email...");
        LOG.info(message.toString());
        LOG.info("Email enviado");
    }
}
