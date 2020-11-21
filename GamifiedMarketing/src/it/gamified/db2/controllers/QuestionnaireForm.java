package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.Product;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.Review;
import it.gamified.db2.exceptions.NoDailyQuestionnaire;
import it.gamified.db2.exceptions.NonUniqueDailyQuestionnaire;
import it.gamified.db2.services.QuestionnaireService;

@WebServlet("/QuestionnaireForm")
public class QuestionnaireForm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@PersistenceContext(unitName = "GamifiedMarketingEJB")
	private EntityManager em;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;

	public QuestionnaireForm() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath();
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		Questionnaire dailyQuest = null;
		List<MarketingQuestion> questions = null;
		Product product = null;

		try {
			dailyQuest = qService.findDailyQuestionnaire();
			questions = dailyQuest.getQuestions();
			product = dailyQuest.getProduct();
		} catch (NonUniqueDailyQuestionnaire e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (NoDailyQuestionnaire e) {
			// go to ERROR page which then has a button that redirects to homepage
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("questions", questions);
		ctx.setVariable("product", product);

		String path = "/WEB-INF/Questionnaire.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}