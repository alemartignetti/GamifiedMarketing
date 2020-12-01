package it.gamified.db2.controllers;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.gamified.db2.services.QuestionnaireService;

@WebServlet("/DeleteSubmit")
public class DeleteSubmit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.gamified.db2.services/QuestionnaireService")
	private QuestionnaireService qService;

	public DeleteSubmit() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int quest_id;
		quest_id = Integer.parseInt(request.getParameter("quest_id"));
		
		qService.deleteQuestionnaire(quest_id);
		
		String path = getServletContext().getContextPath() + "/DeleteData";
		response.sendRedirect(path);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}