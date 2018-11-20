package testcode.sparrow;

public class CustomAPI {
    public void CriticalFunction(String data) {
        System.out.println("Critical!!" + data);
        return;
    }

    public String Sanitizer(String data) {
        System.out.println("Sanitize tainted variable" + data);
        return data;
    }
}
