package br.senai.sp.cfp138.guideresta.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.senai.sp.cfp138.guideresta.model.Usuario;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

}
