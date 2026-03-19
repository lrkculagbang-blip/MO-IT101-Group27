package motorphpayrollsystem2;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class MotorPH {

    // --- Data for employees ---
    static ArrayList<Integer> empNos = new ArrayList<>();      // store employee numbers
    static ArrayList<String> empNames = new ArrayList<>();     // store employee full names
    static ArrayList<String> empBirthdays = new ArrayList<>(); // store birthdays
    static ArrayList<Double> empRates = new ArrayList<>();     // store hourly rates
    static double[][][] monthlyHours; // 3D array to store hours [employee][month][week]

    // --- Utility: parse CSV line ---
    public static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false; // check if we are inside quotes
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes; // toggle quotes
            else if (c == ',' && !inQuotes) {   // comma outside quotes means new field
                tokens.add(sb.toString());      // save current field
                sb.setLength(0);                // reset string builder
            } else sb.append(c); // add char to current field
        }
        tokens.add(sb.toString()); // add last field
        return tokens.toArray(String[]::new);
    }

    // --- Calculate hours worked from time strings ---
    public static double calculateHoursWorked(String timeIn, String timeOut) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime in = LocalTime.parse(timeIn, formatter);  // convert string to time
            LocalTime out = LocalTime.parse(timeOut, formatter);
            Duration duration = Duration.between(in, out);     // get difference
            return duration.toMinutes() / 60.0;                // convert minutes to hours
        } catch (Exception e) {
            System.out.println("Error parsing time: " + timeIn + " - " + timeOut);
            return 0; // return 0 if error
        }
    }

    // --- Payroll Methods ---
    public static double SSS(double gross) {
        // calculate SSS contribution based on gross salary
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
        double total = gross * 0.03;   // 3% of gross
        if (total < 300) total = 300;  // min contribution
        if (total > 1800) total = 1800; // max contribution
        return total / 2; // employee share
    }

    public static double PAGIBIG(double gross) {
        double share = (gross <= 1500) ? gross * 0.01 : gross * 0.02; // 1% or 2%
        return Math.min(share, 100); // max 100
    }

    public static double WithholdingTax(double taxable) {
        // calculate tax based on taxable income
        if (taxable <= 20832) return 0;
        else if (taxable <= 33332) return (taxable - 20833) * 0.20;
        else if (taxable <= 66666) return 2500 + (taxable - 33333) * 0.25;
        else if (taxable <= 166666) return 10833 + (taxable - 66667) * 0.30;
        else if (taxable <= 666666) return 40833.33 + (taxable - 166667) * 0.32;
        else return 200833.33 + (taxable - 666667) * 0.35;
    }

    // --- Main ---
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // --- Load employee data from CSV ---
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(MotorPH.class.getResourceAsStream("employees.csv"))
        )) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                empNos.add(Integer.valueOf(data[0].trim())); // emp number
                empNames.add(data[2].trim() + " " + data[1].trim()); // first + last name
                empBirthdays.add(data[3].trim()); // birthday
                empRates.add(Double.valueOf(data[18].trim().replace("\"","").replace(",", ""))); // hourly rate
            }
        } catch (Exception e) {
            System.out.println("Error reading employees.csv"); // error msg
            return;
        }

        int numEmployees = empNos.size();
        monthlyHours = new double[numEmployees][12][4]; // init 3D array for hours

        // --- Load attendance data from CSV ---
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(MotorPH.class.getResourceAsStream("attendance.csv"))
        )) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                int empNo = Integer.parseInt(data[0].trim());
                int index = empNos.indexOf(empNo); // find employee index
                if (index == -1) continue; // skip if not found

                // figure out month and week
                String date = data[3].trim();
                int month = Integer.parseInt(date.split("/")[0]) - 1; // month 0-11
                int day = Integer.parseInt(date.split("/")[1]);
                int week = Math.min((day - 1) / 7, 3); // week 0-3

                // calculate hours worked and add to array
                double hours = calculateHoursWorked(data[4].trim(), data[5].trim());
                monthlyHours[index][month][week] += hours;
            }
        } catch (Exception e) {
            System.out.println("Error reading attendance.csv");
            return;
        }

        // --- User input ---
        System.out.print("Enter Employee Number: ");
        int inputEmp = sc.nextInt();
        int empIndex = empNos.indexOf(inputEmp);
        if (empIndex == -1) {
            System.out.println("Employee not found!");
            return;
        }

        System.out.print("Enter Month (1-12): ");
        int inputMonth = sc.nextInt() - 1;
        System.out.print("Enter Week Number (1-4): ");
        int inputWeek = sc.nextInt() - 1;

        // --- Calculate weekly and monthly gross ---
        double hoursWorked = monthlyHours[empIndex][inputMonth][inputWeek];       // hours this week
        double weeklyGross = hoursWorked * empRates.get(empIndex);                // gross for week

        double monthlyGross = 0;
        for (int w = 0; w < 4; w++)
            monthlyGross += monthlyHours[empIndex][inputMonth][w] * empRates.get(empIndex); // sum month

        // --- Calculate deductions ---
        double totalSSS = SSS(monthlyGross);
        double totalPhilHealth = PhilHealth(monthlyGross);
        double totalPAGIBIG = PAGIBIG(monthlyGross);
        double totalWHT = WithholdingTax(monthlyGross - (totalSSS + totalPhilHealth + totalPAGIBIG));

        // --- Print output ---
        System.out.println("------------------------------------------------");
        System.out.println("Employee #: " + empNos.get(empIndex));
        System.out.println("Name: " + empNames.get(empIndex));
        System.out.println("Birthday: " + empBirthdays.get(empIndex));
        System.out.println("Month: " + (inputMonth + 1));
        System.out.println("Week: " + (inputWeek + 1));
        System.out.printf("Hours Worked: %.2f\n", hoursWorked);
        System.out.printf("Gross Salary: %.2f\n", weeklyGross);

        if (inputWeek < 3) { // only print gross for weeks 1-3
            System.out.printf("Weekly Net Salary: %.2f\n", weeklyGross);
        } else { // week 4, print full deductions
            double weeklyNet = weeklyGross - (totalSSS + totalPhilHealth + totalPAGIBIG + totalWHT);
            System.out.printf("SSS: %.2f\n", totalSSS);
            System.out.printf("PhilHealth: %.2f\n", totalPhilHealth);
            System.out.printf("Pag-IBIG: %.2f\n", totalPAGIBIG);
            System.out.printf("Withholding Tax: %.2f\n", totalWHT);
            System.out.printf("Total Deduction: %.2f\n", (totalSSS + totalPhilHealth + totalPAGIBIG + totalWHT));
            System.out.printf("Weekly Net Salary: %.2f\n", weeklyNet);
        }
        System.out.println("------------------------------------------------");
    }
}