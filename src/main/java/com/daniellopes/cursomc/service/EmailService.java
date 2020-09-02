package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.Cliente;
import com.daniellopes.cursomc.domain.Pedido;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.internet.MimeMessage;

public interface EmailService {

    void sendOrderConfirmationEmail(Pedido pedido);

    void sentEmail(SimpleMailMessage message);

    void sendOrderConfirmationHtmlEmail(Pedido pedido);

    void sendHtmlEmail(MimeMessage message);

    void sendNewPasswordEmail(Cliente cliente, String newPass);
}
