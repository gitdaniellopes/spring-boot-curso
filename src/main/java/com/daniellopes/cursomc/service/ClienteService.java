package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.Cidade;
import com.daniellopes.cursomc.domain.Cliente;
import com.daniellopes.cursomc.domain.Endereco;
import com.daniellopes.cursomc.domain.enums.Perfil;
import com.daniellopes.cursomc.domain.enums.TipoCliente;
import com.daniellopes.cursomc.dto.ClienteDTO;
import com.daniellopes.cursomc.dto.ClienteNewDTO;
import com.daniellopes.cursomc.repository.ClienteRepository;
import com.daniellopes.cursomc.repository.EnderecoRepository;
import com.daniellopes.cursomc.security.UserSs;
import com.daniellopes.cursomc.service.exception.AuthorizationException;
import com.daniellopes.cursomc.service.exception.DataIntegrityException;
import com.daniellopes.cursomc.service.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final ImageService imageService;

    @Value("${img.prefix.client.profile}")
    private String prefix;

    @Value("${img.profile.size}")
    private Integer size;

    public ClienteService(EnderecoRepository enderecoRepository,
                          ClienteRepository clienteRepository, BCryptPasswordEncoder passwordEncoder, S3Service s3Service, ImageService imageService) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.imageService = imageService;
    }

    public Cliente find(Integer id) {

        UserSs user = UserService.authenticated();
        if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
            throw new AuthorizationException("Acesso negado!");
        }

        Optional<Cliente> obj = clienteRepository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: "
                + id + ", Tipo: " + Cliente.class.getName()));
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findByEmail(String email) {
        UserSs user = UserService.authenticated();
        if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
            throw new AuthorizationException("Acesso negado");
        }

        Cliente obj = clienteRepository.findByEmail(email);
        if (obj == null) {
            throw new ObjectNotFoundException(
                    "Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Cliente.class.getName());
        }
        return obj;
    }

    public Page<Cliente> findPage(Integer page, Integer linesPerPage,
                                  String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage,
                Sort.Direction.valueOf(direction), orderBy);
        return clienteRepository.findAll(pageRequest);
    }

    @Transactional
    public Cliente insert(Cliente clienteObj) {
        clienteObj.setId(null);
        clienteObj = clienteRepository.save(clienteObj);
        enderecoRepository.saveAll(clienteObj.getEnderecos());
        return clienteObj;
    }

    public Cliente update(Cliente cliente) {
        Cliente clienteObj = find(cliente.getId());
        updateData(clienteObj, cliente);
        return clienteRepository.save(clienteObj);
    }

    //metodo que atualiza o cliente somente com os dados que passamos na requisição.
    private void updateData(Cliente clienteObj, Cliente cliente) {
        clienteObj.setNome(cliente.getNome());
        clienteObj.setEmail(cliente.getEmail());
    }

    public void delete(Integer id) {
        find(id);
        try {
            clienteRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possivel " +
                    "excluir por que a entidades relacionadas.");
        }
    }

    public Cliente fromDTO(ClienteDTO clienteDTO) {
        return new Cliente(clienteDTO.getId(), clienteDTO.getNome(),
                clienteDTO.getEmail(), null, null, null);
    }

    public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
        Cliente cliente = new Cliente(null,
                clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
                clienteNewDTO.getCpfOuCnpj(),
                TipoCliente.toEnum(clienteNewDTO.getTipoCliente()),
                passwordEncoder.encode(clienteNewDTO.getSenha()));

        Cidade cidade = new Cidade(clienteNewDTO.getCidadeId(), null, null);

        Endereco endereco = new Endereco(null,
                clienteNewDTO.getLogradouro(), clienteNewDTO.getNumero(), clienteNewDTO.getComplemento(),
                clienteNewDTO.getBairro(), clienteNewDTO.getCep(), cliente, cidade);

        cliente.getEnderecos().add(endereco);
        cliente.getTelefones().add(clienteNewDTO.getTelefone1());
        if (clienteNewDTO.getTelefone2() != null) {
            cliente.getTelefones().add(clienteNewDTO.getTelefone2());
        }
        if (clienteNewDTO.getTelefone3() != null) {
            cliente.getTelefones().add(clienteNewDTO.getTelefone3());
        }

        return cliente;
    }

    public URI uploadProfilePicture(MultipartFile multipartFile) {

        UserSs user = UserService.authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso negado!");
        }
        BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);
        jpgImage = imageService.cropSquare(jpgImage);
        jpgImage = imageService.resize(jpgImage, size);

        String fileName = prefix + user.getId() + ".jpg";

        return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"),
                fileName, "image");
    }
}
