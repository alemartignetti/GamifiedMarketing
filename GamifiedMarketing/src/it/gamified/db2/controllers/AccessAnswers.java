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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.gamified.db2.entities.Answer;
import it.gamified.db2.entities.OptionalQuest;
import it.gamified.db2.exceptions.AnswerDuplicate;
import it.gamified.db2.services.AnswerService;
import it.gamified.db2.services.QuestionnaireService;

@WebServlet("/AccessAnswers")
public class AccessAnswers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.gamified.db2.services/AnswerService")
	private AnswerService aService;

	public AccessAnswers() {
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
		
		int quest_id;
		int user_id;
		quest_id = Integer.parseInt(request.getParameter("questionnaire_id"));
		user_id = Integer.parseInt(request.getParameter("user_id"));

		Answer answer = null;
		
		try {
			answer = aService.getAnswerByUserAndQuestionnaire(quest_id, user_id);
		} catch (AnswerDuplicate e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "There is more than one answer for the same user.");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		if(answer == null) {
			
		}
		
		ctx.setVariable("answer", answer);
		
		if(answer.getOptionalAnswer() == null) {
			
		}
		
		OptionalQuest optansw = answer.getOptionalAnswer();
		
		ctx.setVariable("age", optansw.getAge() == null ? "N/D" : optansw.getAge());
		ctx.setVariable("exp", optansw.getExpertise() == null ? "N/D" : optansw.getExpertise().name());
		ctx.setVariable("sex", optansw.getSex() == null ? "N/D" : optansw.getSex().name());
		
		String path = "/WEB-INF/AccessAnswers.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}