package it.gamified.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.gamified.db2.utils.RegexUtils;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext(); //Retrieve parameters within action in index.html
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 2 cases can occur
		// 1. User is non existent
		// 2. Exception due to error in query
		String usr_field = null;
		String pass = null;
		try {
			usr_field = StringEscapeUtils.escapeJava(request.getParameter("username_email"));
			pass = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
			if (usr_field == null || pass == null || usr_field.isEmpty() || pass.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
//		User user; // entity to vbe created
//		try {
//			// query db to authenticate for user
//			user = usrService.checkCredentials(usr_field, pass);
//		} catch (CredentialsException | NonUniqueResultException e) {
//			e.printStackTrace();
//			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
//			return;
//		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (/* user == null */ false) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
//			request.getSession().setAttribute("user", user);
//			path = getServletContext().getContextPath() + "/Home";
//			response.sendRedirect(path);
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Success");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		}

	}

	public void destroy() {
	}
}
