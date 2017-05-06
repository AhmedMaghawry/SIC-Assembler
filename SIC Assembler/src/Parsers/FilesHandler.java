package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public void saveObjectFile(String[][] listing, String Object, String FileName) throws IOException {
        FileName = FileName.substring(0,FileName.length() - 4);
        File listingFile = new File("C:\\Users\\Ahmed Maghawry\\Documents\\Desktop\\output\\"+FileName+".txt");
        File objectFile = new File("C:\\Users\\Ahmed Maghawry\\Documents\\Desktop\\output\\"+FileName+".obj");
        createObj(objectFile, Object);
        createList(listingFile, listing);
    }

    private void createList(File listingFile, String[][] listing) throws IOException {
        FileWriter fw = new FileWriter(listingFile);
        for (int i = 0; i < listing.length; i++) {
            String x = listing[i][0] + "   " + makeItGood2(listing[i][1]) + "   " + listing[i][2];
            fw.write(x);
            fw.append(System.lineSeparator());
        }
        fw.close();
    }
    
    private String makeItGood2(String operandAddress) {
        String res = "";
        for (int i = 0; i < 6 - operandAddress.length(); i++) {
            res += " ";
        }
        res += operandAddress;
        return res;
    }

    private void createObj(File objectFile, String object) throws IOException {
        FileWriter fw = new FileWriter(objectFile);
        fw.write(object);
        fw.close();
    }

}
