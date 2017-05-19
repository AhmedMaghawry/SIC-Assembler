package Symbols;

public class Literal {
 private String name;
 private String value;//Hex String
 private Integer length;
 private Integer address;
 
 public Literal(String name,String value,Integer length,Integer address) {
   this.name = name;
   this.value = value;
   this.length = length;
   this.address = address;
}//Constructor

public Literal() {
	// TODO Auto-generated constructor stub
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}

public Integer getLength() {
	return length;
}

public void setLength(Integer length) {
	this.length = length;
}

public Integer getAddress() {
	return address;
}

public void setAddress(Integer address) {
	this.address = address;
}
 
 
}//class
