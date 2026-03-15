package motorphpayrollsystem2;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {

        HashMap<Integer, Employee> employees = new HashMap<>();

        
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("employees.csv"))) {
                br.readLine(); // skip header
                String line;
                
                while ((line = br.readLine()) != null) {
                    
                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    
                    int empNo = Integer.parseInt(data[0]);
                    String lastName = data[1];
                    String firstName = data[2];
                    String birthday = data[3];
                    
                    double hourlyRate = Double.parseDouble(data[18].replace("\"", "").trim());
                    
                    Employee emp = new Employee(empNo, firstName + " " + lastName, birthday, hourlyRate);
                    employees.put(empNo, emp);
                }
            } // skip header
        } catch (Exception e) {
            System.out.println("Error reading employees.csv");
        }

       
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("attendance.csv"))) {
                br.readLine(); // skip header
                String line;
                
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    
                    int empNo = Integer.parseInt(data[0]);
                    String date = data[3];  // MM/DD/YYYY
                    String timeIn = data[4];
                    String timeOut = data[5];
                    
                    Employee emp = employees.get(empNo);
                    if (emp != null) {
                        Attendance att = new Attendance(timeIn, timeOut);
                        double hours = att.calculateHoursWorked();
                        
                        String[] dateParts = date.split("/");
                        
                        int month = Integer.parseInt(dateParts[0]);
                        int day = Integer.parseInt(dateParts[1]);
                        
                        int week = (day - 1) / 7;
                        if (week > 3) week = 3;
                        
                        double[] weeks = emp.monthlyHours.getOrDefault(month, new double[4]);
                        weeks[week] += hours;
                        emp.monthlyHours.put(month, weeks);
                    }
                }
            } // skip header
        } catch (Exception e) {
            System.out.println("Error reading attendance.csv");
        }

        
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Employee Number: ");
        int empNo = sc.nextInt();

        System.out.print("Enter Month (1-12): ");
        int month = sc.nextInt();

        System.out.print("Enter Week Number (1-4): ");
        int week = sc.nextInt() - 1; // array is 0-indexed

        System.out.println("------------------------------------------------");

        Employee emp = employees.get(empNo);
        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        double[] weeks = emp.monthlyHours.getOrDefault(month, new double[4]);
        double hours = weeks[week];
        double gross = hours * emp.hourlyRate;
        double monthlyGross = emp.getTotalMonthlyGross(month);

        // -------------------- PRINT OUTPUT --------------------
        System.out.println("Employee #: " + emp.empNo);
        System.out.println("Name: " + emp.name);
        System.out.println("Birthday: " + emp.birthday);
        System.out.println("Month: " + month);
        System.out.println("Week: " + (week + 1));
        System.out.printf("Hours Worked: %.2f\n", hours);
        System.out.printf("Gross Salary: %.2f\n", gross);

        if (week == 3) { // Week 4 → deductions applied
            double sss = Payroll.SSS(monthlyGross);
            double ph = Payroll.PhilHealth(monthlyGross);
            double pi = Payroll.PAGIBIG(monthlyGross);
            double taxable = monthlyGross - (sss + ph + pi);
            double wht = Payroll.WithHoldingTax(taxable);
            double totalDeduction = sss + ph + pi + wht;
            double net = gross - totalDeduction;

            System.out.printf("SSS: %.2f\n", sss);
            System.out.printf("PhilHealth: %.2f\n", ph);
            System.out.printf("Pag-IBIG: %.2f\n", pi);
            System.out.printf("Withholding Tax: %.2f\n", wht);
            System.out.printf("Total Deduction: %.2f\n", totalDeduction);
            System.out.printf("Net Salary: %.2f\n", net);
        } else {
            System.out.printf("Net Salary: %.2f\n", gross);
        }

        System.out.println("------------------------------------------------");
    }
}