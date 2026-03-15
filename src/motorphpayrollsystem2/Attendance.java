package motorphpayrollsystem2;
// Attendance.java
public class Attendance {
    double hoursWorked;

    public Attendance(String timeIn, String timeOut){
        String[] inT = timeIn.split(":");
        String[] outT = timeOut.split(":");
        double inH = Integer.parseInt(inT[0]) + Integer.parseInt(inT[1])/60.0;
        double outH = Integer.parseInt(outT[0]) + Integer.parseInt(outT[1])/60.0;
        this.hoursWorked = outH - inH;
    }

    public double calculateHoursWorked(){
        return hoursWorked;
    }
}