package com.example.taxcaculator.model;

public class TaxCalculator {
    public double taxMonthly(int monthlySalary, int dependent, int year) {
        YearFee.getFee(year);
        int selfFee = YearFee.getSelfFee();
        int dependentFee = YearFee.getDependentFee();

        int TNTT = monthlySalary - selfFee - (dependentFee * dependent);
        double tax = 0;

        // Tính thuế theo các bậc thuế
        if (TNTT > 0) {
            if (TNTT <= 5000000) { // Bậc 1
                tax = TNTT * 0.05;
            } else if (TNTT <= 10000000) { // Bậc 2
                tax = 250000 + (TNTT - 5000000) * 0.1;
            } else if (TNTT <= 18000000) { // Bậc 3
                tax = 750000 + (TNTT - 10000000) * 0.15;
            } else if (TNTT <= 32000000) { // Bậc 4
                tax = 1950000 + (TNTT - 18000000) * 0.2;
            } else if (TNTT <= 52000000) { // Bậc 5
                tax = 4750000 + (TNTT - 32000000) * 0.25;
            } else if (TNTT <= 80000000) { // Bậc 6
                tax = 9750000 + (TNTT - 52000000) * 0.3;
            } else { // Bậc 7
                tax = 18150000 + (TNTT - 80000000) * 0.35;
            }
        }
        return tax;
    }

    public double taxAnnual(int annualSalary, int dependent, int year) {
        YearFee.getFee(year);
        int selfFee = YearFee.getSelfFee() * 12;
        int dependentFee = YearFee.getDependentFee() * 12;

        int TNTT = annualSalary - selfFee - (dependentFee * dependent);
        double tax = 0;


        if (TNTT > 0) {
            if (TNTT <= 60000000) { // Bậc 1
                tax = TNTT * 0.05;
            } else if (TNTT <= 120000000) { // Bậc 2
                tax = 3000000 + (TNTT - 60000000) * 0.1;
            } else if (TNTT <= 216000000) { // Bậc 3
                tax = 9000000 + (TNTT - 120000000) * 0.15;
            } else if (TNTT <= 384000000) { // Bậc 4
                tax = 24000000 + (TNTT - 216000000) * 0.2;
            } else if (TNTT <= 624000000) { // Bậc 5
                tax = 60000000 + (TNTT - 384000000) * 0.25;
            } else if (TNTT <= 960000000) { // Bậc 6
                tax = 120000000 + (TNTT - 624000000) * 0.3;
            } else { // Bậc 7
                tax = 240000000 + (TNTT - 960000000) * 0.35;
            }
        }
        return tax;
    }

    //--------------------------------------------------------------------------------------------------
    public double taxAnnual(int annualSalary, int selfFee, int dependentFee, int dependent) {
        selfFee *= 12;
        dependentFee *= 12;

        int TNTT = annualSalary - selfFee - (dependentFee * dependent);
        double tax = 0;

        if (TNTT > 0) {
            if (TNTT <= 60000000) { // Bậc 1
                tax = TNTT * 0.05;
            } else if (TNTT <= 120000000) { // Bậc 2
                tax = 3000000 + (TNTT - 60000000) * 0.1;
            } else if (TNTT <= 216000000) { // Bậc 3
                tax = 9000000 + (TNTT - 120000000) * 0.15;
            } else if (TNTT <= 384000000) { // Bậc 4
                tax = 24000000 + (TNTT - 216000000) * 0.2;
            } else if (TNTT <= 624000000) { // Bậc 5
                tax = 60000000 + (TNTT - 384000000) * 0.25;
            } else if (TNTT <= 960000000) { // Bậc 6
                tax = 120000000 + (TNTT - 624000000) * 0.3;
            } else { // Bậc 7
                tax = 240000000 + (TNTT - 960000000) * 0.35;
            }
        }
        return tax;
    }
    public double taxMonthly(int monthlySalary, int selfFee, int dependentFee, int dependent) {
        int TNTT = monthlySalary - selfFee - (dependentFee * dependent);
        double tax = 0;

        // Tính thuế theo các bậc thuế
        if (TNTT > 0) {
            if (TNTT <= 5000000) { // Bậc 1
                tax = TNTT * 0.05;
            } else if (TNTT <= 10000000) { // Bậc 2
                tax = 250000 + (TNTT - 5000000) * 0.1;
            } else if (TNTT <= 18000000) { // Bậc 3
                tax = 750000 + (TNTT - 10000000) * 0.15;
            } else if (TNTT <= 32000000) { // Bậc 4
                tax = 1950000 + (TNTT - 18000000) * 0.2;
            } else if (TNTT <= 52000000) { // Bậc 5
                tax = 4750000 + (TNTT - 32000000) * 0.25;
            } else if (TNTT <= 80000000) { // Bậc 6
                tax = 9750000 + (TNTT - 52000000) * 0.3;
            } else { // Bậc 7
                tax = 18150000 + (TNTT - 80000000) * 0.35;
            }
        }
        return tax;
    }
}
