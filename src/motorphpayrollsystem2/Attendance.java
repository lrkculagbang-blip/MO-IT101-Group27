package motorphpayrollsystem2;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class Attendance {
    private final String timeIn;
    private final String timeOut;

    public Attendance(String timeIn, String timeOut) {
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public double calculateHoursWorked() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime in = LocalTime.parse(timeIn, formatter);
            LocalTime out = LocalTime.parse(timeOut, formatter);
            Duration duration = Duration.between(in, out);
            return duration.toMinutes() / 60.0;
        } catch (Exception e) {
            System.out.println("Error parsing time: " + timeIn + " - " + timeOut);
            e.printStackTrace();
            return 0;
        }
    }
}