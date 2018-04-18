package lava.test.common;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import lava.rt.common.ReflectCommon;



public class ReflectCommonTest {

	Properties properties;
	
	public static String test_a;
	
	public static String test_b;
	
	public static String[] test_arr;
	
	@Before
	public void setUp() throws Exception {
		properties=new Properties();
		String CONF_PATH=ReflectCommonTest.class.getClassLoader().getResource("rt.properties").getFile();
		InputStream is =new FileInputStream(CONF_PATH);
		properties.load(is);
		is.close();
		
	}

	@Test
	public void test() {
		float pred=ReflectCommon.loadProperties(properties, ReflectCommonTest.class);
		 assertNotEquals(0, pred);
        assertEquals("vvvv", test_a);
	}
	
	@Test
	public void testInjection() {
		float pred=ReflectCommon.injection(properties);
        assertNotEquals(0, pred);
        
	}

}
