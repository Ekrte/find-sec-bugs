package testcode.sparrow;

public class InfiniteLoop__do_true {
    public void bad()
    {
        int i = 0;

        /* FLAW: Infinite Loop - do{}while(true)with no break point */
        do
        {
            System.out.println(i);
            i++;
        } while(true);
    }

    private void good1()
    {
        int i = 0;

        do
        {
            /* FIX: Add a break point for the loop if i = 10 */
            if (i == 10)
            {
                break;
            }

            System.out.println(i);
            i++;
        } while(true);
    }

    public void good()
    {
        good1();
    }
}
