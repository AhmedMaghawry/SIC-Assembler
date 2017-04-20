package Parsers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {

    ArrayList<String> code;
    ArrayList<String> toPros2;
    Hashtable<String, Integer> OPTable;
    Hashtable<String, Integer> SYMTable;
    int LOCCRT;
    int startingAddress = 0;
    int progLenght;
    Pattern pattern;
    Matcher matcher;
    StringBuilder builder;
    String output;

    public Process(ArrayList<String> code) {
        this.code = code;
        OPTable = new Hashtable<String, Integer>();
        SYMTable = new Hashtable<String, Integer>();
        toPros2 = new ArrayList<>();
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
                startingAddress = Integer.parseInt(matcher.group(3));
                LOCCRT = startingAddress;
                toPros2.add(code.get(0));
            } else {
                LOCCRT = 0;
            }
        }
        for (int i = 1; i < code.size(); i++) {
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
                    if (OPTable.contains(matcher.group(2).toLowerCase())) {
                        LOCCRT += 3;
                    } else if (matcher.group(2).toLowerCase().equals("word")) {
                        LOCCRT += 3;
                    } else if (matcher.group(2).toLowerCase().equals("resw")) {
                        LOCCRT += 3 * Integer.parseInt(matcher.group(3));
                    } else if (matcher.group(2).toLowerCase().equals("resb")) {
                        LOCCRT += Integer.parseInt(matcher.group(3));
                    } else if (matcher.group(2).toLowerCase().equals("byte")) {
                        if (matcher.group(3).toLowerCase().startsWith("c")) {
                            LOCCRT += 3;
                        } else {
                            LOCCRT++;
                        }
                    } else if (matcher.group(2).toLowerCase().equals("end")) {
                        // Do Nothing just save it
                    } else {
                        Exception e = new Exception("Invalid Operation Code");
                        e.printStackTrace();
                    }
                }
                toPros2.add(code.get(i));
            }
        }
        progLenght = LOCCRT - startingAddress;
    }

    void prs2() {
        
    }
}
