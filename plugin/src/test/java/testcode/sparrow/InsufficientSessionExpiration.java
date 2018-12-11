package testcode.sparrow;

import javax.servlet.http.*;

public class InsufficientSessionExpiration extends HttpServlet {

    public void bad(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {

        HttpSession webSession = request.getSession(true);

        /* FLAW: A negative time indicates the session should never expire */
        webSession.setMaxInactiveInterval(-1);

        response.getWriter().write("bad(): Session still valid");

    }

    public void good(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        good1(request, response);
    }

    private void good1(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {

        HttpSession sesssion = request.getSession(true);

        /* FIX: enforce an absolute session timeout of 30 seconds */
        if (sesssion.getCreationTime() + 30000 > System.currentTimeMillis())
        {
            response.getWriter().write("good(): Invalidating session per absolute timeout enforcement");
            sesssion.invalidate();
            return;
        }
        else
        {
            response.getWriter().write("good(): Session still valid");
        }
    }
}
