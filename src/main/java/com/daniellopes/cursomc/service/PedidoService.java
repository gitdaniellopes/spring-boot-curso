package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.*;
import com.daniellopes.cursomc.domain.enums.EstadoPagamento;
import com.daniellopes.cursomc.repository.ItemPedidoRepository;
import com.daniellopes.cursomc.repository.PagamentoRepository;
import com.daniellopes.cursomc.repository.PedidoRepository;
import com.daniellopes.cursomc.security.UserSs;
import com.daniellopes.cursomc.service.exception.AuthorizationException;
import com.daniellopes.cursomc.service.exception.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final BoletoService boletoService;
    private final PagamentoRepository pagamentoRepository;
    private final ProdutoService produtoService;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ClienteService clienteService;

    private final EmailService emailService;


    public PedidoService(PedidoRepository repository, BoletoService boletoService,
                         PagamentoRepository pagamentoRepository, ProdutoService produtoService, ItemPedidoRepository itemPedidoRepository, ClienteService clienteService, EmailService emailService) {
        this.repository = repository;
        this.boletoService = boletoService;
        this.pagamentoRepository = pagamentoRepository;
        this.produtoService = produtoService;
        this.itemPedidoRepository = itemPedidoRepository;
        this.clienteService = clienteService;
        this.emailService = emailService;
    }

    public Pedido find(Integer id) {
        Optional<Pedido> obj = repository.findById(id);
        UserSs user = UserService.authenticated();
        assert user != null;
        Cliente cliente = clienteService.find(user.getId());
        if (!cliente.getId().equals(repository.findById(id).get().getCliente().getId())) {
            throw new AuthorizationException("Acesso negado.");
        }
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
    }

    public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        UserSs user = UserService.authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso negado");
        }
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Cliente cliente =  clienteService.find(user.getId());
        return repository.findByCliente(cliente, pageRequest);
    }

    @Transactional
    public Pedido insert(Pedido pedido) {
        pedido.setId(null);
        pedido.setInstante(new Date());
        pedido.setCliente(clienteService.find(pedido.getCliente().getId()));
        pedido.getPagamento().setEstadoPagamento(EstadoPagamento.PENDENTE);
        pedido.getPagamento().setPedido(pedido);

        if (pedido.getPagamento() instanceof PagamentoComBoleto) {
            PagamentoComBoleto boleto = (PagamentoComBoleto) pedido.getPagamento();
            boletoService.preenchePagamentoComBoleto(boleto, pedido.getInstante());
        }

        pedido = repository.save(pedido);
        pagamentoRepository.save(pedido.getPagamento());

        for (ItemPedido itemPedido : pedido.getItens()) {
            itemPedido.setDesconto(0.0);
            itemPedido.setProduto(produtoService.find(itemPedido.getProduto().getId()));
            itemPedido.setPreco(itemPedido.getProduto().getPreco());
            itemPedido.setPedido(pedido);
        }
        itemPedidoRepository.saveAll(pedido.getItens());
        emailService.sendOrderConfirmationHtmlEmail(pedido);
        return pedido;
    }
}
