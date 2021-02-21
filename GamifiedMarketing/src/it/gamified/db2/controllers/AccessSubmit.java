package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import it.gamified.db2.entities.Log;
import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.entities.User.Role;
import it.gamified.db2.services.AnswerService;
import it.gamified.db2.services.LogService;
import it.gamified.db2.services.QuestionnaireService;

@WebServlet("/AccessSubmit")
public class AccessSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.gamified.db2.services/AnswerService")
	private AnswerService aService;
	@EJB(name = "it.gamified.db2.services/LogService")
	private LogService lService;

	public AccessSubmit() {
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
		
		User user = (User) session.getAttribute("user");
		
		if (user.getUrole() == Role.USER) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("permmsg", "Permission Denied, you're not an Admin!");
			String path = "/WEB-INF/Permission.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		int quest_id;
		quest_id = Integer.parseInt(request.getParameter("quest_id"));

		Questionnaire questionnaire = null;
		List<MarketingQuestion> questions = null;
		List<Answer> answers = null;
		List<User> users = new ArrayList<User>();
		Set<User> cancelled_users = new HashSet<User>();
		List<Log> logs = null;
		
		questionnaire = qService.findQuestionnaire(quest_id);

		questions = questionnaire.getQuestions();
		answers = questionnaire.getAnswers();
		
		for (Answer a : answers) {
			users.add(a.getUser());
		}
		
		logs = lService.getCancelledLogs(questionnaire);
		
		for (Log l : logs) {
			cancelled_users.add(l.getUser());
			System.out.println("FOUND USER IN LOGS");
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		ctx.setVariable("questions", questions);
		ctx.setVariable("users", users);
		ctx.setVariable("questionnaire", questionnaire);
		ctx.setVariable("cusers", cancelled_users);
		
		String path = "/WEB-INF/AccessSubmit.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}