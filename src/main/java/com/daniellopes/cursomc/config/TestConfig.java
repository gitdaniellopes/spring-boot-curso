package com.daniellopes.cursomc.config;

import com.daniellopes.cursomc.service.DbService;
import com.daniellopes.cursomc.service.EmailService;
import com.daniellopes.cursomc.service.MockEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.ParseException;

@Configuration
@Profile("test")
public class TestConfig {

    @Autowired
    private DbService dbService;

    @Bean
    public boolean instantiateDataBase() throws ParseException {
        dbService.instantiateTestDatabase();
        return true;
    }


    //O @Bean faz com que esse metodo fique disponivel como um componente no sistema
    // se em outra classe eu injeto o EmailService, o spring vai procurar esse bem
    // e vai me devolver a instancia dele que é o MockEmailService
    @Bean
    public EmailService emailService(){
        return new MockEmailService();
    }
}
