import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DiffTest {

	
	
	public static void main(String[] args) throws Exception {
		//String path="E:/jboss-5.1.0.GA/server/default/deploy/backend.war/WEB-INF/classes/org/b3mn/poem/handler/";
		JarFile jarNew = new JarFile(
				"E:\\workspace-spring-tool-suite-4-4.0.0.RELEASE\\jc-trunk\\jc_web\\target\\jc_web.war")
				,jarOld = new JarFile(
						"E:\\workspace-spring-tool-suite-4-4.0.0.RELEASE\\jc-trunk\\jc_web\\target\\jc_web_old.war")
				;
		String diffRoot="E:\\\\workspace-spring-tool-suite-4-4.0.0.RELEASE\\\\jc-trunk\\\\jc_web\\\\target\\diff";
		
		File diffDir=new File(diffRoot);
		
		diffDir.deleteOnExit();
		diffDir.mkdirs();
		
		Enumeration<JarEntry> entries = jarNew.entries(),oldEtries = jarOld.entries();
		
		Map<String,JarEntry> oldEntryMap=new HashMap<>();
		while(oldEtries.hasMoreElements()) {
            JarEntry entry=oldEtries.nextElement();
			
			if(entry.isDirectory())continue;
			
			String key=entry+" "+entry.getTime();
			oldEntryMap.put(key, entry);
			
		}
		
		//遍历条目。 
		while (entries.hasMoreElements()) {
			//参考api获取你需要的文件信息。
			JarEntry entry=entries.nextElement();
			
			if(entry.isDirectory())continue;
			
			String key=entry+" "+entry.getTime();
			
			if(oldEntryMap.containsKey(key))continue;
			
            String msg=key;
			//msg+="\n"+br.readLine();
			//br.close();
			//is.close();
            createFile(diffRoot,jarNew,entry);
			System.out.println(msg);
		}
		
		
		
		
	}

	private static void createFile(String diffRoot,JarFile jarFile, JarEntry entry) throws IOException {
		// TODO Auto-generated method stub
		String dir=entry.toString(),file=diffRoot+"/"+entry.toString();
		dir=dir.substring(0, dir.lastIndexOf("/"));
		dir=diffRoot+"/"+dir;
		
		String msg="diffpath:"+file;
		File fileDir=new File(dir),filePath=new File(file);
		if(!fileDir.exists())fileDir.mkdirs();
		
		//filePath.deleteOnExit();
		if(!filePath.createNewFile()) {
			throw new IOException("create fail:"+file);
		}
		
		
		try(InputStream in = jarFile.getInputStream(entry)
				;
			BufferedReader br=new BufferedReader(new InputStreamReader(in))
				;
			FileWriter fw=new FileWriter(filePath)
				){
			
			
			
			
			char[] bs=new char[1024];
			//String line=null;
			while(br.read(bs)>0) {
				fw.write(bs);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		System.out.println(msg);
	}
	
	
	
	
	private static void zip(String zipFileName, File inputFile) throws Exception {
		System.out.println("压缩中...");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		BufferedOutputStream bo = new BufferedOutputStream(out);
		zip(out, inputFile, inputFile.getName(), bo);
		bo.close();
		out.close(); // 输出流关闭
		System.out.println("压缩完成");
	}
 
	private static void zip(ZipOutputStream out, File f, String base,
			BufferedOutputStream bo) throws Exception { // 方法重载
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (fl.length == 0) {
				out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
				System.out.println(base + "/");
			}
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
			}
			
		} else {
			out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
			System.out.println(base);
			FileInputStream in = new FileInputStream(f);
			BufferedInputStream bi = new BufferedInputStream(in);
			int b;
			while ((b = bi.read()) != -1) {
				bo.write(b); // 将字节流写入当前zip目录
			}
			bi.close();
			in.close(); // 输入流关闭
		}
	}


}
