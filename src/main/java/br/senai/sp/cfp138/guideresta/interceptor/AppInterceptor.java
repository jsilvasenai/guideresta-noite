package br.senai.sp.cfp138.guideresta.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import br.senai.sp.cfp138.guideresta.annotation.Publico;

@Component
public class AppInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// variável para obter a URI
		String uri = request.getRequestURI();
		// variável para a sessão
		HttpSession session = request.getSession();
		// se for página de erro, libera
		if (uri.startsWith("/error")) {
			return true;
		}

		// verificar se handler é um HandlerMethod,
		// o que indica que ele está chamando um método
		// em algum controller
		if (handler instanceof HandlerMethod) {
			// casting de Object para HandlerMethod
			HandlerMethod metodo = (HandlerMethod) handler;
			if (uri.startsWith("/api")) {
				
				return true;
			} else {
				// verifica se este método é público
				if (metodo.getMethodAnnotation(Publico.class) != null) {
					return true;
				}
				// verifica se existe um usuário logado
				if (session.getAttribute("usuarioLogado") != null) {
					return true;
				}
				// redireciona para a página inicial
				response.sendRedirect("/");
				return false;
			}
		}
		return true;
	}
}
