package testcode.sparrow;

import javax.servlet.http.*;

public class InappropriateAuthorization {
    /* uses badsource and badsink */
    public void bad(HttpServletRequest request) throws Throwable
    {
        String data;

        /* FLAW: Get the user ID from a URL parameter */
        data = request.getParameter("id");
        CustomAPI a = new CustomAPI();
        /* POTENTIAL FLAW: no check to see whether the user has privileges to view the data */
        a.CriticalFunction("bad() - result requested: " + data +"\n");
    }

    public void good(HttpServletRequest request) throws Throwable
    {
        String data;

        /* FLAW: Get the user ID from a URL parameter */
        data = request.getParameter("id");
        CustomAPI a = new CustomAPI();
        /* Check whether the user has privileges to view the data */
        data = a.Sanitizer(data);
        a.CriticalFunction("good() - result requested: " + data +"\n");
    }
}