package br.senai.sp.cfp138.guideresta.rest;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.guideresta.annotation.Privado;
import br.senai.sp.cfp138.guideresta.annotation.Publico;
import br.senai.sp.cfp138.guideresta.model.Erro;
import br.senai.sp.cfp138.guideresta.model.Restaurante;
import br.senai.sp.cfp138.guideresta.model.Usuario;
import br.senai.sp.cfp138.guideresta.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioRestController {
	@Autowired
	private UsuarioRepository repository;

	@Publico
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> criarUsuario(@RequestBody Usuario usuario) {
		try {
			// insere o usuário no banco de dados
			repository.save(usuario);
			// retorna um código HTTP 201, informa como acessar o recurso inserido
			// e acrescenta no corpo da resposta o objeto inserido
			return ResponseEntity.created(URI.create("/api/usuario/" + usuario.getId())).body(usuario);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			Erro erro = new Erro(HttpStatus.INTERNAL_SERVER_ERROR,
					"Registro Duplicado", e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			e.printStackTrace();
			Erro erro = new Erro(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage(), e.getClass().getName());
			return new ResponseEntity<Object>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Privado
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ResponseEntity<Usuario> 
					getRestaurante(@PathVariable("id") Long idUsuario){
		// tenta buscar o usuário no repository
		Optional<Usuario> optional = repository.findById(idUsuario);
		// se o restaurante existir
		if(optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@Privado
	@RequestMapping(value="/{id}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> atualizarUsuario
				(@RequestBody Usuario usuario,@PathVariable("id") Long id){
		//validação do ID
		if(id != usuario.getId()) {
			throw new RuntimeException("ID Inválido");
		}
		repository.save(usuario);
		return ResponseEntity.ok().build();
	}
	
	@Privado
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> excluirUsuario
				(@PathVariable("id") Long idUsuario){
		repository.deleteById(idUsuario);
		return ResponseEntity.noContent().build();
	}
}
