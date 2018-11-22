package testcode.sparrow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class IntegerOverflow {

    public void bad() throws Throwable
    {
        int data;

        data = Integer.MIN_VALUE; /* Initialize data */

        /* read input from URLConnection */
        {
            URLConnection urlConnection = (new URL("http://www.example.org/")).openConnection();
            BufferedReader readerBuffered = null;
            InputStreamReader readerInputStream = null;

            try
            {
                readerInputStream = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
                readerBuffered = new BufferedReader(readerInputStream);

                /* POTENTIAL FLAW: Read data from a web server with URLConnection */
                /* This will be reading the first "line" of the response body,
                 * which could be very long if there are no newlines in the HTML */
                String stringNumber = readerBuffered.readLine();

                if (stringNumber != null) // avoid NPD incidental warnings
                {
                    try
                    {
                        data = Integer.parseInt(stringNumber.trim());
                    }
                    catch (NumberFormatException exceptNumberFormat)
                    {
                        System.out.println("Number format exception parsing data from string");
                    }
                }
            }
            catch (IOException exceptIO)
            {
                System.out.println("Error with stream reading");
            }
        }

        /* POTENTIAL FLAW: if data == Integer.MAX_VALUE, this will overflow */
        int result = (int)(data + 1);
        result = result + 1;
        result = data + data;
        System.out.println("False positive: " + data);
    }

    public void good() {
        int data;
        int temp=256;
        data = 1;
        data = 2;
        data = temp;
        data = temp + 1;
    }
}
