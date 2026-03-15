package motorphpayrollsystem2;

// Employee.java


import java.util.HashMap;

public class Employee {
    int empNo;
    String name;
    String birthday;
    double hourlyRate;

    // Map of month -> 4 weeks hours
    HashMap<Integer, double[]> monthlyHours = new HashMap<>();

    public Employee(int empNo, String name, String birthday, double hourlyRate){
        this.empNo = empNo;
        this.name = name;
        this.birthday = birthday;
        this.hourlyRate = hourlyRate;
    }

    // Get total monthly gross for a specific month
    public double getTotalMonthlyGross(int month){
        double total = 0;
        double[] weeks = monthlyHours.getOrDefault(month, new double[4]);
        for(double h : weeks) total += h * hourlyRate;
        return total;
    }
}