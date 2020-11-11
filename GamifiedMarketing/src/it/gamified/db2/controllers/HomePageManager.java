package it.gamified.db2.controllers;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.List;
import java.util.List;

//import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/HomePage")
public class HomePageManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public HomePageManager() {
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
//		String loginpath = getServletContext().getContextPath() + "/index.html";
//		HttpSession session = request.getSession();
//		if (session.isNew() || session.getAttribute("user") == null) {
//			response.sendRedirect(loginpath);
//			return;
//		}
		

//		try {
//
//			/*
//			 * HERE WE SHOW SEVERAL WAYS TO DEAL WITH SECOND LEVEL CACHING, AKA SHARED
//			 * CACHE. SHARED CACHE IS MAINTAINED BY THE ENTITY MANAGER FACTORY AND SERVES
//			 * REQUESTS FROM MULTIPLE ENTITY MANAGERS. IF AN ENTITY IS DELETED OUTSIDE OF
//			 * JPA THE SHARED CACHE MAY STILL SEE IT. THUS IF THE DATABASE IS ACCESSED ALSO
//			 * BY OTHER NON JPA APPLICATIONS AND JPA LOCKING IS NOT USED, REFRESHING THE
//			 * CACHE IS NEEDED TO SEE THE CURRENT DATABASE STATE. IF YOU WANT TO TEST THEM,
//			 * CHANGE THE SERVICE METHOD USED AND LOGIN. THEN DELETE SOME MISISONS WITH THE
//			 * MYSQL WORKBENCH, LOGOUT AND LOGIN AGAIN TO SEE THE DIFFERENT BEHAVIORS WRT
//			 * THE SHARED CACHE
//			 */
//			// These versions uses a JPQ query that is translated to SQL and bypasses the
//			// shared cache
//			// List<Mission> missions = mService.findMissionsByUserJPQL(user.getId());
//			// List<Mission> missions = mService.findMissionsByUserNoCache(user.getId());
//			// This version uses navigation and fetches missions from the shared cache
//			// (including deleted ones)
//
//			missions = mService.findMissionsByUser(user.getId());
//			// This version uses navigation and fetches missions from the shared cache
//			// However it explicitly refreshes the status of the reported from the database
//			// List<Mission> missions = mService.findMissionsByUserRefresh(user.getId());
//
//			projects = pService.findAllProjects();
//		} catch (Exception e) {
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data");
//			return;
//		}

		// Redirect to the Home page and add missions to the parameters
		String path = "/WEB-INF/HomePage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
//		ctx.setVariable("missions", missions);
//		ctx.setVariable("projects", projects);
		String product = "Generic";
		List<String> reviews = new ArrayList<String>(){{ add("1"); add("2"); }};
		ctx.setVariable("reviews", reviews);
		ctx.setVariable("product", product);

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
