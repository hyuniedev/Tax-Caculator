import com.example.taxcaculator.model.TaxCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTax {
    TaxCalculator calculator = new TaxCalculator();
    List<List<Integer>> lsData = LoadDataTesting.ReadData();


    private int TotalTax(int indexTestcase){
        int totalTax;
        List<Integer> ls = lsData.get(indexTestcase);
        int selfFee = ls.get(0);
        int dependentFee = ls.get(1);
        int dependent = ls.get(2);
        int totalLuong = 0;
        for (int i = 0; i < 12; i++) {
            totalLuong += ls.get(i + 3);
        }
        totalTax = (int) calculator.taxAnnual(totalLuong, selfFee, dependentFee, dependent);
        return totalTax;
    }

    private int TotalTaxMonthly(int indexTestcase){
        List<Integer> ls = lsData.get(indexTestcase);
        int selfFee = ls.get(0);
        int dependentFee = ls.get(1);
        int dependent = ls.get(2);
        int totalTaxMonth = 0;
        for(int i = 0; i < 12; i++) {
            totalTaxMonth += (int) calculator.taxMonthly(ls.get(i+3),selfFee, dependentFee, dependent);
        }
        return totalTaxMonth;
    }

    @Test
    void TestTotalTaxMonthly() {
        for (int i = 0; i < lsData.size(); i++) {
            assertEquals(lsData.get(i).get(16), TotalTaxMonthly(i));
        }
    }

    @Test
    void TestTotalTax() {
        for(int i = 0; i<lsData.size(); i++){
            assertEquals(lsData.get(i).get(17), TotalTax(i));
        }
    }

    @Test
    void TestTruyThu_TruyLinh(){
        for(int i = 0; i<lsData.size(); i++){
            assertEquals(lsData.get(i).get(18), TotalTax(i) - TotalTaxMonthly(i));
        }
    }
}
