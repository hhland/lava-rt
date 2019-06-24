package lava.rt.test.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import lava.rt.linq.Entity;


public class JC2010_ENTERPRISE_DBImpl extends BaseJC2010_ENTERPRISE_DBImpl {

	DataSource[] dataSources;
	
	Map<String, Cacheable<? extends Entity>> cacheMap=new HashMap<>();
	
	public JC2010_ENTERPRISE_DBImpl(DataSource... dataSources) {
		super();
		this.dataSources = dataSources;
	}



	@Override
	protected DataSource[] getDataSources() {
		// TODO Auto-generated method stub
		return dataSources;
	}



	@Override
	protected <E extends Entity> Cacheable<E> cacheGet(Class<E> cls, Object pk) {
		// TODO Auto-generated method stub
		String key=cls.getName()+":"+pk;
		return (Cacheable<E>) cacheMap.get(key);
	}



	@Override
	protected <E extends Entity> void cachePut(E entity, Object pk) {
		// TODO Auto-generated method stub
		String key=entity.getClass().getName()+":"+pk;
		Cacheable<E> cacheable=new Cacheable<E>(entity, 1000*10);
		cacheMap.put(key, cacheable);
	}
	
	
	

}
