package testcode.command;

public class CustomCommandInjection {
    public void bad() throws Throwable
    {
        String data;
        /* get environment variable ADD */
        /* POTENTIAL FLAW: Read data from an environment variable */
        data = System.getenv("ADD");
        String oScommand = "";
        Process process = Runtime.getRuntime().exec(oScommand + data);
    }
}
