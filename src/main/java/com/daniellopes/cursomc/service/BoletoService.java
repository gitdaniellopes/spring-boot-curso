package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.PagamentoComBoleto;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class BoletoService {

    public void preenchePagamentoComBoleto(PagamentoComBoleto boleto, Date instanteDePedido) {
        //em uma aplicação real, eu chamaria o web service que gera o boleto para min

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(instanteDePedido);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        boleto.setDateVencimento(calendar.getTime());
    }
}
