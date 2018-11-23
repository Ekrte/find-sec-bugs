package testcode.sparrow;

import java.io.File;

public class FileDeleteThread extends Thread {
    public void run() {
        File f = new File("Test_367.txt");
        if (f.exists()) { // file exist check
            f.delete();                             /* BUG */
        }
    }
}
