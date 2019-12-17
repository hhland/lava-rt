package lava.rt.test.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import lava.rt.cache.CacheItem;
import lava.rt.cache.MemoryCacheContainer;
import lava.rt.linq.Entity;


public class JC2010_ENTERPRISE_DBImpl extends JC2010_ENTERPRISE_DBBase {

	DataSource[] dataSources;
	
	Map<String, CacheItem<? extends Entity>> cacheMap=new HashMap<>();
	
	public JC2010_ENTERPRISE_DBImpl(DataSource... dataSources) {
		super();
		this.dataSources = dataSources;
	}



	

	
	static MemoryCacheContainer cacheContainer=new MemoryCacheContainer();

	static {
		cacheContainer.getCleanItemThread().start();
	}

	@Override
	protected <E extends Entity> CacheItem<E> cacheGet(Class<E> cls, Object pk) {
		// TODO Auto-generated method stub
		return cacheContainer.get(cls, pk);
		
	}



	@Override
	protected <E extends Entity> void cachePut(E entity, Object pk) {
		// TODO Auto-generated method stub
		cacheContainer.put(entity, pk, 1000*60);
	}



	@Override
	protected DataSource[] getReadDataSources() {
		// TODO Auto-generated method stub
		return dataSources;
	}



	@Override
	protected DataSource[] getWriteDataSources() {
		// TODO Auto-generated method stub
		return dataSources;
	}



	
	
	
	

}
