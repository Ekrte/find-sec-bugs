package testcode.sparrow;

public class RaceCondition {
    public void onCreate() {
        FileWriteThread fileWriteThread = new FileWriteThread();
        FileReadThread fileReadThread = new FileReadThread();
        FileDeleteThread fileDeleteThread = new FileDeleteThread();

        fileWriteThread.start();
        fileReadThread.start(); //bug
        fileDeleteThread.start(); //bug
    }
}