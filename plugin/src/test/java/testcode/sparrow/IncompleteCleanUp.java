package testcode.sparrow;

import testcode.juliet.IO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class IncompleteCleanUp {
    public void bad() throws Throwable
    {

        File tempFile = null;

        try
        {
            tempFile = File.createTempFile("temp", "1234");
            System.out.println(tempFile.toString());

            /* FLAW: Do not delete the temporary file */

            /* Set the permissions to avoid insecure temporary file incidentals  */
            if (!tempFile.setWritable(true, true))
            {
                System.out.println("Could not set Writable permissions");
            }
            if (!tempFile.setReadable(true, true))
            {
                System.out.println("Could not set Readable permissions");
            }
            if (!tempFile.setExecutable(false))
            {
                System.out.println("Could not set Executable permissions");
            }
        }
        catch (IOException exceptIO)
        {
            System.out.println("Could not create temporary file");
        }

    }

    public void good() throws Throwable
    {
        good1();
    }

    private void good1() throws Throwable
    {

        File tempFile = null;

        try
        {
            tempFile = File.createTempFile("temp", "1234");
            IO.writeLine(tempFile.toString());

            /* FIX: Call deleteOnExit() so that the file will be deleted */
            tempFile.deleteOnExit();

            /* Set the permissions to avoid insecure temporary file incidentals  */
            if (!tempFile.setWritable(true, true))
            {
                System.out.println("Could not set Writable permissions");
            }
            if (!tempFile.setReadable(true, true))
            {
                System.out.println("Could not set Readable permissions");
            }
            if (!tempFile.setExecutable(false))
            {
                System.out.println("Could not set Executable permissions");
            }
        }
        catch (IOException exceptIO)
        {
            System.out.println("Could not create temporary file");
        }
    }

    public void bad_Servlet(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {

        File tempFile = null;

        try
        {
            tempFile = File.createTempFile("temp", "1234");

            /* FLAW: Delete the temp file by using .deleteOnExit(). Using this method to delete
             * the file for a Servlet can keep the file in existence for a long time as it will not
             * be deleted until the JVM is shut down. */
            tempFile.deleteOnExit();

            /* Set the permissions to avoid insecure temporary file incidentals  */
            if (!tempFile.setWritable(true, true))
            {
                System.out.println("Could not set Writable permissions");
            }
            if (!tempFile.setReadable(true, true))
            {
                System.out.println("Could not set Readable permissions");
            }
            if (!tempFile.setExecutable(false))
            {
                System.out.println("Could not set Executable permissions");
            }
        }
        catch (IOException exceptIO)
        {
            System.out.println("Could not create temporary file");
        }

    }

    public void good_Servlet(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        good_Servlet(request, response);
    }

    private void good1_Servlet(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {

        File tempFile = null;

        try
        {
            tempFile = File.createTempFile("temp", "1234");

            /* Set the permissions to avoid insecure temporary file incidentals  */
            if (!tempFile.setWritable(true, true))
            {
                System.out.println("Could not set Writable permissions");
            }
            if (!tempFile.setReadable(true, true))
            {
                System.out.println("Could not set Readable permissions");
            }
            if (!tempFile.setExecutable(false))
            {
                System.out.println("Could not set Executable permissions");
            }
        }
        catch (IOException exceptIO)
        {
            System.out.println("Could not create temporary file");
        }
        finally
        {
            /* FIX: Delete the temporary file manually */
            if (tempFile.exists())
            {
                tempFile.delete();
            }
        }

    }
}
