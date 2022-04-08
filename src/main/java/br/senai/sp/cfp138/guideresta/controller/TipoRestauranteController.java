package br.senai.sp.cfp138.guideresta.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.senai.sp.cfp138.guideresta.model.TipoRestaurante;
import br.senai.sp.cfp138.guideresta.repository.TipoRestauranteRepository;

@Controller
public class TipoRestauranteController {
	// repository para persistência do Tipo do Restaurante
	// @AutoWired para injetar a dependência
	@Autowired
	private TipoRestauranteRepository repository;

	// request mapping para o formulário
	@RequestMapping("formTipoRestaurante")
	public String form() {
		return "tipoRestaurante/form";
	}

	// request mapping para salvar o Tipo de Restaurante
	@RequestMapping(value = "salvarTipo", method = RequestMethod.POST)
	public String salvarAdmin(@Valid TipoRestaurante tipo, BindingResult result, RedirectAttributes attr) {
		// verifica se houve erro na validação do objeto
		if (result.hasErrors()) {
			// envia msg de erro via requisição
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			return "redirect:formTipoRestaurante";
		}

		try {
			// salva o Tipo de Restaurante
			repository.save(tipo);
			attr.addFlashAttribute("mensagemSucesso",
					"Tipo de restaurante salvo com sucesso. ID do Tipo:" + tipo.getId());
		} catch (Exception e) {
			// caro ocorra uma Exception informa ao usuário
			attr.addFlashAttribute("mensagemErro",
					"Houve um erro ao cadastrar o Tipo de Restaurante: " + e.getMessage());
		}
		return "redirect:formTipoRestaurante";
	}

	// request mapping para listar, informando a página desejada
	@RequestMapping("listarTipos/{page}")
	public String listar(Model model, @PathVariable("page") int page) {
		// cria um pageable com 12 elementos por página, ordenando os objetos pelo nome,
		// de forma ascendente
		PageRequest pageable = PageRequest.of(page - 1, 12, Sort.by(Sort.Direction.ASC, "nome"));
		// cria a página atual através do repository
		Page<TipoRestaurante> pagina = repository.findAll(pageable);
		// descobrir o total de páginas
		int totalPages = pagina.getTotalPages();
		// cria uma lista de inteiros para representar as paginas
		List<Integer> pageNumbers = new ArrayList<Integer>();
		// preencher a lista com as páginas
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i + 1);
		}
		// adiciona as variáveis na Model
		model.addAttribute("tipos", pagina.getContent());
		model.addAttribute("paginaAtual", page);
		model.addAttribute("totalPaginas", totalPages);
		model.addAttribute("numPaginas", pageNumbers);
		// retorna para o HTML da lista
		return "tipoRestaurante/lista";
	}
	
	@RequestMapping("/alterarTipo")
	public String alterarTipo(Model model, Long idTipo) {
		TipoRestaurante tipo = repository.findById(idTipo).get();
		model.addAttribute("tipo", tipo);
		return "forward:/formTipoRestaurante";
	}

	@RequestMapping("/excluirTipo")
	public String excluirTipo(Long idTipo) {
		repository.deleteById(idTipo);
		return "redirect:/listarTipos/1";
	}
	
	// request mapping para buscar
		@RequestMapping("buscarTipos")
		public String buscar(Model model, String parametro) {
			// busca
			List<TipoRestaurante> tipos = repository.buscarTipos(parametro);
			if(tipos.size() == 0) {
				model.addAttribute("mensagemErro", "Nenhuma correspondência encontrada");
			}else {
				model.addAttribute("tipos", repository.buscarTipos(parametro));	
			}			
			return "tipoRestaurante/lista";
		}
	
}
