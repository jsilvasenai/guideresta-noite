package br.senai.sp.cfp138.guideresta.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.senai.sp.cfp138.guideresta.annotation.Privado;
import br.senai.sp.cfp138.guideresta.annotation.Publico;
import br.senai.sp.cfp138.guideresta.rest.UsuarioRestController;

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
				// variável para o token
				String token = null;
				// verificar se é um método privado
				if (metodo.getMethodAnnotation(Privado.class) != null) {
					try {
						// se o método for privado recupera o token
						token = request.getHeader("Authorization");
						// cria o algoritmo para assinar
						Algorithm algoritmo = Algorithm.HMAC256(UsuarioRestController.SECRET);
						// objeto para verificar o token
						JWTVerifier verifier = 
								JWT.require(algoritmo).withIssuer(UsuarioRestController.EMISSOR).build();
						// decodifica o Token
						DecodedJWT jwt = verifier.verify(token);
						// recupera os dados do payload
						Map<String, Claim> claims = jwt.getClaims();
						System.out.println(claims.get("name"));
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						if(token == null) {
							response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
						}else {
							response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
						}
						return false;
					}
				}
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
