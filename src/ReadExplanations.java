import com.sun.deploy.net.MessageHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadExplanations {

    private final ArrayList<String> allLines = new ArrayList<>();

    public ReadExplanations() {

        String filePath = "Explanations.txt";

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.length() == 0 ) continue;
                allLines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    protected String getLine(int num) {return allLines.get(num);}
}