package motorphpayrollsystem2;
public class Employee {
    public int empNo;
    public String name;
    public String birthday;
    public double hourlyRate;
    public double[][] monthlyHours; // [12 months][4 weeks]

    public Employee(int empNo, String name, String birthday, double hourlyRate) {
        this.empNo = empNo;
        this.name = name;
        this.birthday = birthday;
        this.hourlyRate = hourlyRate;
        this.monthlyHours = new double[12][4]; // initialize all 0
    }
}