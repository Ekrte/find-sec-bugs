package testcode.sparrow;

public class ImproperCheckForExceptionalConditions {

    public void bad() throws Throwable
    {

        String systemProperty = System.getProperty("CWE395");

        try
        {
            /* INCIDENTAL: Possible Null Pointer Dereference (CWE476 / CWE690) */
            if(systemProperty.equals("CWE395"))
            {
                System.out.println("systemProperty is CWE395");
            }
        }
        catch (NullPointerException exceptNullPointer) /* FLAW: Use of catch block to detect null dereferences */
        {
            System.out.println("systemProperty is null");
        }

    }

    public void bad2() throws Throwable {
        try {
            Integer.parseInt("Test");
        } catch (Exception var2) {
            System.out.println("Caught Exception");
            throw var2;
        }
    }

    public void good() throws Throwable
    {
        good1();
    }

    private void good1() throws Throwable {

        String systemProperty = System.getProperty("CWE395");

        if (systemProperty != null) /* FIX: Check for null before calling equals() */ {
            if (systemProperty.equals("CWE395")) {
                System.out.println("systemProperty is CWE395");
            }
        } else {
            System.out.println("systemProperty is null");
        }
    }
}
