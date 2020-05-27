package lava.rt.wrapper;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.util.*;
import java.util.Map.Entry;

import javax.management.*;
import javax.management.modelmbean.*;
import javax.management.remote.*;

import lava.rt.common.ReflectCommon;


public class MBeanServerWrapper extends BaseWrapper<MBeanServer> {

	protected final static Map<Class, ModelMBeanInfo> clsMbeanInfoMap = new HashMap<>();

	public MBeanServerWrapper() {
		super(ManagementFactory.getPlatformMBeanServer());
		// TODO Auto-generated constructor stub

	}

	public MBeanServerWrapper(MBeanServer _this) {
		super(_this);
		// TODO Auto-generated constructor stub
    
	}

	public JMXConnectorServer newJMXConnectorServer(JMXServiceURL serviceURL, Map<String, ?> environment)
			throws IOException {
		JMXConnectorServer ret = JMXConnectorServerFactory.newJMXConnectorServer(serviceURL, environment, _this);
		return ret;
	}

	

	public ObjectInstance registerMBean(Object object, String subfix) throws Exception {
		ObjectInstance ret = null;
		if(isImplMBean(object)) {
			ObjectName objectName = new ObjectName(
					object.getClass().getPackage().getName() + ":name=" + object.getClass().getSimpleName() + subfix);
			ret = _this.registerMBean(object, objectName);
		}else {
			RequiredModelMBean rmbean=createMBean(object);
			ret = registerMBean(rmbean, subfix);
		}
		

		return ret;
	}

	public static boolean isImplMBean(Object object) {
		// TODO Auto-generated method stub
		boolean ret=false;
		for(Class inft:object.getClass().getInterfaces()){
			if(inft.getName().endsWith("MBean")) {
				ret=true;
				break;
			}
		}
		return ret;
	}

	public ObjectInstance registerMBean(RequiredModelMBean mbean, String subfix) throws Exception {
		
		String cname=mbean.getMBeanInfo().getClassName(),
				pname=cname.substring(0, cname.lastIndexOf("."))
				,sname=cname.substring(cname.lastIndexOf(".")+1)
				;
		ObjectName objectName = new ObjectName(
				pname + ":name=" + sname + subfix);
		ObjectInstance ret = _this.registerMBean(mbean, objectName);

		return ret;
	}
	
	public static JMXServiceURL getRmiUrl(String host, int rmiPort, String domainName) throws MalformedURLException {
		JMXServiceURL ret = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://" + host + ":" + rmiPort + "/" + domainName);

		return ret;
	}

	public static RequiredModelMBean createMBean(Object bean) throws Exception {
		RequiredModelMBean model = new RequiredModelMBean();
			model.setManagedResource(bean, "ObjectReference");
			ModelMBeanInfo info = getMBeanInfo(bean.getClass());
			model.setModelMBeanInfo(info);
		
		return model;
	}

	public static ModelMBeanInfo getMBeanInfo(Class beanCls) {

		ModelMBeanInfo ret = clsMbeanInfoMap.get(beanCls);

		if (ret == null) {

			Map<String, Field> fieldMap = ReflectCommon.getTheDeclaredFieldMap(beanCls);
			Map<String, Method> methodMap = ReflectCommon.getAllMethodMap(beanCls);
			Set<String> getSetNames=new HashSet<>();
			List<ModelMBeanAttributeInfo> attrs = new ArrayList<>();
			List<ModelMBeanOperationInfo> opers = new ArrayList<>();
			for (Entry<String, Field> ent : fieldMap.entrySet()) {

				//////////////////////////////////////////////////////////////////
				// 属性 //
				//////////////////////////////////////////////////////////////////
				// 构造name属性信息
				String dname = ent.getKey().substring(0, 1).toUpperCase() + ent.getKey().substring(1),
						setName = "set" + dname, getName = "get" + dname;
				;
				boolean READABLE = methodMap.containsKey(getName), WRITABLE = methodMap.containsKey(setName),
						BOOLEAN = ent.getValue().getType() == Boolean.class;
				Descriptor portAttrDesc = new DescriptorSupport();
				portAttrDesc.setField("name", dname);
				portAttrDesc.setField("descriptorType", "attribute");
				portAttrDesc.setField("displayName", dname);
				if (READABLE) {
					portAttrDesc.setField("getMethod", getName);
                    //methodMap.remove(getName);
					getSetNames.add(getName);
				}
				if (WRITABLE) {
					portAttrDesc.setField("setMethod", setName);
					//methodMap.remove(setName);
				    getSetNames.add(setName);
				}

				String tname=ent.getValue().getType().getName();
				ModelMBeanAttributeInfo nameAttrInfo = new ModelMBeanAttributeInfo(//
						dname, // 属性名
						tname, // 属性类型
						"", // 描述文字
						READABLE, WRITABLE, BOOLEAN, // 读写
						portAttrDesc // 属性描述
				);

				attrs.add(nameAttrInfo);

			}

			for (Entry<String, Method> ent : methodMap.entrySet()) {
				
				
				
				Method method = ent.getValue();
				ModelMBeanOperationInfo oper;
				Parameter[] params = method.getParameters();
				MBeanParameterInfo[] mparams = new MBeanParameterInfo[params.length];
				for (int i = 0; i < mparams.length; i++) {
					Parameter parami = params[i];
					mparams[i] = new MBeanParameterInfo(parami.getName(), parami.getType().getName(), "");
				}
				
				oper = new ModelMBeanOperationInfo(//
						ent.getKey(), //
						null, //
						mparams, //
						method.getReturnType().getName(), //
						MBeanOperationInfo.INFO, //
						null//
				);
				Descriptor portAttrDesc = oper.getDescriptor();
				if(getSetNames.contains(ent.getKey())) {
					portAttrDesc.setField("visibility", 4);
				}
				oper.setDescriptor(portAttrDesc);
				opers.add(oper);
			}

			//////////////////////////////////////////////////////////////////
			// 最后总合 //
			//////////////////////////////////////////////////////////////////
			// create ModelMBeanInfo
			String desc="create MBeanInfo by "+MBeanServerWrapper.class.getName()+".createMBeanInfo";
			ret = new ModelMBeanInfoSupport(//
					beanCls.getName(), // MBean类
					desc, // 描述文字
					attrs.toArray(new ModelMBeanAttributeInfo[attrs.size()]), 
					null, // 所有的构造函数信息
					opers.toArray(new ModelMBeanOperationInfo[opers.size()]), //
					null, // 所有的通知信息(本例无)
					null// MBean描述
			);
            clsMbeanInfoMap.put(beanCls, ret);
		}
		return ret;
	}

}
