package Parsers;

public class Converter {
    
    public String hexaToBin(String value) {
        return Integer.toBinaryString(hexaToDecimal(value));
    }
    
    public String binToHexa(String value) {
        return decimalToHexa(Integer.parseInt(value, 2));
    }
    
    public int hexaToDecimal(String value) {
        return Integer.parseInt(value, 16);
    }
    
    public String decimalToHexa(int value) {
        return Integer.toHexString(value);
    }    
}
