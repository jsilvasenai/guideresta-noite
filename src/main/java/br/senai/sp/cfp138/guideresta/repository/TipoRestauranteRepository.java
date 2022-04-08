package br.senai.sp.cfp138.guideresta.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.senai.sp.cfp138.guideresta.model.TipoRestaurante;

public interface TipoRestauranteRepository extends PagingAndSortingRepository<TipoRestaurante, Long> {
	@Query("SELECT t FROM TipoRestaurante t WHERE t.nome LIKE %:p% OR t.palavrasChave LIKE %:p% ORDER BY t.nome ASC")
	public List<TipoRestaurante> buscarTipos(@Param("p") String parametro);
	
	public List<TipoRestaurante> findAllByOrderByNomeAsc();
}
