import lava.rt.wrapper.LoggerWrapper;

public class IntTest {

	
	static LoggerWrapper logger=LoggerWrapper.CONSOLE;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Integer re=3|5|9;
        
        logger.info(re.toBinaryString(re));
	}

}
