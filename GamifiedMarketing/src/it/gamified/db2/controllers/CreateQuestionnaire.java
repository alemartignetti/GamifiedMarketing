package it.gamified.db2.controllers;

import java.io.IOException;
import java.io.InputStream;
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

import it.gamified.db2.entities.User;
import it.gamified.db2.entities.User.Role;
import it.gamified.db2.exceptions.MissingData;
import it.gamified.db2.exceptions.NoDailyQuestionnaire;
import it.gamified.db2.exceptions.NonUniqueDailyQuestionnaire;
import it.gamified.db2.exceptions.PastDate;
import it.gamified.db2.services.QuestionnaireService;
import it.gamified.db2.utils.ImageUtils;
import it.gamified.db2.utils.StringUtil;

@WebServlet("/CreateQuestionnaire")
@MultipartConfig
public class CreateQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;
	
	public CreateQuestionnaire() {
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
		
		User user = (User) session.getAttribute("user");
		
		if (user.getUrole() == Role.USER) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("permmsg", "Permission Denied, you're not an Admin!");
			String path = "/WEB-INF/Permission.html";
			templateEngine.process(path, ctx, response.getWriter());
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
		byte[] imgByteArray = null;
		Date dateProduct = null;
		List<String> marketingQuestions = new ArrayList<>();

		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		User user = (User) session.getAttribute("user");

		if (user.getUrole() == Role.USER) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("permmsg", "Permission Denied, you're not an Admin and cannot create a questionnaire!");
			String path = "/WEB-INF/Permission.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}


		try {

			System.out.println("Creating Questionnaire");

			productTitle = StringEscapeUtils.escapeJava(request.getParameter("product-title"));

			System.out.println("Product Title:" + productTitle);


			dateProduct = StringUtil.handleFormat(request.getParameter("date")); //In some browser the format is not strict

			if(dateProduct == null) {
				String path = "/WEB-INF/CreateQuestionnaire.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("errorMsg", "Wrong date format. It should be in the form yyyy-mm-dd");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			else if(dateProduct.before(new Date())) {
				throw new PastDate("Inserted past date");
			}

			if(request.getPart("image-product").getSize()>0) {
				Part imgFile = request.getPart("image-product");
				InputStream imgContent = imgFile.getInputStream();
				imgByteArray = ImageUtils.readImage(imgContent);
			}

			System.out.println("Image:" + imgByteArray == null ? null : "not null");
			
			int counter = Integer.parseInt(request.getParameter("counter"));

			System.out.println("Counter:" + counter);

			String markQuestion = request.getParameter("insert-quest");
			System.out.println("Mark Question n" + 0 + ":" + markQuestion);
			if (markQuestion != null && !markQuestion.isEmpty()) {
				marketingQuestions.add(markQuestion);
			}

			if (counter > 1) {
				for (int i = 1; i <= counter; i++) {
					markQuestion = request.getParameter("mark_question_" + i);
					System.out.println("Mark Question n" + i + ":" + markQuestion);
					if (markQuestion != null && !markQuestion.isEmpty()) {
						marketingQuestions.add(markQuestion);
					}
				}
			}

			if (productTitle == null 
					|| productTitle.isEmpty()) {
				throw new MissingData("Missing product title.");
			}

			try {
				// query db to authenticate for user
				qService.createQuestionnaire(productTitle, imgByteArray, dateProduct, marketingQuestions);
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

		} catch (MissingData e) {
			// for debugging only e.printStackTrace();
			String path = "/WEB-INF/CreateQuestionnaire.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Some Data are missing");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} catch (PastDate e) {
			// for debugging only e.printStackTrace();
			String path = "/WEB-INF/CreateQuestionnaire.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Invalid Date inserted: cannot create questionnaire for past dates.");
			templateEngine.process(path, ctx, response.getWriter());
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