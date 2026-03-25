package motorphpayrollsystem2;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MotorPH {

    // ---------------- EMPLOYEE DATA ----------------
    static ArrayList<Integer> employeeNumbers = new ArrayList<>();          // store employee numbers
    static ArrayList<String> employeeNames = new ArrayList<>();            // store full names
    static ArrayList<String> employeeBirthdays = new ArrayList<>();        // store birthdays
    static ArrayList<String> employeeAddresses = new ArrayList<>();        // store addresses
    static ArrayList<String> employeePhones = new ArrayList<>();           // store phone numbers
    static ArrayList<String> employeeStatuses = new ArrayList<>();         // store employment status
    static ArrayList<String> employeePositions = new ArrayList<>();        // store position/job title
    static ArrayList<Double> employeeHourlyRates = new ArrayList<>();      // store hourly rates

    // attendance storage using maps (employee -> list of attendance records)
    static HashMap<Integer, List<Attendance>> attendanceMap = new HashMap<>();

    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy"); // format for reading attendance CSV
    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");     // format for time

    // ---------------- MAIN METHOD ----------------
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        loadEmployees();           // load employee data from CSV
        loadAttendance();          // load attendance data from CSV

        while (true) {
            // LOGIN SCREEN
            System.out.println("\n=== LOGIN ===");
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();

            // check login credentials
            if (user.equals("employee") && pass.equals("12345")) {
                employeeMenu(scanner);        // employee menu
            } else if (user.equals("payroll_staff") && pass.equals("12345")) {
                payrollMenu(scanner);         // payroll staff menu
            } else {
                System.out.println("Invalid login.");
            }
        }
    }

    // ---------------- ATTENDANCE RECORD ----------------
    static class Attendance {
        LocalDate date;         // date of attendance
        LocalTime timeIn;       // time in
        LocalTime timeOut;      // time out

        Attendance(LocalDate date, LocalTime in, LocalTime out) {
            this.date = date;
            this.timeIn = in;
            this.timeOut = out;
        }
    }

    // ---------------- LOAD EMPLOYEES ----------------
    public static void loadEmployees() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(MotorPH.class.getResourceAsStream("/employees.csv")))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = parseCSV(line);

                employeeNumbers.add(Integer.parseInt(d[0]));                  // employee #
                employeeNames.add(d[2] + " " + d[1]);                        // first + last name
                employeeBirthdays.add(d[3]);                                  // birthday
                employeeAddresses.add(d[4]);                                   // address
                employeePhones.add(d[5]);                                      // phone
                employeeStatuses.add(d[10]);                                   // status
                employeePositions.add(d[11]);                                  // position
                employeeHourlyRates.add(Double.parseDouble(d[18]));           // hourly rate
            }
        } catch (Exception e) {
            System.out.println("Error loading employees.csv: " + e.getMessage());
        }
    }

    // ---------------- LOAD ATTENDANCE ----------------
    public static void loadAttendance() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(MotorPH.class.getResourceAsStream("/attendance.csv")))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = parseCSV(line);
                int empNo = Integer.parseInt(d[0]);
                LocalDate date = LocalDate.parse(d[3], dateFormatter);
                LocalTime in = LocalTime.parse(d[4], timeFormatter);
                LocalTime out = LocalTime.parse(d[5], timeFormatter);

                Attendance att = new Attendance(date, in, out);
                attendanceMap.computeIfAbsent(empNo, k -> new ArrayList<>()).add(att); // add attendance to map
            }
        } catch (Exception e) {
            System.out.println("Error loading attendance.csv: " + e.getMessage());
        }
    }

    // ---------------- EMPLOYEE MENU ----------------
    public static void employeeMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== EMPLOYEE MENU ===");
            System.out.println("1. View Info");
            System.out.println("2. View Payslip");
            System.out.println("3. Logout");
            String choice = scanner.nextLine();

            if (choice.equals("3")) break;

            System.out.print("Enter Employee #: ");
            int empNo = Integer.parseInt(scanner.nextLine());
            int index = employeeNumbers.indexOf(empNo);
            if (index == -1) {
                System.out.println("Employee not found.");
                continue;
            }

            if (choice.equals("1")) displayEmployeeInfo(index);
            else generatePayslip(empNo, index); // generate payslip
        }
    }

    // ---------------- PAYROLL STAFF MENU ----------------
    public static void payrollMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== PAYROLL STAFF MENU ===");
            System.out.println("1. Process One Employee");
            System.out.println("2. Process All Employees");
            System.out.println("3. View All Employees");
            System.out.println("4. Logout");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter Employee #: ");
                    int empNo = Integer.parseInt(scanner.nextLine());
                    int index = employeeNumbers.indexOf(empNo);
                    if (index != -1) generatePayslip(empNo, index);
                }
                case "2" -> {
                    for (int i = 0; i < employeeNumbers.size(); i++) {
                        generatePayslip(employeeNumbers.get(i), i);
                    }
                }
                case "3" -> viewAllEmployees();
                case "4" -> { return; }
            }
        }
    }

    // ---------------- DISPLAY EMPLOYEE INFO ----------------
    public static void displayEmployeeInfo(int i) {
        System.out.println("\n========== EMPLOYEE INFORMATION ==========");
        System.out.println("Employee #: " + employeeNumbers.get(i));
        System.out.println("Name      : " + employeeNames.get(i));
        System.out.println("Birthday  : " + employeeBirthdays.get(i));
        System.out.println("Address   : " + employeeAddresses.get(i));
        System.out.println("Phone     : " + employeePhones.get(i));
        System.out.println("Status    : " + employeeStatuses.get(i));
        System.out.println("Position  : " + employeePositions.get(i));
        System.out.printf("Hourly Rate: %.2f\n", employeeHourlyRates.get(i));
        System.out.println("==========================================");
    }

    // ---------------- VIEW ALL EMPLOYEES ----------------
    public static void viewAllEmployees() {
        for (int i = 0; i < employeeNumbers.size(); i++) displayEmployeeInfo(i);
    }

    // ---------------- GENERATE PAYSLIP ----------------
    public static void generatePayslip(int empNo, int index) {

        System.out.println("\n=================================================");
        System.out.println("                MOTORPH PAYSLIP");
        System.out.println("=================================================");

        System.out.println("Employee #: " + employeeNumbers.get(index));
        System.out.println("Name      : " + employeeNames.get(index));
        System.out.println("Position  : " + employeePositions.get(index));
        System.out.printf("Hourly Rate: %.2f\n", employeeHourlyRates.get(index));

        double monthlyGross = 0;      // sum of cutoffs
        double monthlyDeduction = 0;  // deductions applied only once in second cutoff

        // loop months June(6) to December(12)
        for (int month = 6; month <= 12; month++) {

            double[] cutoffHours = new double[2]; // 0 = first cutoff, 1 = second cutoff
            double[] cutoffGross = new double[2];

            // loop two cutoffs per month
            for (int w = 0; w < 2; w++) {
                int startDay = (w == 0) ? 1 : 16;
                int endDay = (w == 0) ? 15 : getLastDayOfMonth(month);

                cutoffHours[w] = calculateHoursForPeriod(empNo, month, startDay, endDay);
                cutoffGross[w] = cutoffHours[w] * employeeHourlyRates.get(index);
            }

            monthlyGross = cutoffGross[0] + cutoffGross[1]; // total gross

            // compute deductions for the whole month (applied in second cutoff)
            double sss = SSS(monthlyGross);
            double phil = PhilHealth(monthlyGross);
            double pagibig = PAGIBIG(monthlyGross);
            double tax = WithholdingTax(monthlyGross - (sss + phil + pagibig));
            monthlyDeduction = sss + phil + pagibig + tax;

            // OUTPUT
            System.out.println("\n-------------------------------------------------");
            System.out.println("Month: " + Month.of(month));
            System.out.println("-------------------------------------------------");

            // first cutoff: no deduction
            System.out.printf("Cutoff 1 (1-15)\n");
            System.out.printf("  Hours Worked : %8.2f\n", cutoffHours[0]);
            System.out.printf("  Gross Pay    : %8.2f\n", cutoffGross[0]);
            System.out.printf("  Net Pay      : %8.2f\n", cutoffGross[0]);

            // second cutoff: deductions applied
            double net2 = cutoffGross[1] - monthlyDeduction;
            if (net2 < 0) net2 = 0;
            System.out.printf("\nCutoff 2 (16-end)\n");
            System.out.printf("  Hours Worked : %8.2f\n", cutoffHours[1]);
            System.out.printf("  Gross Pay    : %8.2f\n", cutoffGross[1]);
            System.out.println("  Deductions:");
            System.out.printf("    SSS        : %8.2f\n", sss);
            System.out.printf("    PhilHealth : %8.2f\n", phil);
            System.out.printf("    Pag-IBIG   : %8.2f\n", pagibig);
            System.out.printf("    Withholding: %8.2f\n", tax);
            System.out.printf("    Total      : %8.2f\n", monthlyDeduction);
            System.out.printf("  Net Pay      : %8.2f\n", net2);

            System.out.println("\nSUMMARY:");
            System.out.printf("  Monthly Gross : %8.2f\n", monthlyGross);
            System.out.printf("  Total Deduct. : %8.2f\n", monthlyDeduction);
            System.out.printf("  Monthly Net   : %8.2f\n", cutoffGross[0] + net2);
        }
        System.out.println("\n=================================================\n");
    }

    // ---------------- CALCULATE HOURS FOR A PERIOD ----------------
    public static double calculateHoursForPeriod(int empNo, int month, int startDay, int endDay) {
        List<Attendance> records = attendanceMap.get(empNo);
        if (records == null) return 0;
        double total = 0;
        for (Attendance att : records) {
            if (att.date.getMonthValue() != month) continue;
            int day = att.date.getDayOfMonth();
            if (day < startDay || day > endDay) continue;
            total += calculateDailyHours(att.timeIn, att.timeOut);
        }
        return total;
    }

    // ---------------- CALCULATE DAILY HOURS ----------------
    public static double calculateDailyHours(LocalTime in, LocalTime out) {

        LocalTime standardStart = LocalTime.of(8, 0);   // work start
        LocalTime graceEnd = LocalTime.of(8, 10);       // 10-minute grace
        LocalTime standardEnd = LocalTime.of(17, 0);    // work end

        // apply grace period
        if (!in.isAfter(graceEnd)) in = standardStart;   // early or within 10min grace counted as 8:00
        if (out.isAfter(standardEnd)) out = standardEnd; // cannot count after 5PM

        if (out.isBefore(in)) return 0;

        double hours = Duration.between(in, out).toMinutes() / 60.0;

        if (hours > 4) hours -= 1; // lunch break deduction

        return hours;
    }

    // ---------------- GET LAST DAY OF MONTH ----------------
    public static int getLastDayOfMonth(int month) {
        return switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> 28;
            default -> 31;
        };
    }

    // ---------------- CSV PARSER ----------------
    public static String[] parseCSV(String line) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean quote = false;
        for (char c : line.toCharArray()) {
            if (c == '"') quote = !quote;
            else if (c == ',' && !quote) { list.add(sb.toString()); sb.setLength(0);}
            else sb.append(c);
        }
        list.add(sb.toString());
        return list.toArray(String[]::new);
    }

    // ---------------- DEDUCTION FORMULAS ----------------
    public static double SSS(double gross) { // SSS based on gross
        if (gross < 3250) return 135;
        else if (gross < 3750) return 157.5;
        else if (gross < 4250) return 180;
        else if (gross < 4750) return 202.5;
        else if (gross < 5250) return 225;
        else if (gross < 5750) return 247.5;
        else if (gross < 6250) return 270;
        else if (gross < 6750) return 292.5;
        else if (gross < 7250) return 315;
        else if (gross < 7750) return 337.5;
        else if (gross < 8250) return 360;
        else if (gross < 8750) return 382.5;
        else if (gross < 9250) return 405;
        else if (gross < 9750) return 427.5;
        else if (gross < 10250) return 450;
        else if (gross < 10750) return 472.5;
        else if (gross < 11250) return 495;
        else if (gross < 11750) return 517.5;
        else if (gross < 12250) return 540;
        else if (gross < 12750) return 562.5;
        else if (gross < 13250) return 585;
        else if (gross < 13750) return 607.5;
        else if (gross < 14250) return 630;
        else if (gross < 14750) return 652.5;
        else if (gross < 15250) return 675;
        else if (gross < 15750) return 697.5;
        else if (gross < 16250) return 720;
        else if (gross < 16750) return 742.5;
        else if (gross < 17250) return 765;
        else if (gross < 17750) return 787.5;
        else if (gross < 18250) return 810;
        else if (gross < 18750) return 832.5;
        else if (gross < 19250) return 855;
        else if (gross < 19750) return 877.5;
        else if (gross < 20250) return 900;
        else if (gross < 20750) return 922.5;
        else if (gross < 21250) return 945;
        else if (gross < 21750) return 967.5;
        else if (gross < 22250) return 990;
        else if (gross < 22750) return 1012.5;
        else if (gross < 23250) return 1035;
        else if (gross < 23750) return 1057.5;
        else if (gross < 24250) return 1080;
        else if (gross < 24750) return 1102.5;
        else return 1125;
    }

    public static double PhilHealth(double gross) {
        double total = gross * 0.03;
        if (total < 300) total = 300;
        if (total > 1800) total = 1800;
        return total / 2;
    }

    public static double PAGIBIG(double gross) {
        double share = (gross <= 1500) ? gross * 0.01 : gross * 0.02;
        return Math.min(share, 100);
    }

    public static double WithholdingTax(double taxable) {
        if (taxable <= 20832) return 0;
        else if (taxable <= 33332) return (taxable - 20833) * 0.20;
        else if (taxable <= 66666) return 2500 + (taxable - 33333) * 0.25;
        else if (taxable <= 166666) return 10833 + (taxable - 66667) * 0.30;
        else if (taxable <= 666666) return 40833.33 + (taxable - 166667) * 0.32;
        else return 200833.33 + (taxable - 666667) * 0.35;
    }
}
