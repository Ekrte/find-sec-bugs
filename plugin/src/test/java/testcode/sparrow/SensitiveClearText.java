package testcode.sparrow;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;

public class SensitiveClearText {
    public void bad_driverManager() throws Throwable
    {
        String password;

        password = ""; /* init password */

        /* Read data using an outbound tcp connection */
        {
            Socket socket = null;
            BufferedReader readerBuffered = null;
            InputStreamReader readerInputStream = null;
            try
            {
                /* Read data using an outbound tcp connection */
                socket = new Socket("host.example.org", 39544);

                /* read input from socket */

                /* POTENTIAL FLAW: Read password using an outbound tcp connection */
                readerInputStream = new InputStreamReader(socket.getInputStream(), "UTF-8");
                readerBuffered = new BufferedReader(readerInputStream);

                password = readerBuffered.readLine();
            }
            catch (IOException exceptIO)
            {
                System.out.println("Error with stream reading");
            }
            finally
            {
                /* clean up stream reading objects */
                try
                {
                    if (readerBuffered != null)
                    {
                        readerBuffered.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing BufferedReader");
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
                    System.out.println("Error closing InputStreamReader");
                }

                /* clean up socket objects */
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing Socket");
                }
            }
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            /* POTENTIAL FLAW: use password directly in getConnection() */
            connection = DriverManager.getConnection("data-url", "root", password);
            preparedStatement = connection.prepareStatement("select * from test_table");
            resultSet = preparedStatement.executeQuery();
        }
        catch (SQLException exceptSql)
        {
            System.out.println("Error with database connection");
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error closing ResultSet");
            }

            try
            {
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error closing PreparedStatement");
            }

            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error closing PreparedStatement");
            }
        }
    }

    public void bad_KerberosKey() throws Throwable
    {
        String password;

        password = ""; /* init password */

        /* read input from URLConnection */
        {
            URLConnection urlConnection = (new URL("http://www.example.org/")).openConnection();
            BufferedReader readerBuffered = null;
            InputStreamReader readerInputStream = null;

            try
            {
                readerInputStream = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
                readerBuffered = new BufferedReader(readerInputStream);

                /* POTENTIAL FLAW: Read password from a web server with URLConnection */
                /* This will be reading the first "line" of the response body,
                 * which could be very long if there are no newlines in the HTML */
                password = readerBuffered.readLine();
            }
            catch (IOException exceptIO)
            {
                System.out.println("Error with stream reading");
            }
            finally
            {
                /* clean up stream reading objects */
                try
                {
                    if (readerBuffered != null)
                    {
                        readerBuffered.close();
                    }
                }
                catch (IOException exceptIO)
                {

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
                    System.out.println("Error closing InputStreamReader");
                }
            }
        }

        if (password != null)
        {
            KerberosPrincipal principal = new KerberosPrincipal("test");
            /* POTENTIAL FLAW: Use password directly in KerberosKey() */
            KerberosKey key = new KerberosKey(principal, password.toCharArray(), null); //bad
        }

    }

    public void bad_Auth() throws Throwable
    {
        String password;

        password = ""; /* init password */

        /* Read data using an outbound tcp connection */
        {
            Socket socket = null;
            BufferedReader readerBuffered = null;
            InputStreamReader readerInputStream = null;
            try
            {
                /* Read data using an outbound tcp connection */
                socket = new Socket("host.example.org", 39544);

                /* read input from socket */

                /* POTENTIAL FLAW: Read password using an outbound tcp connection */
                readerInputStream = new InputStreamReader(socket.getInputStream(), "UTF-8");
                readerBuffered = new BufferedReader(readerInputStream);

                password = readerBuffered.readLine();
            }
            catch (IOException exceptIO)
            {
                System.out.println("Error with stream reading");
            }
            finally
            {
                /* clean up stream reading objects */
                try
                {
                    if (readerBuffered != null)
                    {
                        readerBuffered.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing BufferedReader");
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
                    System.out.println("Error closing InputStreamReader");
                }

                /* clean up socket objects */
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing Socket");
                }
            }
        }

        if (password != null)
        {
            /* POTENTIAL FLAW: Use password directly in PasswordAuthentication() */
            PasswordAuthentication credentials = new PasswordAuthentication("user", password.toCharArray());
        }

    }

    public void good() throws Throwable
    {
        goodG2B();
        goodB2G();
    }

    /* goodG2B() - use goodsource and badsink */
    private void goodG2B() throws Throwable
    {
        String password;

        /* FIX: Use a hardcoded password as the password (it was not sent over the network) */
        /* INCIDENTAL FLAW: CWE-259 Hard Coded Password */
        password = "Password1234!";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try
        {
            /* POTENTIAL FLAW: use password directly in getConnection() */
            connection = DriverManager.getConnection("data-url", "root", password);
            preparedStatement = connection.prepareStatement("select * from test_table");
            resultSet = preparedStatement.executeQuery();
        }
        catch (SQLException exceptSql)
        {
            System.out.println("Error with database connection");
        }
        finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
            }
            catch (SQLException exceptSql)
            {

                System.out.println("Error closing ResultSet");
            }

            try
            {
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error closing PreparedStatement");
            }

            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error closing Connection");
            }
        }

    }

    /* goodB2G() - use badsource and goodsink */
    private void goodB2G() throws Throwable
    {
        String password;

        password = ""; /* init password */

        /* Read data using an outbound tcp connection */
        {
            Socket socket = null;
            BufferedReader readerBuffered = null;
            InputStreamReader readerInputStream = null;
            try
            {
                /* Read data using an outbound tcp connection */
                socket = new Socket("host.example.org", 39544);

                /* read input from socket */

                /* POTENTIAL FLAW: Read password using an outbound tcp connection */
                readerInputStream = new InputStreamReader(socket.getInputStream(), "UTF-8");
                readerBuffered = new BufferedReader(readerInputStream);

                password = readerBuffered.readLine();
            }
            catch (IOException exceptIO)
            {
                System.out.println("Error with stream reading");
            }
            finally
            {
                /* clean up stream reading objects */
                try
                {
                    if (readerBuffered != null)
                    {
                        readerBuffered.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing BufferedReader");
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
                    System.out.println("Error closing InputStreamReader");
                }

                /* clean up socket objects */
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException exceptIO)
                {
                    System.out.println("Error closing Socket");
                }
            }
        }

        if (password != null)
        {
            /* FIX: Decrypt password before using in getConnection() */
            {
                Cipher aesCipher = Cipher.getInstance("AES");
                /* INCIDENTAL: CWE-321: Use of Hard-coded Cryptographic Key */
                SecretKeySpec secretKeySpec = new SecretKeySpec("ABCDEFGHABCDEFGH".getBytes("UTF-8"), "AES");
                aesCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                password = new String(aesCipher.doFinal(password.getBytes("UTF-8")), "UTF-8");
            }
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try
            {
                /* POTENTIAL FLAW: use password directly in getConnection() */
                connection = DriverManager.getConnection("data-url", "root", password);
                preparedStatement = connection.prepareStatement("select * from test_table");
                resultSet = preparedStatement.executeQuery();
            }
            catch (SQLException exceptSql)
            {
                System.out.println("Error with database connection");
            }
            finally
            {
                try
                {
                    if (resultSet != null)
                    {
                        resultSet.close();
                    }
                }
                catch (SQLException exceptSql)
                {
                    System.out.println("Error closing ResultSet");
                }

                try
                {
                    if (preparedStatement != null)
                    {
                        preparedStatement.close();
                    }
                }
                catch (SQLException exceptSql)
                {
                    System.out.println("Error closing PreparedStatement");
                }

                try
                {
                    if (connection != null)
                    {
                        connection.close();
                    }
                }
                catch (SQLException exceptSql)
                {
                    System.out.println("Error closing Connection");
                }
            }
        }

    }
}
