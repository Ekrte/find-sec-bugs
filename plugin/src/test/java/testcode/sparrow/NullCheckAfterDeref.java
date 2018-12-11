package testcode.sparrow;

public class NullCheckAfterDeref {
    public void bad() throws Throwable
    {

        {
            String myString = null;
            myString = "Hello";

            System.out.println(myString.length());

            /* FLAW: Check for null after dereferencing the object. This null check is unnecessary. */
            if (myString != null)
            {
                myString = "my, how I've changed";
            }

            System.out.println(myString.length());
        }

    }

    public void good() throws Throwable
    {
        good1();
        good2();
    }

    private void good1() throws Throwable
    {

        {
            String myString = null;
            myString = "Hello";

            System.out.println(myString.length());

            /* FIX: Don't check for null since we wouldn't reach this line if the object was null */
            myString = "my, how I've changed";

            System.out.println(myString.length());
        }
    }

    private void good2() throws Throwable
    {

        {
            String myString = null;
            myString = "Hello";

            if ((myString != null) && (myString.length() > 0))
            /* FIX: Don't check for null since we wouldn't reach this line if the object was null */
            myString = "my, how I've changed";

            System.out.println(myString .length());
        }
    }
}
