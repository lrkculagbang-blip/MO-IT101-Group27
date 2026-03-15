package motorphpayrollsystem2;

public class Payroll {

    // SSS Contribution
    public static double SSS(double GrossSalary){
        if(GrossSalary<3250) return 135.00;
        else if(GrossSalary>=3250 && GrossSalary<3750) return 157.50;
        else if(GrossSalary>=3750 && GrossSalary<4250) return 180.00;
        else if(GrossSalary>=4250 && GrossSalary<4750) return 202.50;
        else if(GrossSalary>=4750 && GrossSalary<5250) return 225.00;
        else if(GrossSalary>=5250 && GrossSalary<5750) return 247.50;
        else if(GrossSalary>=5750 && GrossSalary<6250) return 270.00;
        else if(GrossSalary>=6250 && GrossSalary<6750) return 292.50;
        else if(GrossSalary>=6750 && GrossSalary<7250) return 315.00;
        else if(GrossSalary>=7250 && GrossSalary<7750) return 337.50;
        else if(GrossSalary>=7750 && GrossSalary<8250) return 360.00;
        else if(GrossSalary>=8250 && GrossSalary<8750) return 382.50;
        else if(GrossSalary>=8750 && GrossSalary<9250) return 405.00;
        else if(GrossSalary>=9250 && GrossSalary<9750) return 427.50;
        else if(GrossSalary>=9750 && GrossSalary<10250) return 450.00;
        else if(GrossSalary>=10250 && GrossSalary<10750) return 472.50;
        else if(GrossSalary>=10750 && GrossSalary<11250) return 495.00;
        else if(GrossSalary>=11250 && GrossSalary<11750) return 517.50;
        else if(GrossSalary>=11750 && GrossSalary<12250) return 540.00;
        else if(GrossSalary>=12250 && GrossSalary<12750) return 562.50;
        else if(GrossSalary>=12750 && GrossSalary<13250) return 585.00;
        else if(GrossSalary>=13250 && GrossSalary<13750) return 607.50;
        else if(GrossSalary>=13750 && GrossSalary<14250) return 630.00;
        else if(GrossSalary>=14250 && GrossSalary<14750) return 652.50;
        else if(GrossSalary>=14750 && GrossSalary<15250) return 675.00;
        else if(GrossSalary>=15250 && GrossSalary<15750) return 697.50;
        else if(GrossSalary>=15750 && GrossSalary<16250) return 720.00;
        else if(GrossSalary>=16250 && GrossSalary<16750) return 742.50;
        else if(GrossSalary>=16750 && GrossSalary<17250) return 765.00;
        else if(GrossSalary>=17250 && GrossSalary<17750) return 787.50;
        else if(GrossSalary>=17750 && GrossSalary<18250) return 810.00;
        else if(GrossSalary>=18250 && GrossSalary<18750) return 832.50;
        else if(GrossSalary>=18750 && GrossSalary<19250) return 855.00;
        else if(GrossSalary>=19250 && GrossSalary<19750) return 877.50;
        else if(GrossSalary>=19750 && GrossSalary<20250) return 900.00;
        else if(GrossSalary>=20250 && GrossSalary<20750) return 922.50;
        else if(GrossSalary>=20750 && GrossSalary<21250) return 945.00;
        else if(GrossSalary>=21250 && GrossSalary<21750) return 967.50;
        else if(GrossSalary>=21750 && GrossSalary<22250) return 990.00;
        else if(GrossSalary>=22250 && GrossSalary<22750) return 1012.50;
        else if(GrossSalary>=22750 && GrossSalary<23250) return 1035.00;
        else if(GrossSalary>=23250 && GrossSalary<23750) return 1057.50;
        else if(GrossSalary>=23750 && GrossSalary<24250) return 1080.00;
        else if(GrossSalary>=24250 && GrossSalary<24750) return 1102.50;
        else return 1125.00;
    }

    // PhilHealth
    public static double PhilHealth(double GrossSalary){
        double totalPremium = GrossSalary * 0.03;
        if(totalPremium < 300) totalPremium = 300;
        if(totalPremium > 1800) totalPremium = 1800;
        return totalPremium / 2; // E-share
    }

    // Pag-IBIG
    public static double PAGIBIG(double GrossSalary){
        double share = (GrossSalary <= 1500) ? GrossSalary * 0.01 : GrossSalary * 0.02;
        if(share > 100) share = 100;
        return share;
    }

    // Withholding Tax
    public static double WithHoldingTax(double taxableIncome){
        if(taxableIncome <= 20832) return 0;
        else if(taxableIncome <= 33332) return (taxableIncome-20833) * 0.20;
        else if(taxableIncome <= 66666) return 2500 + (taxableIncome-33333) * 0.25;
        else if(taxableIncome <= 166666) return 10833 + (taxableIncome-66667) * 0.30;
        else if(taxableIncome <= 666666) return 40833.33 + (taxableIncome-166667) * 0.32;
        else return 200833.33 + (taxableIncome-666667) * 0.35;
    }

    // Total Deduction
    public static double TotalDeduction(double grossMonthly){
        double sss = SSS(grossMonthly);
        double ph = PhilHealth(grossMonthly);
        double pi = PAGIBIG(grossMonthly);
        double taxable = grossMonthly - (sss + ph + pi);
        double wht = WithHoldingTax(taxable);
        return sss + ph + pi + wht;
    }
}
