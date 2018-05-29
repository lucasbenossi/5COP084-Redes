package lmbenossi.ArgsParser;

public enum Arg {
	SENDER("-sender", Type.BOOLEAN),
	RECEIVER("-receiver", Type.BOOLEAN),
	HOST("-host", Type.STRING),
	FILE("-file", Type.STRING),
	PORT("-port", Type.INTEGER),
	PARTLEN("-partlen", Type.INTEGER),
	BUFFER("-buffer", Type.INTEGER);
	
	private enum Type{
		BOOLEAN, STRING, INTEGER;
	}
	
	private String arg;
	private Type type;
	private boolean set = false;
	private String value = null;
	private int integer = 0;
	
	private Arg(String arg, Type type) {
		this.arg = arg;
		this.type = type;
	}
	
	public void set() {
		this.set = true;
	}
	public void set(String value) {
		if(this.isInteger()) {
			try {
				this.integer = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				this.integer = 0;
				return;
			}
		}
		set();
		this.value = value;
	}

	@Override
	public String toString() {
		return this.arg;
	}
	
	public boolean isBoolean() {
		return this.type.equals(Type.BOOLEAN);
	}
	public boolean isString() {
		return this.type.equals(Type.STRING);
	}
	public boolean isInteger() {
		return this.type.equals(Type.INTEGER);
	}
	
	public boolean isSet(){
		return this.set;
	}
	
	public String getValue() {
		return this.value;
	}
	public int getInteger() {
		return this.integer;
	}
}
