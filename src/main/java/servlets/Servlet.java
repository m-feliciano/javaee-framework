package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import crud.Action;

@WebServlet("/company")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse resp) {

		String param = req.getParameter("action");
		
		String classname = "crud." + param;
		String path = null;

		try {
			Class<?> clazz = Class.forName(classname);
			Action action = (Action) clazz.getDeclaredConstructor().newInstance();
			path = action.doService(req, resp);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String[] array = path.split(":");

		if (array[0].equals("forward")) {
			try {
				req.getRequestDispatcher("/WEB-INF/view/" + array[1]).forward(req, resp);
			} catch (IOException | ServletException e) {
				e.printStackTrace();
			}
		} else {
			try {
				resp.sendRedirect(array[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
