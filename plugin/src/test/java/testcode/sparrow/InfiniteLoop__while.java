package testcode.sparrow;

public class InfiniteLoop__while {
    public void bad()
    {
        int i = 0;

        /* FLAW: Infinite Loop - while() with no break point */
        while (i >= 0)
        {
            System.out.println(i);
            i = (i + 1) % 256;
        }
    }

    private void good1()
    {
        int i = 0;

        while (i >= 0)
        {
            /* FIX: Add a break point for the loop if i = 10 */
            if (i == 10)
            {
                break;
            }

            System.out.println(i);
            i = (i + 1) % 256;
        }
    }

    public void good()
    {
        good1();
    }
}
