package com.sohu.sohudb;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import lava.rt.adapter.MBeanServerAdapter;
import lava.rt.adapter.PropertiesAdapter;
import lava.rt.aio.ClientFactory;

import lava.rt.aio.tcp.TcpQueryClient;
import lava.rt.aio.tcp.TcpServerConfig;
import lava.rt.common.ReflectCommon;
import lava.rt.logging.LogFactory;

public class AsyncSohuDBClientFactory implements ClientFactory<TcpQueryClient> {

	public TcpQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		
		AsyncSohuDBClientFactory factory=new AsyncSohuDBClientFactory();
		
		TcpQueryClient client=factory.newInstance();
		
		LogFactory.SYSTEM.level=LogFactory.LEVEL_WARN;
		
		TcpServerConfig config=new TcpServerConfig("test", "hhlin@localhost:8080");
		
		String prefix="";
		String template=PropertiesAdapter.getSimpleFieldNames(prefix,TcpServerConfig.class);
		
		System.out.println(template);
		
		File file=ReflectCommon.getFile("/aio.properties");
		
		PropertiesAdapter propertiesAdapter=new PropertiesAdapter(file);
		
		propertiesAdapter.injection(prefix,config);
		
		AsyncSohuDBPool pool=new AsyncSohuDBPool(config);
		
		//pool.init();
		
		String domainName = "MyMBean";
		
		 MBeanServerAdapter mbs0=new MBeanServerAdapter();
		 
		           //create mbean and register mbean
		 mbs0.registerMBean(new Hello(),"");
		 mbs0.registerMBean(new Hello(),"sdfsdf");          

		 
		 mbs0.registerMBean(config,"-fdsfs");
        
        System.out.println("start.....");
		
        
        int rmiPort = 1099;
        Registry registry = LocateRegistry.createRegistry(rmiPort);
        
        
        MBeanServerAdapter mbs=new MBeanServerAdapter(MBeanServerFactory.createMBeanServer(domainName));
        mbs.registerMBean(new Hello(),"5");
        
        
        JMXServiceURL url =MBeanServerAdapter.getRmiUrl("localhost", rmiPort, domainName);
        
        System.out.println(url);
        
        JMXConnectorServer jmxConnector =mbs.newJMXConnectorServer(url, System.getenv());
        jmxConnector.start();

	}
	
}
