package br.senai.sp.cfp138.guideresta.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.guideresta.model.Restaurante;

public interface RestauranteRepository extends PagingAndSortingRepository<Restaurante, Long>{
	public List<Restaurante> findByTipoId(Long id);
	public List<Restaurante> findByEstacionamento(boolean estac);
}
