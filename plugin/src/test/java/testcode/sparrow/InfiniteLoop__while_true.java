package testcode.sparrow;

public class InfiniteLoop__while_true {
    public void bad()
    {
        int i = 0;

        /* FLAW: Infinite Loop - while(true) with no break point */
        while(true)
        {
            System.out.println(i);
            i++;
        }
    }

    private void good1()
    {
        int i = 0;

        while(true)
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
