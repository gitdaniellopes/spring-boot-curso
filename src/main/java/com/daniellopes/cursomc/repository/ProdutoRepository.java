package com.daniellopes.cursomc.repository;

import com.daniellopes.cursomc.domain.Categoria;
import com.daniellopes.cursomc.domain.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    @Query("SELECT DISTINCT obj FROM Produto obj " +
            "INNER JOIN obj.categorias cat WHERE  obj.nome LIKE %:nome% AND cat IN :categorias")
    Page<Produto> findDistinctByNomeContainingAndCategoriasIn(
            @Param("nome") String nome,
            @Param("categorias") List<Categoria> categorias,
            Pageable pageRequest);

    //posso fazer somente assim sem a query
    //Page<Produto> findDistinctByNomeContainingAndCategoriasIn(String nome,
    // List<Categoria> categorias,Pageable pageRequest);
}
