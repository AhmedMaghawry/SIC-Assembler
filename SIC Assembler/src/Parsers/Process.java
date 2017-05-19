package Parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

import Symbols.IntermediateFile;
import Symbols.Literal;

public class Process {

    ArrayList<String> code;
    int start = 0;
    boolean isIndex = false;
    boolean error = false;
    String errorMessage;
    int errorIndex;
    IntermediateFile intermediateFile; //become 2D arraylist
    String[][] listingFile;
    String ObjectFile = "";
    Hashtable<String, Integer> OPTable;
    Hashtable<String, Integer> SYMTable;
    Hashtable<String, Literal> LITTable; /*Raafat*/ /* Literal value is the key , Literal object is the value of hash table)*/   
    int LOCCRT;
    int startingAddress = 0;
    int progLenght;
    Converter convert;
    Pattern pattern;
    Matcher matcher;
    static FilesHandler fileHandler;

    public Process(ArrayList<String> code, String fileName) {
        this.code = code;
        checkSpaces(code);
        convert = new Converter();
        OPTable = new Hashtable<String, Integer>();
        SYMTable = new Hashtable<String, Integer>();
        LITTable = new Hashtable<String, Literal>();/*Raafat **Key is Hex String of the literal Value , Value is Literal object ** */
        //intermediateFile = new String[code.size()][2];
        intermediateFile = new IntermediateFile();
      //  listingFile = new String[code.size()][3];
        fillOPTable();
        prs1();
        if (!error) {
            prs2();
            if (!error) {
                outputOnlyIntermediate();
                outputOnlyListing();
                outputOnlyObjectCode();
                makeListingGood();
                fileHandler = new FilesHandler();
                try {
                    fileHandler.saveObjectFile(listingFile, ObjectFile, fileName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else
                outputOnlyIntermediate();
        } else
            outputOnlyIntermediate();
    }

    private void outputOnlyObjectCode() {
        System.out.println("------------");
        System.out.println(ObjectFile);
    }

    private void outputOnlyListing() {
        System.out.println("------------");
        System.out.println("The Listing file");
        for (int i = 0; i < listingFile.length; i++) {
            System.out.println(listingFile[i][0] + "   " + makeItGood2(listingFile[i][1]) + "   " + listingFile[i][2]);
        }
    }

    private void outputOnlyIntermediate() {
        System.out.println("The Intermidiate file :");
        if (error) {
            for (int i = 0; i < intermediateFile.size(); i++) {
                if (errorIndex == i)
                    //System.out.println(intermediateFile[i][0] + "   " + intermediateFile[i][1] + "   " + errorMessage);
                	System.out.println(intermediateFile.get(i,0) + "   " + intermediateFile.get(i,1) + "   " + errorMessage);
                else
                   // System.out.println(intermediateFile[i][0] + "   " + intermediateFile[i][1]);
                	System.out.println(intermediateFile.get(i,0) + "   " + intermediateFile.get(i,1));
            }
        } else {
            for (int i = 0; i < intermediateFile.size(); i++) {
               // System.out.println(intermediateFile[i][0] + "   " + intermediateFile[i][1]);
            	  System.out.println(intermediateFile.get(i,0) + "   " + intermediateFile.get(i,1));
            }
        }
    }

    private void checkSpaces(ArrayList<String> code2) {
        Pattern patternSpaces = Pattern.compile("(?i)(.{8})\\s(.{2,6})\\s{0,2}(.{0,18})(.{0,31})");
        for (int i = 0; i < code2.size(); i++) {
            if (!code2.get(i).startsWith(".")) {
                Matcher matchSpace = patternSpaces.matcher(code2.get(i).replaceAll("\t", "    "));
                if (matchSpace.find()) {
                    String lable = matchSpace.group(1);
                    String operation = matchSpace.group(2);
                    String operand = matchSpace.group(3);
                    if (lable != null) {
                        lable = lable.trim();
                        if (lable.contains(" ")) {
                            // Exception e = new Exception("Syntax error in line
                            // : " + (i+1));
                            // e.printStackTrace();
                            error = true;
                            errorIndex = i;
                            errorMessage = "Syntax error";
                            // System.exit(0);
                        }
                    } else {
                        // Exception e = new Exception("Syntax error in line : "
                        // + (i+1));
                        // e.printStackTrace();
                        error = true;
                        errorIndex = i;
                        errorMessage = "Syntax error";
                        // System.exit(0);
                    }
                    if (operation == null) {
                        // Exception e = new Exception("There is no operation in
                        // line : " + (i+1));
                        // e.printStackTrace();
                        error = true;
                        errorIndex = i;
                        errorMessage = "There is no operation";
                        // System.exit(0);
                    }
                    
                } else {
                    // Exception e = new Exception("Syntax error in line : " +
                    // (i+1));
                    // e.printStackTrace();
                    error = true;
                    errorIndex = i;
                    errorMessage = "Syntax error";
                    // System.exit(0);
                }
            }
        }
    }

    private void makeListingGood() {
        for (int i = 0; i < listingFile.length; i++) {
            if (listingFile[i][0] == null)
                listingFile[i][0] = "";
            if (listingFile[i][1] == null)
                listingFile[i][1] = "";
            if (listingFile[i][2] == null)
                listingFile[i][2] = "";
        }
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
        while (code.get(start).contains(".")) {
        	intermediateFile.add("FirstElement", "");
           // intermediateFile[start][1] = code.get(start);
        	intermediateFile.add("SecondElement", code.get(start));
           // intermediateFile[start][0] = "";
        	
            start++;
        }
        if (!code.get(code.size() - 1).toLowerCase().contains("end")) {
            // Exception e = new Exception("There isn't end statement in the
            // program");
            // e.printStackTrace();
            // System.exit(0);
            error = true;
            errorIndex = code.size() - 1;
            errorMessage = "There isn't end statement in  the program";
        }
        pattern = Pattern.compile("(?i)(\\w+)?\\s+(\\w+)\\s*(.+)?");
        matcher = pattern.matcher(code.get(start).toLowerCase());
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                startingAddress = convert.hexaToDecimal(matcher.group(3).trim());
                LOCCRT = startingAddress;
                if ((startingAddress + "").length() > 4) {
                    // Exception e = new Exception("Out of Range Address");
                    // e.printStackTrace();
                    // System.exit(0);
                    error = true;
                    errorIndex = 0;
                    errorMessage = "Out of Range Address";
                }
                //intermediateFile[start][1] = code.get(start);
                //intermediateFile[start][0] = makeGoodShape(convert.decimalToHexa(startingAddress));
                intermediateFile.add("FirstElement", makeGoodShape(convert.decimalToHexa(startingAddress)));
                intermediateFile.add("SecondElement", code.get(start));
            } else {
                LOCCRT = 0;
                // Exception e = new Exception("Invalid Operation Code : The
                // operation : " + matcher.group(2) + " in line "
                // + 1 + " is Undefiend it should be START");
                // e.printStackTrace();
                // System.exit(0);
                error = true;
                errorIndex = 0;
                errorMessage = "Undefiend it should be START";
            }
        }
        for (int i = start + 1; i < code.size() - 1; i++) {
            matcher = pattern.matcher(code.get(i).toLowerCase());
            if (matcher.find()) {
                if (!code.get(i).contains(".")) {
                   //intermediateFile[i][1] = code.get(i);
                   // intermediateFile[i][0] = makeGoodShape(convert.decimalToHexa(LOCCRT) + "");
                    intermediateFile.add("FirstElement", convert.decimalToHexa(LOCCRT) + "");	
                    intermediateFile.add("SecondElement", code.get(i));
                    if (matcher.group(1) != null) {
                        String tt = matcher.group(1).toLowerCase().trim();
                        if (SYMTable.containsKey(tt)) {
                            // Exception e = new Exception("There is the same
                            // Symbole before : The sympole "
                            // + matcher.group(1) + " in line " + (i + 1) + " is
                            // duplicated");
                            // e.printStackTrace();
                            // System.exit(0);
                            error = true;
                            errorIndex = i;
                            errorMessage = "There is the same Symbole before";
                        } else {
                            SYMTable.put(matcher.group(1).toLowerCase(), LOCCRT);
                        }
                    }
                    String operation = matcher.group(2).toLowerCase();
                    if (OPTable.containsKey(operation)) {
                        LOCCRT += 3;
                        /*Raafat*/
                        String operand = matcher.group(3);
                        //If is Literal
                        if(operand.startsWith("=")){
                        	Literal lit = new Literal();
                        	String LitName = operand;
                        	String LitValue; //Key of hashtable **Hex String**
                        	Integer LitLength;
                        	lit.setName(LitName); //Set Literal name
                        	StringBuilder sb = new StringBuilder(operand); 
                        	sb.deleteCharAt(0);//delete "=" from the string of Literal
                        	operand = sb.toString(); // Literal without "="
                        	char c = operand.charAt(0);
                        	boolean isDigit = (c >= '0' && c <= '9'); //Do the literal starts with digit
                        	//If the Literal is of WORD type
                        	if(operand.startsWith("+") || operand.startsWith("-") || isDigit){   		 		
                        	    LitLength = 3;
                        		LitValue = convert.decimalToHexa(operand);
                        		lit.setValue(LitValue);
                        		lit.setLength(LitLength);  
                        		if(!LITTable.containsKey(LitValue)){
                        		  LITTable.put(LitValue, lit);	
                        		}
                        		//If the literal of Type byte character
                        	}else if(operand.toLowerCase().startsWith("c")){
                        		LitLength = operand.length()-3;
                        		LitValue = convConstantByteToObjectCode(operand,i);
                        		lit.setValue(LitValue);
                        		lit.setLength(LitLength);
                        		if(!LITTable.containsKey(LitValue)){
                          		  LITTable.put(LitValue, lit);	
                          		}
                        	}else if(operand.toLowerCase().startsWith("x")){
                        		LitLength = ((operand.length() - 3) % 2 == 0) ? (operand.length() - 3) / 2 : (operand.length() - 3) / 2 + 1;
                        		LitValue = convConstantByteToObjectCode(operand,i);
                        		lit.setValue(LitValue);
                        		lit.setLength(LitLength);
                        		if(!LITTable.containsKey(LitValue)){
                          		  LITTable.put(LitValue, lit);	
                          		}
                        	}else{
                        		error = true;
                                errorMessage = "Wrong Literal Format";
                                errorIndex = i;
                        	}//error 
                        }//if  Literal                      
                        
                    } else if (operation.equals("word")) {
                        LOCCRT += 3;
                    } else if (operation.equals("resw")) {
                        LOCCRT += 3 * Integer.parseInt(matcher.group(3).trim());
                    } else if (operation.equals("resb")) {
                        LOCCRT += Integer.parseInt(matcher.group(3).trim());
                    } else if (operation.equals("byte")) {
                        if (matcher.group(3).toLowerCase().startsWith("c")) {
                            String word = matcher.group(3).toLowerCase().trim();
                            LOCCRT += word.length() - 3;
                        } else if (matcher.group(3).toLowerCase().startsWith("x")) {
                            String word = matcher.group(3).toLowerCase().trim();
                            LOCCRT += ((word.length() - 3) % 2 == 0) ? (word.length() - 3) / 2
                                    : (word.length() - 3) / 2 + 1;
                        }
                    }else if(operation.equals("ltorg")){
                    	            
                    } else {
                        // Exception e = new Exception("Invalid Operation Code :
                        // The operation : " + operation
                        // + " in line " + (i + 1) + " is Undefiend");
                        // e.printStackTrace();
                        // System.exit(0);
                        error = true;
                        errorIndex = i;
                        errorMessage = "Invalid Operation Code";
                    }
                } else {
                   // intermediateFile[i][1] = code.get(i);
                  //  intermediateFile[i][0] = "";
                    intermediateFile.add("FirstElement", "");
                    intermediateFile.add("SecondElement", code.get(i));
                }
            } else {
                //intermediateFile[i][1] = code.get(i);
                //intermediateFile[i][0] = "";
                intermediateFile.add("FirstElement", "");
                intermediateFile.add("SecondElement", code.get(i));
            }
        }//for
       // intermediateFile[code.size() - 1][1] = code.get(code.size() - 1);
       // intermediateFile[code.size() - 1][0] = makeGoodShape(convert.decimalToHexa(LOCCRT) + "");
        intermediateFile.add("FirstElement", makeGoodShape(convert.decimalToHexa(LOCCRT) + ""));
        intermediateFile.add("SecondElement", code.get(code.size() - 1));
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
    	listingFile = new String[intermediateFile.size()][3];
        //String firstLine = intermediateFile[start][1];
    	String firstLine = intermediateFile.get(start, 1);
        matcher = pattern.matcher(firstLine);
        if (matcher.find()) {
            if (matcher.group(2).toLowerCase().equals("start")) {
                listingFile[start][0] = intermediateFile.get(start,0).toUpperCase();
                listingFile[start][1] = "";                
                listingFile[start][2] = intermediateFile.get(start, 1);                
            }
            writeTheHeader(matcher.group(1), matcher.group(3).trim(), progLenght);
            intializeFirstTextrec(start + 1, matcher.group(2).toLowerCase(), matcher.group(3).trim());
        }
        boolean openIT = false;
        int counter = 0;
        String tempObj = "";
        for (int i = start + 1; i < intermediateFile.size(); i++) {
            matcher = pattern.matcher(intermediateFile.get(i,1).toLowerCase());
            if (matcher.find()) {
                if (!intermediateFile.get(i,1).startsWith(".")) {
                    String operation = matcher.group(2).toLowerCase();
                    // String sympole = (matcher.group(1) != null)?
                    // matcher.group(1).toLowerCase() : null;
                    String operand = matcher.group(3);
                    if (operand != null) {
                        operand = operand.toLowerCase().trim();
                    }
                    if (OPTable.containsKey(operation)) {
                        String operandAddress;
                        if (operand != null) {
                            if (SYMTable.containsKey(operand)) {
                                //listingFile[i][0] = intermediateFile[i][0].toUpperCase();
                            	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                                //listingFile[i][2] = intermediateFile[i][1];
                            	listingFile[i][2] = intermediateFile.get(i,1);
                                operandAddress = convert.decimalToHexa(SYMTable.get(operand)) + "";
                            } else if (operand.toLowerCase().contains("0x")) {
                            	//listingFile[i][0] = intermediateFile[i][0].toUpperCase();
                            	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                                //listingFile[i][2] = intermediateFile[i][1];
                            	listingFile[i][2] = intermediateFile.get(i,1);
                                operandAddress = operand.substring(2, operand.length());
                            } else if (operand.toLowerCase().contains(",x")) {
                            	//listingFile[i][0] = intermediateFile[i][0].toUpperCase();
                            	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                                //listingFile[i][2] = intermediateFile[i][1];
                            	listingFile[i][2] = intermediateFile.get(i,1);
                                operandAddress = convert
                                        .decimalToHexa(SYMTable.get(operand.substring(0, operand.length() - 2))) + "";
                                isIndex = true;
                            } else {
                                operandAddress = "0";
                                // Exception e = new Exception("Invalid Address
                                // : The Lable : " + operand.toUpperCase()
                                // + " in line " + (i + 1) + " Didn't exsist");
                                // e.printStackTrace();
                                // System.exit(0);
                                error = true;
                                errorIndex = i;
                                errorMessage = "This Lable isn't defined";
                            }
                        } else {
                        	//listingFile[i][0] = intermediateFile[i][0].toUpperCase();
                        	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                            //listingFile[i][2] = intermediateFile[i][1];
                        	listingFile[i][2] = intermediateFile.get(i,1);
                            operandAddress = "0";
                        }
                        listingFile[i][1] = assembletheCode(operation, operandAddress).toUpperCase();
                        counter++;
                    } else if (operation.equals("word") || operation.equals("byte")) {
                    	//listingFile[i][0] = intermediateFile[i][0].toUpperCase();
                    	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                        //listingFile[i][2] = intermediateFile[i][1];
                    	listingFile[i][2] = intermediateFile.get(i,1);
                        if (operation.equals("word")) {
                            listingFile[i][1] = convConstantWordToObjectCode(operand).toUpperCase();
                        } else {
                            listingFile[i][1] = convConstantByteToObjectCode(operand,i).toUpperCase();
                        }
                        counter++;
                    } else if (operation.equals("resw") || operation.equals("resb") || operation.equals("end")) {
                    	listingFile[i][0] = intermediateFile.get(i, 0).toUpperCase();
                        listingFile[i][1] = "";
                        listingFile[i][2] = intermediateFile.get(i,1);
                        //counter++;
                    }
                    if (counter > 10 || openIT) {
                        counter = 1;
                        openIT = false;
                        // writeTextRecordToObjectProg();
                        ObjectFile += goodLen(convert.decimalToHexa(
                                (tempObj.length() % 2 == 0) ? tempObj.length() / 2 : tempObj.length() / 2 + 1))
                                        .toUpperCase()
                                + tempObj;
                        tempObj = "";
                        intializeFirstTextrec(i, matcher.group(2).toLowerCase(), intermediateFile.get(i, 0));
                    }
                    if(operation.equals("resw") || operation.equals("resb"))
                        openIT = true;
                    // addToObjectF(listingFile[i][1]);
                    tempObj += listingFile[i][1];
                } else {
                    listingFile[i][0] = code.get(i);
                    listingFile[i][1] = "";
                    listingFile[i][2] = "";
                }
            }
        }
        if (counter != 0) {
            ObjectFile += goodLen(convert
                    .decimalToHexa((tempObj.length() % 2 == 0) ? tempObj.length() / 2 : tempObj.length() / 2 + 1))
                            .toUpperCase()
                    + tempObj;
            tempObj = "";
        }
        // writeLastTextRectoObjPro();
        writeEndRectoObjPro();
    }

    /*
     * private void addToObjectF(String objectCode) { if
     * (!objectCode.equals("")) { /*for (int i = 0; i < 6 - objectCode.length();
     * i++) { ObjectFile += "0"; }
     */
    /*
     * ObjectFile += objectCode.toUpperCase(); } }
     */

    private String goodLen(String x) {
        String res = "";
        for (int i = 0; i < 2 - x.length(); i++) {
            res += "0";
        }
        res += x;
        return res;
    }

    private void writeEndRectoObjPro() {
        ObjectFile += System.lineSeparator() + "E";
        String startAdd = convert.decimalToHexa(startingAddress);
        for (int i = 0; i < 6 - startAdd.length(); i++) {
            ObjectFile += "0";
        }
        ObjectFile += startAdd.toUpperCase();
    }

    private String convConstantWordToObjectCode(String operand) {
        String res = "";
        for (int i = 0; i < 6 - operand.length(); i++) {
            res += "0";
        }
        res += convert.decimalToHexa(Integer.parseInt(operand));
        return res;
    }

    private String convConstantByteToObjectCode(String operand, int k) {
        String res = "";
        if (operand.startsWith("c")) {
            String value = operand.substring(2, operand.length() - 1).toUpperCase();
            if(value.length() > 3){
                error = true;
                errorMessage = "Out of Range";
                errorIndex = k;
            }
            for (int i = 0; i < value.length(); i++) {
                res += Integer.toHexString((int) value.charAt(i));
            }
        } else if (operand.startsWith("x")) {
            String value = operand.substring(2, operand.length() - 1);
            if(value.length() > 6){
                error = true;
                errorMessage = "Out of Range";
                errorIndex = k;
            }
            return value;
        }
        return res;
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
        String resOperation = "";
        String operationST = convert.decimalToHexa(OPTable.get(operation));
        for (int i = 0; i < 2 - operationST.length(); i++)
            resOperation += "0";
        resOperation += operationST;
        String res = resOperation + makeItGood(convert.binToHexa(triv));
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
            res += " ";
        }
        res += operandAddress;
        return res;
    }

    /*
     * private int getContLength(int start) { int counter = 0; for (int i =
     * start; i < start + 10; i++) { matcher =
     * pattern.matcher(code.get(i).toLowerCase()); if (matcher.find()) { if
     * (!code.get(i).startsWith(".")) { String operation =
     * matcher.group(2).toLowerCase(); if (OPTable.containsKey(operation) ||
     * operation.equals("word") || operation.equals("byte")) { counter++; } else
     * if (operation.equals("resb") || operation.equals("resw")) { // do Nothing
     * } else { break; } } } } return counter * 3; }
     */

    private void intializeFirstTextrec(int i2, String operation, String address) {
        if (!operation.equals("resw") && !operation.equals("resb") && !operation.equals("end")) {
            ObjectFile += System.lineSeparator() + "T";
            for (int i = 0; i < 6 - address.length(); i++)
                ObjectFile += "0";
            ObjectFile += address.toUpperCase();
        } else {
            if(!operation.equals("end")){
                i2++;
            //matcher = pattern.matcher(intermediateFile[i2][1]);
                matcher = pattern.matcher(intermediateFile.get(i2, 1));
            if (matcher.find()) {
                //intializeFirstTextrec(i2, matcher.group(2).toLowerCase(), intermediateFile[i2][0]);
            	intializeFirstTextrec(i2, matcher.group(2).toLowerCase(), intermediateFile.get(i2, 0));
            }
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
        ObjectFile += address.toUpperCase();
        String hexaLen = convert.decimalToHexa(length);
        for (int i = 0; i < 6 - hexaLen.length(); i++)
            ObjectFile += "0";
        ObjectFile += hexaLen.toUpperCase();
    }

    public static void main(String[] args) {
        fileHandler = new FilesHandler();
        JFrame frame = new JFrame("SIC");
        frame.setVisible(true);
        JTextField btn = new JTextField();
        frame.getContentPane().add(btn);
        frame.setSize(100, 100);
        JFileChooser fileCho = new JFileChooser();
        fileCho.showOpenDialog(frame);
        File getFile = fileCho.getSelectedFile();
        String fileName = getFile.getName();
        ArrayList<String> code = fileHandler.readFile(getFile);
        for (String x : code)
            System.out.println(x);
        Process pross = new Process(code, fileName);
        // String[][] y = pross.intermediateFile;
        // System.out.println("The Intermidiate file :");
        // for (int i = 0; i < y.length; i++) {
        // System.out.println(y[i][0] + " " + y[i][1]);
        // }
        // System.out.println("------------");
        // System.out.println("The Listing file");
        // String[][] z = pross.listingFile;
        // for (int i = 0; i < z.length; i++) {
        // System.out.println(z[i][0] + " " + pross.makeItGood2(z[i][1]) + " " +
        // z[i][2]);
        // }
        // System.out.println("------------");
        // System.out.println(pross.ObjectFile);
        btn.setEditable(false);
        if (pross.error)
            btn.setText("Error");
        else
            btn.setText("Done");
    }
}
