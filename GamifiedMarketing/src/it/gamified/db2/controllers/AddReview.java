package it.gamified.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.exceptions.NoDailyQuestionnaire;
import it.gamified.db2.exceptions.NonUniqueDailyQuestionnaire;
import it.gamified.db2.services.QuestionnaireService;

/**
 * Servlet implementation class AddComment
 */
@WebServlet("/AddReview")
public class AddReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;
	
	public AddReview() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String basepath = getServletContext().getContextPath();
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(basepath);
			return;
		}
		
		User u = (User) session.getAttribute("user");
		Questionnaire q;
		try {
			q = qService.findDailyQuestionnaire();
		} catch (NonUniqueDailyQuestionnaire | NoDailyQuestionnaire e1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Daily Questionnaire does not exist");
			return;
		}
		
		String reviewText = null;
		
		try {
			reviewText = StringEscapeUtils.escapeJava(request.getParameter("add_review"));
			
			if (reviewText == null || reviewText.isEmpty() ) {
				throw new Exception("Missing Review text, could not post review.");
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		qService.addReviewToQuestionnaire(q, reviewText, u.getId());
		
		String path = getServletContext().getContextPath() + "/HomePage";
		response.sendRedirect(path);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/index.html";
		templateEngine.process(path, ctx, response.getWriter());
	}
	

	public void destroy() {
	}

}
