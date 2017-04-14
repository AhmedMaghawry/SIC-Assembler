package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FilesHandler {
    
    private BufferedReader b;

    public ArrayList<String> readFile(File io) {
        ArrayList<String> result = new ArrayList<>();
        try {
            b = new BufferedReader(new FileReader(io));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                result.add(readLine);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    public void saveObjectFile(String value) {
        
    }

}
