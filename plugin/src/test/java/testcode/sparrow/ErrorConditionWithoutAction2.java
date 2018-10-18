package testcode.sparrow;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class ErrorConditionWithoutAction2
{
    native String test(String s1, int len);

    static
    {
        try
        {
            System.loadLibrary("JNITest"); /* load JNITest.dll or libJNITest.so */
        }
        catch (UnsatisfiedLinkError errorUnsatisfiedLink)
        {
            System.out.println("test");
        }
    }

    public void bad() throws IOException
    {
        InputStreamReader readerInputStream = null;
        BufferedReader readerBuffered = null;

        int intNumber = 0;
        try
        {
            System.out.println("test");

            readerInputStream = new InputStreamReader(System.in, "UTF-8");
            readerBuffered = new BufferedReader(readerInputStream);

            String stringLine = readerBuffered.readLine();

            System.out.println("test");
            intNumber = Integer.parseInt(readerBuffered.readLine());

            System.out.println("test");
        }
        catch (IOException exceptIO)
        {
        }
        finally
        {
            try
            {
                if (readerBuffered != null)
                {
                    readerBuffered.close();
                }
            }
            catch (IOException exceptIO)
            {
                System.out.println("test");
            }

            try
            {
                if (readerInputStream != null)
                {
                    readerInputStream.close();
                }
            }
            catch (IOException exceptIO)
            {
                System.out.println("test");
            }
        }
    }
}

