package testcode.sparrow;

public class InfiniteLoop__for_empty {
    public void bad()
    {
        int i = 0;

        /* FLAW: Infinite Loop - for() with no break point */
        for (;;)
        {
            System.out.println(i);
            i++;
        }
    }

    private void good1()
    {
        int i = 0;

        for (;;)
        {
            /* FIX: Add a break point for the loop if i = 10 */
            if (i == 10)
            {
                break;
            }

            System.out.println(i);
            i++;
        }
    }

    public void good()
    {
        good1();
    }
}
