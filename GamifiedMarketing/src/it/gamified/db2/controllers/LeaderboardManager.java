package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.List;
import java.util.List;

import javax.ejb.EJB;
//import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.exceptions.NoDailyQuestionnaire;
import it.gamified.db2.exceptions.NonUniqueDailyQuestionnaire;
import it.gamified.db2.services.QuestionnaireService;
import it.gamified.db2.services.UserServices;

@WebServlet("/Leaderboard")
public class LeaderboardManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/UserServices")
	private UserServices uService;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	

	public LeaderboardManager() {
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
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Questionnaire dailyQuest = null;
		
		try {
			dailyQuest = qService.findDailyQuestionnaire();
		} catch (NonUniqueDailyQuestionnaire e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (NoDailyQuestionnaire e) {
			System.out.print("No Daily Questionnaire, denying formatting.");
		}
		
		List<User> leaderboard = uService.getLeaderboard(dailyQuest.getId());
		
		boolean emptyLeaderboard = false;
		
		if(leaderboard == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Leaderboard is null");
			return;
		}
		else if(leaderboard.isEmpty()) {
			emptyLeaderboard = true;
		}
		
		String path = "/WEB-INF/Leaderboard.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("leaderboard", leaderboard);
		ctx.setVariable("emptyL", emptyLeaderboard);
		System.out.println("Leaderboard Formatted.");

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}