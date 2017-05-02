package Parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {

    ArrayList<String> code;
    int start = 0;
    boolean isIndex = false;
    String[][] intermediateFile;
    String[][] listingFile;
    String ObjectFile = "";
    Hashtable<String, String> OPTable;
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
        OPTable = new Hashtable<String, String>();
        SYMTable = new Hashtable<String, Integer>();
        intermediateFile = new String[code.size()][2];
        listingFile = new String[code.size()][3];
        fillOPTable();
        prs1();
        prs2();
        makeListingGood();
    }

    private void makeListingGood() {
        for(int i = 0; i < listingFile.length; i++) {
            if (listingFile[i][0] == null)
                listingFile[i][0] = "";
            if (listingFile[i][1] == null)
                listingFile[i][1] = "";
            if (listingFile[i][2] == null)
                listingFile[i][2] = "";
        }
    }

    private void fillOPTable() {
        OPTable.put("add", "24");
        OPTable.put("and", "64");
        OPTable.put("comp", "40");
        OPTable.put("div", "36");
        OPTable.put("j", "60");
        OPTable.put("jeq", "48");
        OPTable.put("jgt", "52");
        OPTable.put("jlt", "56");
        OPTable.put("jsub", "72");
        OPTable.put("lda", "00");
        OPTable.put("ldch", "80");
        OPTable.put("ldl", "08");
        OPTable.put("ldx", "04");
        OPTable.put("mul", "32");
        OPTable.put("or", "68");
        OPTable.put("rd", "216");
        OPTable.put("rsub", "76");
        OPTable.put("sta", "12");
        OPTable.put("stch", "84");
        OPTable.put("stl", "20");
        OPTable.put("stx", "16");
        OPTable.put("sub", "28");
        OPTable.put("td", "224");
        OPTable.put("tix", "44");
        OPTable.put("wd", "220");
    }

    void prs1() {
        while (code.get(start).contains(".")) {
            intermediateFile[start][1] = code.get(start);
            intermediateFile[start][0] = "";
            start++;
        }
        pattern = Pattern.compile("(?i)\\s*(\\w+)?\\s+(\\w+)\\s+(\\w+)");
        matcher = pattern.matcher(code.get(start).toLowerCase());
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                startingAddress = convert.hexaToDecimal(matcher.group(3));
                LOCCRT = startingAddress;
                intermediateFile[start][1] = code.get(start);
                intermediateFile[start][0] = makeGoodShape(convert.decimalToHexa(startingAddress));
            } else {
                LOCCRT = 0;
            }
        }
        for (int i = start + 1; i < code.size() - 1; i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).contains(".")) {
                    intermediateFile[i][1] = code.get(i);
                    intermediateFile[i][0] = makeGoodShape(convert.decimalToHexa(LOCCRT) + "");
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
                        } else if (matcher.group(3).toLowerCase().startsWith("x")) {
                            String word = matcher.group(3).toLowerCase();
                            LOCCRT += (word.length() - 3) / 2;
                        }
                    } else {
                        Exception e = new Exception("Invalid Operation Code");
                        e.printStackTrace();
                    }
                } else {
                    intermediateFile[i][1] = code.get(i);
                    intermediateFile[i][0] = "";
                }
            } else {
                intermediateFile[i][1] = code.get(i);
                intermediateFile[i][0] = "";
            }
        }
        intermediateFile[code.size() - 1][1] = code.get(code.size() - 1);
        intermediateFile[code.size() - 1][0] = makeGoodShape(convert.decimalToHexa(LOCCRT) + "");
        progLenght = LOCCRT - startingAddress;
    }

    private String makeGoodShape(String string) {
        String res = "";
        for (int i = 0; i < 6 - string.length(); i++) {
            res += "0";
        }
        res += string;
        return res;
    }

    void prs2() {
        String firstLine = intermediateFile[start][1];
        matcher = pattern.matcher(firstLine);
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                listingFile[start][0] = intermediateFile[start][0];
                listingFile[start][1] = "";
                listingFile[start][2] = intermediateFile[start][1];
            }
            writeTheHeader(matcher.group(1), matcher.group(3), progLenght - 1);
            intializeFirstTextrec(start + 1, matcher.group(2).toLowerCase(), matcher.group(3));
        }
        int counter = 0;
        for (int i = start + 1; i < code.size(); i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).startsWith(".")) {
                    String operation = matcher.group(2).toLowerCase();
                    // String sympole = (matcher.group(1) != null)?
                    // matcher.group(1).toLowerCase() : null;
                    String operand = matcher.group(3).toLowerCase();
                    if (OPTable.containsKey(operation)) {
                        String operandAddress;
                        if (operand != null) {
                            if (operand.toLowerCase().contains(",x")) {
                                isIndex = true;
                            }
                            if (SYMTable.containsKey(operand)) {
                                listingFile[i][0] = intermediateFile[i][0];
                                listingFile[i][2] = intermediateFile[i][1];
                                operandAddress = convert.decimalToHexa(SYMTable.get(operand)) + "";
                            } else if (operand.toLowerCase().contains("0x")) {
                                operandAddress = operand.substring(2, operand.length());
                            } else {
                                operandAddress = "0";
                                Exception e = new Exception("Invalid Address");
                                e.printStackTrace();
                            }
                        } else {
                            listingFile[i][0] = intermediateFile[i][0];
                            listingFile[i][2] = intermediateFile[i][1];
                            operandAddress = "0";
                        }
                        listingFile[i][1] = assembletheCode(operation, operandAddress);
                        counter++;
                    } else if (operation.equals("word") || operation.equals("byte")) {
                        listingFile[i][0] = intermediateFile[i][0];
                        listingFile[i][2] = intermediateFile[i][1];
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
                        counter++;
                    }
                    if (counter >= 10) {
                        counter = 0;
                        // writeTextRecordToObjectProg();
                        intializeFirstTextrec(i, matcher.group(2).toLowerCase(), intermediateFile[i][0]);
                    }
                    addToObjectF(listingFile[i][1]);
                }
            }
        }
        // writeLastTextRectoObjPro();
        writeEndRectoObjPro();
    }

    private void addToObjectF(String objectCode) {
        if (!objectCode.equals("")) {
            for (int i = 0; i < 6 - objectCode.length(); i++) {
                ObjectFile += "0";
            }
            ObjectFile += objectCode;
        }
    }

    private void writeEndRectoObjPro() {
        ObjectFile += "\n" + "E";
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
        if (operand.startsWith("c")) {
            String value = operand.substring(2, operand.length() - 1);
            String res = "";
            for (int i = 0; i < value.length(); i++) {
                res += convert.decimalToHexa((int) value.charAt(i));
            }
        } else if (operand.startsWith("x")) {
            return operand.substring(2, operand.length() - 1);
        }
        return "";
    }

    private String assembletheCode(String operation, String operandAddress) {
        /*
         * String binarytemp = convert.decimalToBin(OPTable.get(operation));
         * String binary = ""; for(int i = 0; i < 8 - binarytemp.length(); i++)
         * { binary += "0"; } binary += binarytemp; binary = binary.substring(0,
         * binary.length() - 2); binary += "000000"; String res = ""; String
         * temp = binary.substring(0, 4); res += convert.binToHexa(temp); temp =
         * binary.substring(4, 8); res += convert.binToHexa(temp); temp =
         * binary.substring(8, 12); res += convert.binToHexa(temp); res +=
         * convert.subHexa(operandAddress, currentAddress);
         */
        String temp = convert.hexaToBin(operandAddress);
        String triv = "";
        for (int i = 0; i < 16 - temp.length(); i++) {
            if (isIndex) {
                triv += "1";
                isIndex = false;
            } else {
                triv += "0";
            }
        }
        triv += temp;
        String res = OPTable.get(operation) + makeItGood(convert.binToHexa(triv));
        return res;
    }

    private String makeItGood(String operandAddress) {
        String res = "";
        for (int i = 0; i < 4 - operandAddress.length(); i++) {
            res += "0";
        }
        res += operandAddress;
        return res;
    }
    
    private String makeItGood2(String operandAddress) {
        String res = "";
        for (int i = 0; i < 6 - operandAddress.length(); i++) {
            res += "0";
        }
        res += operandAddress;
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
                    } else if (operation.equals("resb") || operation.equals("resw")) {
                        // do Nothing
                    } else {
                        break;
                    }
                }
            }
        }
        return counter * 3;
    }

    private void intializeFirstTextrec(int i2, String operation, String address) {
        if (!operation.equals("resw") && !operation.equals("resb")) {
            int len = getContLength(i2);
            ObjectFile += "\n" + "T";
            for (int i = 0; i < 6 - address.length(); i++)
                ObjectFile += "0";
            ObjectFile += address;
            String hexaLen = convert.decimalToHexa(len);
            for (int i = 0; i < 2 - hexaLen.length(); i++)
                ObjectFile += "0";
            ObjectFile += hexaLen;
        } else {
            i2++;
            matcher = pattern.matcher(intermediateFile[i2][1]);
            if (matcher.find()) {
                intializeFirstTextrec(i2, matcher.group(2).toLowerCase(), intermediateFile[i2][0]);
            }
        }
    }

    private void writeTheHeader(String name, String address, int length) {
        if (name != null) {
            ObjectFile += "H" + name;
            for (int i = 0; i < 6 - name.length(); i++)
                ObjectFile += " ";
        } else {
            ObjectFile += "H" + "      ";
        }
        for (int i = 0; i < 6 - address.length(); i++)
            ObjectFile += "0";
        ObjectFile += address;
        String hexaLen = convert.decimalToHexa(length);
        for (int i = 0; i < 6 - hexaLen.length(); i++)
            ObjectFile += "0";
        ObjectFile += hexaLen;
    }

    public static void main(String[] args) {
        FilesHandler file = new FilesHandler();
        ArrayList<String> code = file.readFile(new File("C:\\sic\\Exmpls\\AM.txt"));
        for (String x : code)
            System.out.println(x);
        Process pross = new Process(code);
        String[][] y = pross.intermediateFile;
        System.out.println("The Intermidiate file :");
        for (int i = 0; i < y.length; i++) {
            System.out.println(y[i][0] + "   " + y[i][1]);
        }
        System.out.println("------------");
        System.out.println("The Listing file");
        String[][] z = pross.listingFile;
        for (int i = 0; i < y.length; i++) {
            System.out.println(z[i][0] + "   " + pross.makeItGood2(z[i][1]) + "   " + z[i][2]);
        }
        System.out.println("------------");
        System.out.println(pross.ObjectFile);
    }
}
