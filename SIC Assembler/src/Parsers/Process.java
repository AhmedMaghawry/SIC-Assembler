package Parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.FileHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {

    ArrayList<String> code;
    String[][] intermediateFile;
    String[][] listingFile;
    String ObjectFile = "";
    Hashtable<String, Integer> OPTable;
    Hashtable<String, Integer> SYMTable;
    int LOCCRT;
    int startingAddress = 0;
    int progLenght;
    Converter convert;
    Pattern pattern;
    Matcher matcher;

    public Process(ArrayList<String> code) {
        this.code = code;
        convert = new Converter();
        OPTable = new Hashtable<String, Integer>();
        SYMTable = new Hashtable<String, Integer>();
        intermediateFile = new String[code.size()][2];
        listingFile = new String[code.size()][10];
        fillOPTable();
        prs1();
        prs2();
    }

    private void fillOPTable() {
        OPTable.put("add", 24);
        OPTable.put("and", 64);
        OPTable.put("comp", 40);
        OPTable.put("div", 36);
        OPTable.put("j", 60);
        OPTable.put("jeq", 48);
        OPTable.put("jgt", 52);
        OPTable.put("jlt", 56);
        OPTable.put("jsub", 72);
        OPTable.put("lda", 0);
        OPTable.put("ldch", 80);
        OPTable.put("ldl", 8);
        OPTable.put("ldx", 4);
        OPTable.put("mul", 32);
        OPTable.put("or", 68);
        OPTable.put("rd", 216);
        OPTable.put("rsub", 76);
        OPTable.put("sta", 12);
        OPTable.put("stch", 84);
        OPTable.put("stl", 20);
        OPTable.put("stx", 16);
        OPTable.put("sub", 28);
        OPTable.put("td", 224);
        OPTable.put("tix", 44);
        OPTable.put("wd", 220);
    }

    void prs1() {
        pattern = Pattern.compile("(?i)\\s*(\\w+)?\\s+(\\w+)\\s+(\\w+)");
        matcher = pattern.matcher(code.get(0).toLowerCase());
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                startingAddress = convert.hexaToDecimal(matcher.group(3));
                LOCCRT = startingAddress;
                intermediateFile[0][1] = code.get(0);
                intermediateFile[0][0] = convert.decimalToHexa(startingAddress);
            } else {
                LOCCRT = 0;
            }
        }
        for (int i = 1; i < code.size() - 1; i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).startsWith(".")) {
                    if (matcher.group(1) != null) {
                        if (SYMTable.contains(matcher.group(1).toLowerCase())) {
                            Exception e = new Exception("There is the same Symbole before");
                            e.printStackTrace();
                        } else {
                            SYMTable.put(matcher.group(1).toLowerCase(), LOCCRT);
                        }
                    }
                    String operation = matcher.group(2).toLowerCase();
                    if (OPTable.containsKey(operation)) {
                        LOCCRT += 3;
                    } else if (operation.equals("word")) {
                        LOCCRT += 3;
                    } else if (operation.equals("resw")) {
                        LOCCRT += 3 * Integer.parseInt(matcher.group(3));
                    } else if (operation.equals("resb")) {
                        LOCCRT += Integer.parseInt(matcher.group(3));
                    } else if (operation.equals("byte")) {
                        if (matcher.group(3).toLowerCase().startsWith("c")) {
                            String word = matcher.group(3).toLowerCase();
                            LOCCRT += word.length() - 3;
                        } else if (matcher.group(3).toLowerCase().startsWith("x")){
                            String word = matcher.group(3).toLowerCase();
                            LOCCRT += (word.length() - 3) / 2;
                        }
                    }else {
                        Exception e = new Exception("Invalid Operation Code");
                        e.printStackTrace();
                    }
                }
                intermediateFile[i][1] = code.get(i);
                intermediateFile[i][0] = convert.decimalToHexa(LOCCRT)+"";
            }
        }
        intermediateFile[code.size() - 1][1] = code.get(code.size() - 1);
        intermediateFile[code.size() - 1][0] = convert.decimalToHexa(LOCCRT)+"";
        progLenght = LOCCRT - startingAddress;
    }

    void prs2() {
        String firstLine = intermediateFile[0][1];
        matcher = pattern.matcher(firstLine);
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                listingFile[0][0] = intermediateFile[0][0];
                listingFile[0][1] = "";
                listingFile[0][2] = intermediateFile[0][1];
                listingFile[0][3] = "";
                listingFile[0][4] = "";
                listingFile[0][5] = "";
                listingFile[0][6] = "";
                listingFile[0][7] = "";
                listingFile[0][8] = "";
                listingFile[0][9] = "";
            }
            writeTheHeader(matcher.group(1), matcher.group(3), progLenght);
            intializeFirstTextrec(matcher.group(3), getContLength(1));
        }
        int counter = 0;
        for (int i = 1; i < code.size(); i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).startsWith(".")) {
                    String operation = matcher.group(2).toLowerCase();
                    //String sympole = (matcher.group(1) != null)? matcher.group(1).toLowerCase() : null;
                    String operand = matcher.group(3).toLowerCase();
                    if (OPTable.containsKey(operation)) {
                        String operandAddress;
                        if(operand != null) {
                            if (SYMTable.containsKey(operand)) {
                            listingFile[i][0] = intermediateFile[i][0];
                            listingFile[i][2] = intermediateFile[i][1];
                            operandAddress = convert.decimalToHexa(SYMTable.get(operand)) + "";
                            listingFile[i][3] = operandAddress;
                            listingFile[i][4] = 1 + "";
                            listingFile[i][5] = 1 + "";
                            listingFile[i][6] = 0 + "";
                            listingFile[i][7] = 0 + "";
                            listingFile[i][8] = 0 + "";
                            listingFile[i][9] = 0 + "";
                        } else {
                            operandAddress = "0";
                            Exception e = new Exception("Invalid Address");
                            e.printStackTrace();
                        }
                        }else {
                            listingFile[i][0] = intermediateFile[i][0];
                            listingFile[i][2] = intermediateFile[i][1];
                            operandAddress = "0";
                            listingFile[i][3] = operandAddress;
                            listingFile[i][4] = 1 + "";
                            listingFile[i][5] = 1 + "";
                            listingFile[i][6] = 0 + "";
                            listingFile[i][7] = 0 + "";
                            listingFile[i][8] = 0 + "";
                            listingFile[i][9] = 0 + "";
                        }
                        listingFile[i][1] = assembletheCode(intermediateFile[i + 1][0], operation, operandAddress);
                        counter++;
                    } else if (operation.equals("word") || operation.equals("byte")) {
                        listingFile[i][0] = intermediateFile[i][0];
                        listingFile[i][2] = intermediateFile[i][1];
                        listingFile[i][3] = "";
                        listingFile[i][4] = "";
                        listingFile[i][5] = "";
                        listingFile[i][6] = "";
                        listingFile[i][7] = "";
                        listingFile[i][8] = "";
                        listingFile[i][9] = "";
                        if (operation.equals("word")) {
                            listingFile[i][1] = convConstantWordToObjectCode(operand);
                        } else {
                            listingFile[i][1] = convConstantByteToObjectCode(operand);
                        }
                        counter++;
                    } else if (operation.equals("resw") || operation.equals("resb") || operation.equals("end")) {
                        listingFile[i][0] = intermediateFile[i][0];
                        listingFile[i][1] = "";
                        listingFile[i][2] = intermediateFile[i][1];
                        listingFile[i][3] = "";
                        listingFile[i][4] = "";
                        listingFile[i][5] = "";
                        listingFile[i][6] = "";
                        listingFile[i][7] = "";
                        listingFile[i][8] = "";
                        listingFile[i][9] = "";
                    }
                    if (counter > 10) {
                        counter = 0;
                        //writeTextRecordToObjectProg();
                        intializeFirstTextrec(matcher.group(3), getContLength(i));
                    }
                    addToObjectF(listingFile[i][1]);
                }
            }
        }
        //writeLastTextRectoObjPro();
        writeEndRectoObjPro();
    }
    
    private void addToObjectF(String objectCode) {
        for (int i = 0; i < 6 - objectCode.length(); i++) {
            ObjectFile += "0";
        }
        ObjectFile += objectCode;
    }

    private void writeEndRectoObjPro() {
        ObjectFile += "\n"+ "E";
        String startAdd = convert.decimalToHexa(startingAddress);
        for (int i = 0; i < 6 - startAdd.length(); i++) {
            ObjectFile += "0";
        }
        ObjectFile += startAdd;
    }

    private String convConstantWordToObjectCode(String operand) {
        return convert.decimalToHexa(Integer.parseInt(operand));
    }
    
    private String convConstantByteToObjectCode(String operand) {
        if(operand.startsWith("c")) {
            String value = operand.substring(2, operand.length() - 1);
            String res = "";
            for (int i = 0; i < value.length(); i++) {
                res += convert.decimalToHexa((int)value.charAt(i));
            }
        } else if (operand.startsWith("x")) {
            return operand.substring(2, operand.length() - 1);
        }
        return "";
    }

    private String assembletheCode(String currentAddress, String operation, String operandAddress) {
        String binarytemp = convert.decimalToBin(OPTable.get(operation));
        String binary = "";
        for(int i = 0; i < 8 - binarytemp.length(); i++) {
            binary += "0";
        }
        binary += binarytemp;
        binary = binary.substring(0, binary.length() - 2);
        binary += "110010";
        String res = "";
        String temp = binary.substring(0, 4);
        res += convert.binToHexa(temp);
        temp = binary.substring(4, 8);
        res += convert.binToHexa(temp);
        temp = binary.substring(8, 12);
        res += convert.binToHexa(temp);
        res += convert.subHexa(operandAddress, currentAddress);
        return res;
    }

    private int getContLength(int start) {
        int counter = 0;
        for (int i = start; i < start + 10; i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).startsWith(".")) {
                    String operation = matcher.group(2).toLowerCase();
                    if (OPTable.containsKey(operation) || operation.equals("word") || operation.equals("byte")) {
                        counter++;
                    } else {
                        break;
                    }
                }
            }
        }
        return counter * 3;
    }

    private void intializeFirstTextrec(String address, int len) {
        ObjectFile += "\n"+"T";
        for (int i = 0; i < 6 - address.length(); i++)
            ObjectFile += "0";
        ObjectFile += address;
        String hexaLen = convert.decimalToHexa(len);
        for (int i = 0; i < 2 - hexaLen.length(); i++)
            ObjectFile += "0";
        ObjectFile += hexaLen;
    }

    private void writeTheHeader(String name, String address, int length) {
        ObjectFile += "H" + name;
        String hexaLen = convert.decimalToHexa(length);
        for (int i = 0; i < 6 - name.length(); i++)
            ObjectFile += " ";
        for (int i = 0; i < 6 - address.length(); i++)
            ObjectFile += "0";
        ObjectFile += address;
        for (int i = 0; i < 6 - hexaLen.length(); i++)
            ObjectFile += "0";
        ObjectFile += hexaLen;
    }

    public static void main(String[] args) {
        FilesHandler file = new FilesHandler();
        ArrayList<String> code = file.readFile(new File("C:\\sic\\Exmpls\\dodo.txt"));
        for (String x : code)
            System.out.println(x);
        Process pross = new Process(code);
        String[][] y = pross.intermediateFile;
        for (int i = 0; i < y.length; i++) {
            System.out.println(y[i][0] + "   " + y[i][1]);
        }
        System.out.println("------------");
        System.out.println(pross.ObjectFile);
    }
}
