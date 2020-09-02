package com.daniellopes.cursomc.domain;

import com.daniellopes.cursomc.domain.enums.EstadoPagamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@JsonTypeName("pagamentoComBoleto")
public class PagamentoComBoleto extends Pagamento {

    @JsonFormat(pattern="dd/MM/yyyy")
    private Date dateVencimento;

    @JsonFormat(pattern="dd/MM/yyyy")
    private Date datePagamento;

    public PagamentoComBoleto() {
    }

    public PagamentoComBoleto(Integer id, EstadoPagamento estadoPagamento,
                              Pedido pedido,
                              Date dateVencimento, Date datePagamento) {
        super(id, estadoPagamento, pedido);
        this.dateVencimento = dateVencimento;
        this.datePagamento = datePagamento;
    }

    public Date getDateVencimento() {
        return dateVencimento;
    }

    public void setDateVencimento(Date dateVencimento) {
        this.dateVencimento = dateVencimento;
    }

    public Date getDatePagamento() {
        return datePagamento;
    }

    public void setDatePagamento(Date datePagamento) {
        this.datePagamento = datePagamento;
    }
}
