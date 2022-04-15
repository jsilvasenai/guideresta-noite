package br.senai.sp.cfp138.guideresta.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
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

import br.senai.sp.cfp138.guideresta.model.Administrador;
import br.senai.sp.cfp138.guideresta.repository.AdminRepository;
import br.senai.sp.cfp138.guideresta.util.HashUtil;

@Controller
public class AdminController {

	// variável para persistências dos dados
	@Autowired
	private AdminRepository repository;

	// request mapping para o formulário, do tipo GET
	@RequestMapping("formAdministrador")
	public String form() {
		return "administrador/form";
	}

	// request mapping para salvar o Administrador, do tipo POST
	@RequestMapping(value = "salvarAdmin", method = RequestMethod.POST)
	public String salvarAdmin(@Valid Administrador admin, BindingResult result, RedirectAttributes attr) {
		// verifica se houveram erros na validação
		if (result.hasErrors()) {
			// adiciona uma mensagem de erro
			attr.addFlashAttribute("mensagemErro", "Verifique os campos...");
			// redireciona o para o formulário
			return "redirect:formAdministrador";
		}
		// variável para descobrir alteração ou inserção
		boolean alteracao = admin.getId() != null ? true : false;
		// verificar se a senha está vazia
		if (admin.getSenha().equals(HashUtil.hash(""))) {
			if (!alteracao) {
				// retira a parte antes do @ no e-mail
				String parte = admin.getEmail().substring(0, admin.getEmail().indexOf("@"));
				// "setar" a parte na senha do admin
				admin.setSenha(parte);
			}else {
				// buscar a senha atual no banco de dados
				String hash = repository.findById(admin.getId()).get().getSenha();
				// "setar" o hash na senha
				admin.setSenhaComHash(hash);
			}
		}

		try {
			// salva no bd a entidade
			repository.save(admin);
			// adiciona uma mensagem de sucesso
			attr.addFlashAttribute("mensagemSucesso", "Administrador cadastrado com sucesso. ID:" + admin.getId());
		} catch (Exception e) {
			// adiciona uma mensagem de sucesso
			attr.addFlashAttribute("mensagemErro", "Houve um erro ao cadastrar:" + e.getMessage());
		}

		// redireciona o para o formulário
		return "redirect:formAdministrador";
	}

	// request mapping para listar administradores
	@RequestMapping("listaAdmin/{page}")
	public String listaAdmin(Model model, @PathVariable("page") int page) {
		// cria um pageable informando os parâmetros da página
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		// cria um Page de Administrador através dos parâmetros passados ao repository
		Page<Administrador> pagina = repository.findAll(pageable);
		// adiciona à Model, a lista com os admins
		model.addAttribute("admins", pagina.getContent());
		// variável para o total de páginas
		int totalPages = pagina.getTotalPages();
		// cria um List de inteiros para armazenas os nºs das páginas
		List<Integer> numPaginas = new ArrayList<Integer>();
		// preencher o list com as páginas
		for (int i = 1; i <= totalPages; i++) {
			// adiciona a página ao list
			numPaginas.add(i);
		}
		// adiciona os valores à model
		model.addAttribute("numPaginas", numPaginas);
		model.addAttribute("totalPags", totalPages);
		model.addAttribute("pagAtual", page);
		// retorna para o html da lista
		return "administrador/lista";
	}

	@RequestMapping("/alterarAdm")
	public String alterarTipo(Model model, Long idAdm) {
		Administrador admin = repository.findById(idAdm).get();
		model.addAttribute("adm", admin);
		return "forward:/formAdministrador";
	}

	@RequestMapping("/excluirAdm")
	public String excluirTipo(Long idAdm) {
		repository.deleteById(idAdm);
		return "redirect:/listaAdmin/1";
	}
	
	@RequestMapping("login")
	public String login(Administrador admLogin, RedirectAttributes attr, HttpSession session) {
		// buscar o Administrador no banco
		Administrador admin = repository.
				findByEmailAndSenha(admLogin.getEmail(), admLogin.getSenha());
		// verificar se existe
		if(admin == null) {
			attr.addFlashAttribute("mensagemErro", "Login e/ou senha inválido(s)");
			return "redirect:/";
		}else {
			// salva o administrador na sessão
			session.setAttribute("usuarioLogado", admin);
			return "redirect:/listarRestaurante/1";
		}
	}
}
