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
    
    public String decimalToBin(int value) {
        return Integer.toBinaryString(value);
    }
    
    public String subHexa(String hexa1, String hexa2) {
        int res = hexaToDecimal(hexa1) - hexaToDecimal(hexa2);
        return decimalToHexa(res);
    }
}
