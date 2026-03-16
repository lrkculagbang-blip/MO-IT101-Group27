package motorphpayrollsystem2;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Employee> employees = new ArrayList<>();

        // --- Load employees.csv ---
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Main.class.getResourceAsStream("/employees.csv")))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                int empNo = Integer.parseInt(data[0].trim());
                String lastName = data[1].trim();
                String firstName = data[2].trim();
                String birthday = data[3].trim();
                String rateStr = data[18].trim().replace("\"", "").replace(",", "");
                double hourlyRate = Double.parseDouble(rateStr);

                Employee emp = new Employee(empNo, firstName + " " + lastName, birthday, hourlyRate);
                employees.add(emp);
            }
        } catch (Exception e) {
            System.out.println("Error reading employees.csv");
            return;
        }

        // --- Load attendance.csv ---
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Main.class.getResourceAsStream("/attendance.csv")))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                int empNo = Integer.parseInt(data[0].trim());
                String date = data[3].trim();
                String timeIn = data[4].trim();
                String timeOut = data[5].trim();

                for (Employee emp : employees) {
                    if (emp.empNo == empNo) {
                        Attendance att = new Attendance(timeIn, timeOut);
                        double hours = att.calculateHoursWorked();

                        int month = Integer.parseInt(date.split("/")[0]);
                        int day = Integer.parseInt(date.split("/")[1]);
                        int week = (day - 1) / 7;
                        if (week > 3) week = 3;

                        emp.monthlyHours[month - 1][week] += hours;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading attendance.csv");
            return;
        }

        // --- Input ---
        System.out.print("Enter Employee Number: ");
        int inputEmp = sc.nextInt();
        System.out.print("Enter Month (1-12): ");
        int inputMonth = sc.nextInt();
        System.out.print("Enter Week Number (1-4): ");
        int inputWeek = sc.nextInt() - 1;

        Employee selectedEmp = null;
        for (Employee emp : employees) {
            if (emp.empNo == inputEmp) {
                selectedEmp = emp;
                break;
            }
        }
        if (selectedEmp == null) {
            System.out.println("Employee not found!");
            return;
        }

        // --- Total monthly deductions ---
        double monthlyGross = 0;
        for (int w = 0; w < 4; w++)
            monthlyGross += selectedEmp.monthlyHours[inputMonth - 1][w] * selectedEmp.hourlyRate;

        double totalSSS = Payroll.SSS(monthlyGross);
        double totalPhilHealth = Payroll.PhilHealth(monthlyGross);
        double totalPagIBIG = Payroll.PAGIBIG(monthlyGross);
        double totalWHT = Payroll.WithHoldingTax(monthlyGross - (totalSSS + totalPhilHealth + totalPagIBIG));

        // --- Selected week output ---
        double hoursWorked = selectedEmp.monthlyHours[inputMonth - 1][inputWeek];
        double weeklyGross = hoursWorked * selectedEmp.hourlyRate;

        System.out.println("------------------------------------------------");
        System.out.println("Employee #: " + selectedEmp.empNo);
        System.out.println("Name: " + selectedEmp.name);
        System.out.println("Birthday: " + selectedEmp.birthday);
        System.out.println("Month: " + inputMonth);
        System.out.println("Week: " + (inputWeek + 1));
        System.out.printf("Hours Worked: %.2f\n", hoursWorked);
        System.out.printf("Gross Salary: %.2f\n", weeklyGross);

        if (inputWeek < 3) {
            // Weeks 1-3: no deductions printed
            System.out.printf("Weekly Net Salary: %.2f\n", weeklyGross);
        } else {
            // Week 4: apply all monthly deductions
            double weeklyNet = weeklyGross - (totalSSS + totalPhilHealth + totalPagIBIG + totalWHT);
            System.out.printf("SSS: %.2f\n", totalSSS);
            System.out.printf("PhilHealth: %.2f\n", totalPhilHealth);
            System.out.printf("Pag-IBIG: %.2f\n", totalPagIBIG);
            System.out.printf("Withholding Tax: %.2f\n", totalWHT);
            System.out.printf("Total Deduction: %.2f\n", (totalSSS + totalPhilHealth + totalPagIBIG + totalWHT));
            System.out.printf("Weekly Net Salary: %.2f\n", weeklyNet);
        }
        System.out.println("------------------------------------------------");
    }

    // CSV parser
    public static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(String[]::new);
    }
}