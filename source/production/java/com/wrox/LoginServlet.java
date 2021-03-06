/*
 * Karl McGuire (kmcgui15@uncc.edu)
 * 03/21/19
 * 
 */

package com.wrox;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import java.sql.*;


@WebServlet(
        name = "loginServlet",
        urlPatterns = "/login"
)

public class LoginServlet extends HttpServlet
{
    private static final Map<String, String> userDatabase = new Hashtable<>();

    /*
    static {
        userDatabase.put("Nicholas", "password");
        userDatabase.put("Sarah", "drowssap");
        userDatabase.put("Mike", "wordpass");
        userDatabase.put("John", "green");
    }
    */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if(request.getParameter("logout") != null)
        {
            session.invalidate();
            response.sendRedirect("login");
            return;
        }
        else if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }

        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // verify that the username and password aren't empty
        if(username == null || password == null) {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
                   .forward(request, response);
            return; 
        }

        // TODO: verify that the username and password don't contain SQLi

        // open database connection
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/customersupport", "root", "password");

        // try to find a row where the username and password match
        try {
            Statement stmt = conn.createStatement();
            ResultSet rslt = stmt.ExecuteQuery(
            "SELECT * FROM users WHERE UserID = '" + username + 
                              "` AND password = '" + password + "'");

            // no rows match, login is bad
            if(rslt == false) {
                request.setAttribute("loginFailed", true);
                request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
                    .forward(request, response);
                return; 
            }
        } catch(Exception e) {
            throw e; 
        }
            
        // good login
        session.setAttribute("username", username);
        request.changeSessionId();
        response.sendRedirect("tickets");
    }
}
