package br.senai.sp.cfp138.guideresta.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.guideresta.model.Avaliacao;

public interface AvaliacaoRepository extends PagingAndSortingRepository<Avaliacao, Long> {
	public List<Avaliacao> findByRestauranteId(Long idRest);
}
