package lava.rt.linq.sql;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.Map.Entry;

import lava.rt.common.ReflectCommon;
import lava.rt.common.LangCommon;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.DataContext;
import lava.rt.linq.Entity;
import lava.rt.linq.VerifyExecption;
import lava.rt.linq.sql.SelectCommand.PagingSelectCommand;

import lava.rt.wrapper.ListWrapper;

public interface SqlDataContext extends DataContext {

	
	
	public <M extends Entity> ListWrapper<M> listEntities(Class<M> cls, PagingSelectCommand command ) throws CommandExecuteExecption ;
	
	public <M extends Entity> List<M> listEntities(Class<M> cls, SelectCommand command) throws CommandExecuteExecption ;
	
	
	public String executeQueryJsonList(PagingSelectCommand command) throws CommandExecuteExecption;
	public String executeQueryJsonList(SelectCommand command) throws CommandExecuteExecption;
	
	public Object[][] executeQueryArray(PagingSelectCommand command) throws CommandExecuteExecption ;
	public Object[][] executeQueryArray(SelectCommand command) throws CommandExecuteExecption ;
	
	static final Random RANDOM = new Random();

	public static void verify(Entity entity) throws VerifyExecption {
		Map<String, Field> fieldMap = ReflectCommon.getFieldMap(entity.getClass());
		List<String[]> ret = new ArrayList<>();
		try {
			for (Entry<String, Field> en : fieldMap.entrySet()) {
				Field field = en.getValue();
				field.setAccessible(true);
				ColumnMeta columnMeta = field.getAnnotation(ColumnMeta.class);
				if (columnMeta == null)
					continue;
				Object value = field.get(entity);
				if (value == null) {
					if (columnMeta.nullable()) {
						continue;
					} else {
						ret.add(new String[] { en.getKey(), "-1", columnMeta.comments() + " can't be null" });
					}
				} else if (value instanceof String) {

					if (value.toString().length() > columnMeta.dataLength()) {
						ret.add(new String[] { en.getKey(), "-2",
								columnMeta.comments() + " length can't more than " + columnMeta.dataLength() });
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {}
		if(ret.size()>0) {
			throw new VerifyExecption(ret); 
		}
	}

	public static int fillRandom(Entity entity) {
		Map<String, Field> fieldMap = ReflectCommon.getAllDeclaredFieldMap(entity.getClass());
	    int ret = 0;
		try {
			for (Entry<String, Field> en : fieldMap.entrySet()) {
				Field field = en.getValue();
				field.setAccessible(true);
				ColumnMeta columnMeta = field.getAnnotation(ColumnMeta.class);
				ret++;
				if(field.getType().equals(String.class)) {
					String val=LangCommon.createRandomEn(RANDOM,columnMeta.dataLength()-5);
					field.set(entity, val);
				}else if(field.getType().equals(Integer.class)) {
					field.set(entity, RANDOM.nextInt());
				}else if(field.getType().equals(Double.class)) {
					field.set(entity, RANDOM.nextGaussian());
				}else if(field.getType().equals(Float.class)) {
					field.set(entity, RANDOM.nextFloat());
				}else if(field.getType().equals(BigDecimal.class)) {
					field.set(entity, new BigDecimal(RANDOM.nextGaussian()));
				}else if(field.getType().equals(Date.class)) {
					field.set(entity,new Date());
				}else if(field.getType().equals(Timestamp.class)) {
					field.set(entity,new Timestamp(System.currentTimeMillis()));
				}
				else {
					ret--;
				}
				
				

			}
		} catch (Exception e) {}
        return ret;		
	}

	@Documented
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ColumnMeta {

		int dataLength();

		String comments();

		boolean nullable();

	}

	@Documented
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ObjectMeta {

		String comments();

	}

}
