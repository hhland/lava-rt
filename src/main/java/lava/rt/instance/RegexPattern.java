package lava.rt.instance;


import java.util.regex.Pattern;

public enum RegexPattern implements Instanceable<Pattern>{

	number(""),
	url("^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$ ")
	
	;
	
	
	final  Pattern pattern;
	
	
	private RegexPattern(String regex) {
		// TODO Auto-generated constructor stub
		pattern=Pattern.compile(regex);
	}
	
	public Pattern get() {
	
		return this.pattern;
	}
}
