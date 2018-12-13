package testcode.sparrow;

public class InfiniteLoop__do {
    public void bad()
    {
        int i = 0;
        /* FLAW: Infinite Loop - do{} with no break point */
        do
        {
            System.out.println(i);
            i = (i + 1) % 256;
        } while(i >= 0);
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
            i = (i + 1) % 256;
        } while(i >= 0);
    }

    public void good()
    {
        good1();
    }
}
