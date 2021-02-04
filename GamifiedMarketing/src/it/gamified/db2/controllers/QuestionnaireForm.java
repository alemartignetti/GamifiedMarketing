package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.List;

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
import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.exceptions.AlreadyAnswered;
import it.gamified.db2.exceptions.AnswerDuplicate;
import it.gamified.db2.exceptions.NoDailyQuestionnaire;
import it.gamified.db2.exceptions.NonUniqueDailyQuestionnaire;
import it.gamified.db2.services.AnswerService;
import it.gamified.db2.services.QuestionnaireService;

@WebServlet("/QuestionnaireForm")
public class QuestionnaireForm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.gamified.db2.services/AnswerService")
	private AnswerService aService;

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

		User user = (User) session.getAttribute("user");
		Questionnaire dailyQuest = null;
		List<MarketingQuestion> questions = null;
		boolean isDailyQuestionnaireAvailable = false;
		boolean areQuestionsAvailable = false;

		try {
			dailyQuest = qService.findDailyQuestionnaire();
			Answer answer = aService.getAnswerByUserAndQuestionnaire(dailyQuest.getId(), user.getId());
			
			if(answer != null) {
				throw new AlreadyAnswered("User already answered to the dailyQuestionnaire.");
			}
			questions = dailyQuest.getQuestions();
		} catch (NonUniqueDailyQuestionnaire | AnswerDuplicate e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (NoDailyQuestionnaire e) {
			response.sendRedirect(loginpath + "/HomePage");
			return;
		} catch (AlreadyAnswered e) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("greetMsg", "You've already answered the questionnaire, thank you!");
			String path = "/WEB-INF/ThankYou.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		if (dailyQuest != null) {
			// Questionnaire exists, it becomes a session variable
			session.setAttribute("dailyQuest", dailyQuest);

			// Set thymeleaf variable to present reviews and product
			ctx.setVariable("dailyQuest", dailyQuest);
			ctx.setVariable("questions", questions);
			isDailyQuestionnaireAvailable = true;
			areQuestionsAvailable = !questions.isEmpty();
			System.out.println("Are there questions? " + areQuestionsAvailable);
			System.out.println("Daily Questionnaire Formatted.");
		}
		
		ctx.setVariable("questAvail", isDailyQuestionnaireAvailable); // is there a questionnaire?
		ctx.setVariable("questionsAvail", areQuestionsAvailable); // are there questions?

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