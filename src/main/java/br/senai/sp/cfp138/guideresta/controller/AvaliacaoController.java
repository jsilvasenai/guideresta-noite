package br.senai.sp.cfp138.guideresta.controller;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.senai.sp.cfp138.guideresta.model.Avaliacao;
import br.senai.sp.cfp138.guideresta.repository.AvaliacaoRepository;

@Controller
public class AvaliacaoController {

	@Autowired
	private AvaliacaoRepository repository;

	@RequestMapping("/listarAvaliacao/{page}")
	public String listar(Model model, @PathVariable("page") int page) {
		// caso queira ordenar por algum campo, acrescenta-se o Sort.by no 3º parâmetro
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "restaurante.nome"));
		Page<Avaliacao> avalPage = repository.findAll(pageable);
		int totalPages = avalPage.getTotalPages();
		model.addAttribute("avaliacoes", avalPage.getContent());
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", page);
		List<Integer> pageNumbers = new ArrayList<Integer>();
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i + 1);
		}
		model.addAttribute("pageNumbers", pageNumbers);
		return "avaliacao/lista";
	}

	@RequestMapping("/excluirAvaliacao")
	public String excluirUsuario(Long idAvaliacao) {
		repository.deleteById(idAvaliacao);
		return "redirect:/listarAvaliacao/1";
	}
}
