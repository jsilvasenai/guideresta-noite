package br.senai.sp.cfp138.guideresta.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.guideresta.model.Restaurante;
import br.senai.sp.cfp138.guideresta.model.TipoRestaurante;

public interface RestauranteRepository extends PagingAndSortingRepository<Restaurante, Long>{
	
}
