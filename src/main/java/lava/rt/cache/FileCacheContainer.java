package lava.rt.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class FileCacheContainer extends CacheContainer{
	
	final File baseDir;
	
	

	public FileCacheContainer(File baseDir) {
		super();
		this.baseDir = baseDir;
	}
	
	
	

	@Override
	public void remove(CacheItem... items) {
		// TODO Auto-generated method stub
		for(CacheItem item :items) {
			String key=item.key;
			File cacheFile=new File(baseDir+"/"+key);
			cacheFile.delete();
		}
		
	}

	@Override
	public <E> CacheItem<E> get(Class<E> cls, Object pk) {
		// TODO Auto-generated method stub
		String key=itemKey(cls, pk);
		File cacheFile=new File(baseDir+"/"+key);
		CacheItem<E> ret=null;
		try (
				FileInputStream fos=new FileInputStream(cacheFile);
				ObjectInputStream oos=new ObjectInputStream(fos)
				){
			
			
			ret=(CacheItem<E>) oos.readObject();                 
			
			
		}catch (Exception ex) {
			
		}
        return ret;
	}

	@Override
	public <E> String put(E ret, Object pk, long timeoutMillsec) {
		// TODO Auto-generated method stub
		String key=itemKey(ret.getClass(), pk);
		File cacheFile=new File(baseDir+"/"+key);
		try (
				FileOutputStream fos=new FileOutputStream(cacheFile);
				ObjectOutputStream oos=new ObjectOutputStream(fos)
				){
			
			CacheItem<E> cacheItem=new CacheItem<E>(ret, key, timeoutMillsec);
			oos.writeObject(cacheItem);                 
			
			
		}catch (IOException ex) {
			key=null;
		}
      return key;
	}

	@Override
	protected Collection<CacheItem> coll() {
		// TODO Auto-generated method stub
		Collection<CacheItem> ret=new ArrayList<>();
		for(File cacheFile: baseDir.listFiles()) {
			CacheItem item=null;
			try (
					FileInputStream fos=new FileInputStream(cacheFile);
					ObjectInputStream oos=new ObjectInputStream(fos)
					){
				
				
				item=(CacheItem) oos.readObject();                 
				ret.add(item);
				
			}catch (Exception ex) {
				
			}
		}
		
		return ret;
	}

}
