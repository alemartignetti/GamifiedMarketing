package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

import it.gamified.db2.entities.MarketingQuestion;
import it.gamified.db2.entities.Questionnaire;
import it.gamified.db2.entities.User;
import it.gamified.db2.exceptions.InvalidQuestionAnswer;
import it.gamified.db2.exceptions.OffensiveWord;
import it.gamified.db2.exceptions.TransactionError;
import it.gamified.db2.entities.OptionalQuest;
import it.gamified.db2.services.AnswerService;
import it.gamified.db2.services.MarketingQuestionService;
import it.gamified.db2.services.UserServices;

/**
 * Servlet implementation class AnswerSubmit
 */

@WebServlet("/AnswerSubmit")
public class AnswerSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/MarketingQuestionService")
	private MarketingQuestionService mqService;
	@EJB(name = "it.gamified.db2.services/AnswerService")
	private AnswerService aService;
	@EJB(name = "it.gamified.db2.services/UserServices")
	private UserServices uService;


    public AnswerSubmit() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath();
		
		System.out.println( request.getQueryString( ));
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Questionnaire dailyQuest = null;
		try {
			dailyQuest = (Questionnaire) session.getAttribute("dailyQuest");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tentative Submit failed due to dailyQuest being of wrong type.");
			return;
		}
		
		String homepath = loginpath + "/Home";
		if ( dailyQuest == null ) {
			response.sendRedirect(homepath);
			return;
		}
		
		// Get parameters list
		User user = (User) session.getAttribute("user");
		HashMap<Integer, String> answersQuestionnaire = new HashMap<Integer, String>();
		
		// Get id by hidden input value
		String baseString = "markquest_";
		String baseStringId = "id_question_";
		int questionNum = 1;
		String answerText = request.getParameter(baseString + questionNum);
		
		
		// Fill answers
		while(answerText != null) {
			answerText = StringEscapeUtils.escapeJava(answerText);
			
			int id = -1;
			try {
				System.out.println("RETRIEVING: " + baseStringId + questionNum);
				id = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter(baseStringId + questionNum)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tentative Submit failed due to bad formatted input tag.");
				return;
			}
			answersQuestionnaire.put(id, answerText);
			
			questionNum += 1;
			answerText = request.getParameter(baseString + questionNum);
		}
		
		// Fill optional answer
		String sex = StringEscapeUtils.escapeJava(request.getParameter("sex"));
		String expertise = StringEscapeUtils.escapeJava(request.getParameter("expertise"));
		String age = StringEscapeUtils.escapeJava(request.getParameter("age"));
		
		OptionalQuest optionalAnswer = new OptionalQuest(age, sex, expertise);
		
		try {
			aService.submitAnswer(dailyQuest.getId(), user.getId(), answersQuestionnaire, optionalAnswer);
		} catch(OffensiveWord e) {
			uService.blockUser(user.getId());
		} catch(InvalidQuestionAnswer e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Answer to non existent question.");
			return;
		}
		
		System.out.println("Done.!");
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("greetMsg", "Thank you kindly for answering the questionnaire!");
		String path = "/WEB-INF/ThankYou.html";
		templateEngine.process(path, ctx, response.getWriter());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
