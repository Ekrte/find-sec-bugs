package testcode.sparrow;

import java.io.*;

public class FileWriteThread extends Thread {
    public synchronized void run() {
        try {
            File f = new File("Test_367.txt");
            if (f.exists()) { // file exist check
                BufferedWriter bw = new BufferedWriter(new FileWriter(f)); // file write
                bw.write("Bug"); // Bug
                bw.close();
            }
        } catch (IOException e) { }
    } /* BUG */ // resource leak
}
