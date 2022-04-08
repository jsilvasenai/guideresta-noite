package br.senai.sp.cfp138.guideresta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.senai.sp.cfp138.guideresta.model.Restaurante;
import br.senai.sp.cfp138.guideresta.repository.RestauranteRepository;
import br.senai.sp.cfp138.guideresta.repository.TipoRestauranteRepository;

@Controller
public class RestauranteController  {
	@Autowired
	private TipoRestauranteRepository repTipo;
	@Autowired
	private RestauranteRepository repository;

	@RequestMapping("formRestaurante")
	public String form(Model model) {
		model.addAttribute("tipos", repTipo.findAllByOrderByNomeAsc());
		return "restaurante/form";
	}

	@RequestMapping("salvarRestaurante")
	public String salvar(Restaurante restaurante, @RequestParam("fileFotos") MultipartFile[] fileFotos) {

		System.out.println(fileFotos.length);
		// repository.save(restaurante);

		return "redirect:formRestaurante";
	}



}
