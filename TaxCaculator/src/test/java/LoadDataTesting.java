import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoadDataTesting {
    private static String NAME_FILE_DATA = "data.csv";
    private static String FILE_PATH_DATA = "D:/MyCode/Tax-Caculator/TaxCaculator/target/test-classes/data.csv";
    public static List<List<Integer>> ReadData(){
        List<List<Integer>> data = new ArrayList<>();
        try {
            // Đọc tất cả các dòng từ file
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH_DATA));

            for (String line : lines) {
                String[] values = line.split(",");

                List<Integer> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Integer.parseInt(value.trim()));
                }

                data.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }

        return data;
    }
}
