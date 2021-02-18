package it.gamified.db2.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
import it.gamified.db2.utils.ImageUtils;
import it.gamified.db2.utils.StringUtil;

@WebServlet("/QuestionnaireCreation")
@MultipartConfig
public class QuestionnaireCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	
	public QuestionnaireCreation() {
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
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		String path = "/WEB-INF/CreateQuestionnaire.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
				String productTitle = null;
				byte[] image = null;
				Date dateProduct = null;
				List<String> marketingQuestions = new ArrayList<>();
				
				String loginpath = getServletContext().getContextPath() + "/index.html";
				HttpSession session = request.getSession();
				if (session.isNew() || session.getAttribute("user") == null) {
					response.sendRedirect(loginpath);
					return;
				}
				
				
				try {
					
					System.out.println("Creating Questionnaire");
					
					productTitle = StringEscapeUtils.escapeJava(request.getParameter("product-title"));
					
					System.out.println("Product Title:" + productTitle);
					
					
					dateProduct = StringUtil.handleFormat(request.getParameter("date"));
					
					if(dateProduct == null) {
						String path = "/WEB-INF/CreateQuestionnaire.html";
						ServletContext servletContext = getServletContext();
						final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
						ctx.setVariable("errorMsg", "Wrong date format. It should be in the form yyyy-mm-dd");
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}

					Part imgFile = request.getPart("image-product");
					InputStream imgContent = imgFile.getInputStream();
					byte[] imgByteArray = ImageUtils.readImage(imgContent);
					
					System.out.println("Image:" + imgByteArray == null ? null : "not null");
					//int counter = 0;
					int counter = Integer.parseInt(request.getParameter("counter"));
					
					System.out.println("Counter:" + counter);
					
					String markQuestion = request.getParameter("insert-quest");
					System.out.println("Mark Question n" + 0 + ":" + markQuestion);
					if (markQuestion != null && !markQuestion.isEmpty()) {
						marketingQuestions.add(markQuestion);
					}
								
					if (counter > 1) {
						for (int i = 1; i < counter; i++) {
							markQuestion = request.getParameter("mark_question_" + i);
							System.out.println("Mark Question n" + i + ":" + markQuestion);
							if (markQuestion != null && !markQuestion.isEmpty()) {
								marketingQuestions.add(markQuestion);
							}
						}
					}
					
					if (productTitle == null 
						|| productTitle.isEmpty()) {
						throw new Exception("Missing product title.");
					}
					
					try {
						// query db to authenticate for user
						Questionnaire q = qService.createQuestionnaire(productTitle, imgByteArray,
								dateProduct, marketingQuestions);
					} catch (NonUniqueDailyQuestionnaire e) {
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
						return;
					} catch (NoDailyQuestionnaire e) {
						String path = "/WEB-INF/CreateQuestionnaire.html";
						ServletContext servletContext = getServletContext();
						final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
						ctx.setVariable("errorMsg", "Questionnaire already present. Change Date.");
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}

				} catch (Exception e) {
					// for debugging only e.printStackTrace();
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing values");
					return;
				}
				
				System.out.println("Success questionnaire");
				
				String path = "/WEB-INF/CreateQuestionnaire.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}