package br.senai.sp.cfp138.guideresta.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.senai.sp.cfp138.guideresta.model.Restaurante;
import br.senai.sp.cfp138.guideresta.repository.RestauranteRepository;
import br.senai.sp.cfp138.guideresta.repository.TipoRestauranteRepository;
import br.senai.sp.cfp138.guideresta.util.FirebaseUtil;

@Controller
public class RestauranteController {
	@Autowired
	private TipoRestauranteRepository repTipo;
	@Autowired
	private RestauranteRepository repository;
	@Autowired
	private FirebaseUtil fireUtil;

	@RequestMapping("formRestaurante")
	public String form(Model model) {
		model.addAttribute("tipos", repTipo.findAllByOrderByNomeAsc());
		return "restaurante/form";
	}

	@RequestMapping("/listarRestaurante/{page}")
	public String listar(Model model, @PathVariable("page") int page) {
		// caso queira ordenar por algum campo, acrescenta-se o Sort.by no 3º parâmetro
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		Page<Restaurante> restaPage = repository.findAll(pageable);
		int totalPages = restaPage.getTotalPages();
		model.addAttribute("rests", restaPage.getContent());
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", page);
		List<Integer> pageNumbers = new ArrayList<Integer>();
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i + 1);
		}
		model.addAttribute("pageNumbers", pageNumbers);
		return "restaurante/lista";
	}

	@RequestMapping("/alterarRest")
	public String alterarRestaurante(Model model, Long idRest) {
		Restaurante restaurante = repository.findById(idRest).get();
		model.addAttribute("restaurante", restaurante);
		return "forward:/formRestaurante";
	}

	@RequestMapping("salvarRestaurante")
	public String salvar(Restaurante restaurante, @RequestParam("fileFotos") MultipartFile[] fileFotos) {
		// String para armazenar as URLs
		String fotos = restaurante.getFotos();
		// percorre cada arquivo no vetor
		for (MultipartFile arquivo : fileFotos) {
			// verifica se o arquivo existe
			if (arquivo.getOriginalFilename().isEmpty()) {
				// vai para o próximo arquivo
				continue;
			}
			try {
				// faz o upload e guarda a URL na String fotos
				fotos += fireUtil.upload(arquivo) + ";";
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		// guarda na variável restaurante as fotos
		restaurante.setFotos(fotos);
		// salva no BD
		repository.save(restaurante);
		return "redirect:formRestaurante";
	}

	@RequestMapping("/excluirRestaurante")
	public String excluirRestaurante(Long idRestaurante) {
		Restaurante rest = repository.findById(idRestaurante).get();
		if (rest.getFotos().length() > 0) {
			for (String foto : rest.verFotos()) {
				fireUtil.deletar(foto);
			}
		}
		repository.delete(rest);
		return "redirect:/listarRestaurante/1";
	}

	@RequestMapping("excluirFotos")
	public String excluirFotos(Long idRest, int numFoto, Model model) {
		// busca o Restaurante
		Restaurante rest = repository.findById(idRest).get();
		// busca a URL da foto
		String urlFoto = rest.verFotos()[numFoto];
		// deleta a foto
		fireUtil.deletar(urlFoto);
		// remove a url do atributo fotos
		rest.setFotos(rest.getFotos().replace(urlFoto + ";", ""));
		// salva no BD
		repository.save(rest);
		// coloca o rest na Model
		model.addAttribute("restaurante", rest);
		return "forward:/formRestaurante";
	}

}
