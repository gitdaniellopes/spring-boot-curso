package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.Cliente;
import com.daniellopes.cursomc.repository.ClienteRepository;
import com.daniellopes.cursomc.security.UserSs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new UsernameNotFoundException(email);
        }
        return new UserSs(cliente.getId(), cliente.getEmail(),
                cliente.getSenha(), cliente.getPerfis());
    }
}
