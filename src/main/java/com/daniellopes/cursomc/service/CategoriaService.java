package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.domain.Categoria;
import com.daniellopes.cursomc.domain.Cliente;
import com.daniellopes.cursomc.dto.CategoriaDTO;
import com.daniellopes.cursomc.repository.CategoriaRepository;
import com.daniellopes.cursomc.service.exception.DataIntegrityException;
import com.daniellopes.cursomc.service.exception.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> findAll() {
        return repository.findAll();
    }

    public Categoria find(Integer id) {
        Optional<Categoria> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: "
                + id + ", Tipo: " + Categoria.class.getName()));
    }


    public Page<Categoria> findPage(Integer page, Integer linesPerPage,
                                    String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage,
                Sort.Direction.valueOf(direction), orderBy);
        return repository.findAll(pageRequest);
    }

    public Categoria insert(Categoria categoria) {
        categoria.setId(null);
        return repository.save(categoria);
    }

    public Categoria update(Categoria categoria) {
        Categoria categoriaObj = find(categoria.getId());
        updateData(categoriaObj, categoria);
        return repository.save(categoriaObj);
    }

    private void updateData(Categoria categoriaObj, Categoria categoria) {
        categoriaObj.setNome(categoria.getNome());
    }

    public void delete(Integer id) {
        find(id);
        try {
            repository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possivel " +
                    "excluir uma categoria que possui produtos.");
        }
    }

    public Categoria fromDTO(CategoriaDTO categoriaDTO) {
        return new Categoria(categoriaDTO.getId(), categoriaDTO.getNome());
    }

}
