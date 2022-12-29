import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentTime {
    /**
     * @return current time
     */
    static String currentTime()  {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss a");

        // Creating an object of LocalDateTime class
        // and getting local date and time using now()
        // method
        LocalDateTime now = LocalDateTime.now();

        // Formatting LocalDateTime to string
        String dateTimeString = now.format(formatter);

        return dateTimeString;
    }
}
