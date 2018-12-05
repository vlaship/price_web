package by.spk.price.web.controller;

import by.spk.price.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("ver", Utils.getVersion());
        req.setAttribute("title", "login");

        req.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("login");
        String password = req.getParameter("password");

        if (name.equals(Utils.getPropertiesValue("web.login"))
                && password.equals(Utils.getPropertiesValue("web.password"))) {

            final Cookie cookie = new Cookie(Utils.getPropertiesValue("web.cookie.key"),
                    Utils.getPropertiesValue("web.cookie.value"));
            cookie.setPath(getServletContext().getContextPath());
            cookie.setMaxAge(3600);
            resp.addCookie(cookie);

            resp.sendRedirect(getServletContext().getContextPath() + "/price");
        } else {
            resp.setStatus(500);
            req.setAttribute("error", true);
            req.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
        }
    }
}