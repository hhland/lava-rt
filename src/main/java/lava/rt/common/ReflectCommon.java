package lava.rt.common;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import lava.rt.linq.Entity;
import lava.rt.wrapper.UnsafeWrapper;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class ReflectCommon {

	
	
	
	
	
    
	
	

	public static String getClassPath() {
		String ret= ReflectCommon.class.getClassLoader().getResource("").getPath().replace("file:/", "");
	    return ret;
	}

	

	
	
	public static boolean isThis0(Field field) {
        return field.getName().equals("this$0");
    }
	
	
	public static boolean isArray(Field field) {
        return field.getType().getName().startsWith("[L");
    }
	
	
	
	 //无限级内部类实例化
    public static <T> T newInstance(Class<T> cls) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        T t = null;
        String clsname = cls.getName();
        int i = clsname.lastIndexOf("$");
        if (i > -1) {
            Constructor constr = cls.getConstructors()[0];
            constr.setAccessible(true);
            String pname = clsname.substring(0, i);
            Class pcls = null;
            try {
                pcls = Class.forName(pname);
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(ex);
            }
            t = (T) constr.newInstance(newInstance(pcls));
        } else {
            t = (T) cls.newInstance();
        }
        return t;
    }
   
    public static Map<String,Method> getTheMethodMap(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	
    	Method[] fields=cls.getMethods();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	
    	return fieldMap;
    }
    
    public static Map<String,Method> getAllMethodMap(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	for(Class cl :getTheClassMap(cls).values()) {
    		fieldMap.putAll(getTheMethodMap(cl));   		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Method> getAllDeclaredMethodMap(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	for(Class cl:getTheClassMap(cls).values()) {
    		fieldMap.putAll(getTheDeclaredMethodMap(cl));   		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Method> getTheDeclaredMethodMap(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	
    		Method[] fields=cls.getDeclaredMethods();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	
    	return fieldMap;
    }
    
    
    public static Map<String,Field> getFieldMap(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl :getTheClassMap(cls).values()) {
    		Field[] fields=cl.getFields();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    
    public static Map<String,Field> getAllDeclaredFieldMap(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl:getTheClassMap(cls).values()) {
    		fieldMap.putAll(getTheDeclaredFieldMap(cl));    		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Field> getTheDeclaredFieldMap(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	
    	Field[] fields=cls.getDeclaredFields();
    	Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    	.forEach(f->fieldMap.put(f.getName(), f) );    		
    	
    	return fieldMap;
    }
    
    
    
    
    
    public static Map<String,Class> getTheClassMap(Class cls){
    	Map<String,Class> re=new HashMap<String,Class>();
    	
    	for(Class cl=cls;!Object.class.equals(cl);cl=cl.getSuperclass()) {
    		re.put(cl.getName(), cl);	
    	}
    	return re;
    }
    
    
    
    
    
   
    
    public static Object invoke(Object target,String methodName,Object...params) throws Exception {
		Object ret=null;
		Class[] parameterTypes=new Class[params.length];
		for(int i=0;i<parameterTypes.length;i++) {
			parameterTypes[i]=params[i].getClass();
		}
		
	    Method method= target.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		ret=method.invoke(target, params);
		
		return ret;
	}
    
    
    public static Map<String,Class<?>> getClassMap(Package pack) {

		// 第一个class类的集合
		//Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
    	Map<String,Class<?>> classes=new HashMap<>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack.getName();
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					System.err.println("file类型的扫描");
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					System.err.println("jar类型的扫描");
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											// 添加到classes
											Class cls=Class.forName(packageName + '.' + className);
											classes.put(cls.getName(),cls);
										} catch (ClassNotFoundException e) {
											// log
											// .error("添加用户自定义视图类错误 找不到此类的.class文件");
											  
										}
									}
								}
							}
						}
					} catch (IOException e) {
						// log.error("在扫描用户定义视图时从jar包获取文件出错");
						  
					}
				}
			}
		} catch (IOException e) {
			  
		}

		return classes;
	}

	protected static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Map<String,Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {

			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' + className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
					Class cls=Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
					classes.put(cls.getName(),cls);
				} catch (ClassNotFoundException e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					  
				}
			}
		}
	}
	
	
	public static boolean isStatic(Field field){
		return Modifier.isStatic(field.getModifiers());
	}

	
	public static <E extends Entity> E[] newEntitys(int size,Class<E> entryClass,Object ...objects) throws Exception {
		E[] ret=(E[])Array.newInstance(entryClass,size);
		for(int i=0;i<ret.length;i++){
			ret[i]=newEntity(entryClass, objects);
		}
		return ret;
	}
	
	
	public static <E extends Entity> E newEntity(Class<E> entryClass,Object ...objects) throws Exception {
		E ret = null;
		if(objects.length==0){
		   ret= entryClass.newInstance();
		}else{
		   ret = (E) entryClass.getConstructors()[0].newInstance(objects);
		}
		return ret;
	}
	
	
	public static String toString(Object obj) {
		StringBuffer sbr=new StringBuffer(obj.getClass().getSimpleName());
		sbr.append(" [");
		
		for(Entry<String, Field> ent: getAllDeclaredFieldMap(obj.getClass()).entrySet()) {
			Object val="null";
			try {
				Field field=ent.getValue();
				field.setAccessible(true);
				val =field.get(obj); 
				
			} catch (Exception e) {}
			sbr
			.append(ent.getKey())
			.append("=")
			.append(val)
			.append(",")
			;
		}
		
		sbr.append("]");
		return sbr.toString();
	}
	
}
