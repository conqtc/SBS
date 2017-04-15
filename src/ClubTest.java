import org.junit.runner.*;
import org.junit.runner.notification.*;

public class TestMyClass {
    /**
     * Main entry for the class
     * @param args String array of the parameters
     */
    public static void main(String[] args) {
    	System.out.println("(i) Testing Booking class...");
    	
    	Result result = JUnitCore.runClasses(BookingTest.class);
    	int count = result.getRunCount();
    	int failedCount = result.getFailureCount();

    	System.out.println("Number of test: " + count);
    	System.out.println("Failed test: " + failedCount);
    	
    	for (Failure fail: result.getFailures()) {
    		System.out.println(fail.toString());
    	}
    	
    	System.out.println((count - failedCount) + "/" + count + " PASSED.");
    }
}

