package testcode.sparrow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class ErrorConditionWithoutAction
{
    public void bad() throws Throwable
    {

        File file = null;
        FileInputStream streamFileInput = null;
        InputStreamReader readerInputStream = null;
        BufferedReader readerBuffered = null;

        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
        {
            /* running on Windows */
            file = new File("C:\\doesntexistandneverwill.txt");
        }
        else
        {
            /* running on non-Windows */
            file = new File("/home/user/doesntexistandneverwill.txt");
        }

        try
        {
            streamFileInput = new FileInputStream(file);
            readerInputStream = new InputStreamReader(streamFileInput, "UTF-8");
            readerBuffered = new BufferedReader(readerInputStream);
        }
        catch (FileNotFoundException exceptFileNotFound)
        {
            /* FLAW: do nothing if the file doesn't exist */
        }
        finally
        {
            /* Close stream reading objects */
            try
            {
                if (readerBuffered != null)
                {
                    readerBuffered.close();
                }
            }
            catch(Exception e)
            {
                System.err.println(e);
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
                //System.err.println(exceptIO);
            }

            try
            {
                if (streamFileInput != null)
                {
                    streamFileInput.close();
                }
            }
            catch (IOException exceptIO)
            {
                //System.err.println(exceptIO);
            }
        }
    }

    public void good() throws Throwable
    {
        good1();
    }

    public void good1() throws Throwable
    {

        File file = null;
        FileInputStream streamFileInput = null;
        InputStreamReader readerInputStream = null;
        BufferedReader readerBuffered = null;

        if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
        {
            /* running on Windows */
            file = new File("C:\\doesntexistandneverwill.txt");
        }
        else
        {
            /* running on non-Windows */
            file = new File("/home/user/doesntexistandneverwill.txt");
        }

        try
        {
            streamFileInput = new FileInputStream(file);
            readerInputStream = new InputStreamReader(streamFileInput, "UTF-8");
            readerBuffered = new BufferedReader(readerInputStream);
        }
        catch (FileNotFoundException exceptFileNotFound)
        {
            /* FIX: report read failure and rethrow */
            //System.err.println("something");
            throw exceptFileNotFound;
        }
        finally
        {
            /* Close stream reading objects */
            try
            {
                if (readerBuffered != null)
                {
                    readerBuffered.close();
                }
            }
            catch (IOException exceptIO)
            {
                System.err.println(exceptIO);
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
                System.err.println(exceptIO);
            }

            try
            {
                if (streamFileInput != null)
                {
                    streamFileInput.close();
                }
            }
            catch (IOException exceptIO)
            {
                System.err.println(exceptIO);
            }
        }
    }

    /* Below is the main(). It is only used when building this testcase on
     * its own for testing or for building a binary to use in testing binary
     * analysis tools. It is not used when compiling all the testcases as one
     * application, which is how source code analysis tools are tested.

    public void main(String[] args) throws IOException
    {
        try {
            good();
            bad();
        } catch (IOException exceptIO) {
            System.err.println(exceptIO);
        }
    }
    */
}

