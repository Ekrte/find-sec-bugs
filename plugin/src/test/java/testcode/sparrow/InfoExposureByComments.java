package testcode.sparrow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InfoExposureByComments {

    public void bad(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        /* FLAW: sensitive information exposed in client-side code comments */
        response.getWriter().println("<!--DB username = joe, DB password = 123-->" +
                "<form action=\"/test\" method=post>" +
                "<input type=text name=dbusername>" +
                "<input type=test name=dbpassword>" +
                "<input type=submit value=Submit>" +
                "</form>");
    }

    /* good1() changes IO.STATIC_FINAL_FIVE==5 to IO.STATIC_FINAL_FIVE!=5 */
    private void good1(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        /* FIX: no info exposure in client-side code comments */
        response.getWriter().println("<form action=\"/test\" method=post>" +
                "<input type=text name=dbusername>" +
                "<input type=test name=dbpassword>" +
                "<input type=submit value=Submit>" +
                "</form>");

    }

    /* good2() reverses the bodies in the if statement */
    private void good2(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        /* FIX: no info exposure in client-side code comments */
        response.getWriter().println("<form action=\"/test\" method=post>" +
                "<input type=text name=dbusername>" +
                "<input type=test name=dbpassword>" +
                "<input type=submit value=Submit>" +
                "</form>");
    }

    public void good(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        good1(request, response);
        good2(request, response);
    }
}
