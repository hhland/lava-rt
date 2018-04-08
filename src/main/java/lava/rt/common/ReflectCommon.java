package lava.rt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReflectCommon {

	public static <T> boolean isIn(T obj1, T... objs) {
		for (T obj : objs) {
			if (isEquals(obj1, obj)) {
				return true;
			}
		}
		return false;
	}

	@SafeVarargs
	public static <T> boolean isEquals(T... objects) {
		for (int i = 0,j=1; j < objects.length; j++) {
			T objecti = objects[i],objectj=objects[j];
			if (objecti != null && !objecti.equals(objectj))
				return false;
			else if (objecti == null && objectj == null)
				continue;
		}

		return true;
	}

	public static void attr(Object obj, String field, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String setterName = "set" + TextCommon.firstCharToUpperCase(field);
		obj.getClass().getMethod(setterName, value.getClass()).invoke(obj, value);
	}

	public static <T> T attr(Object obj, String field) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String getterName = "get" + TextCommon.firstCharToUpperCase(field);
		T t = null;
		Object value = obj.getClass().getMethod(getterName).invoke(obj);
		if (value != null)
			t = (T) value;
		return t;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface CopyField {

		String targetName();

		Class targetType() default String.class;

		String[] targetValueRef() default {};

	}
	
	public static float loadProperties(Properties properties,Object object) {
		return 0;
	}
	
	public static float loadProperties(Properties properties,Class cls) {
		
		return 0;
	}

	public static float copyFieldByMethod(Object source, Object target) {

		float copy_count = 0;
		Field[] targetFields = target.getClass().getDeclaredFields();

		Map<String, CopyFieldStruct> sourceAnnFieldStruts = getCopyFieldStructs(source);

		for (Field targetField : targetFields) {

			try {

				if (sourceAnnFieldStruts.containsKey(targetField.getName())) {
					CopyFieldStruct dcMessageFieldStruct = sourceAnnFieldStruts.get(targetField.getName());
					Field sourceAnnField = dcMessageFieldStruct.field;
					CopyField ann = dcMessageFieldStruct.dcCopyField;

					Object sourceValue = sourceAnnField.get(source);
					if (sourceValue != null && !sourceValue.getClass().equals(ann.targetType())) {

						Object targetValue = sourceValue;

						if (sourceValue instanceof Long && Date.class.equals(ann.targetType())) {
							Date dt = Calendar.getInstance().getTime();
							dt.setTime((long) sourceValue);
							targetValue = dt;
						}
						targetField.set(target, targetValue);
					} else if (ann.targetValueRef().length > 0) {

						Object targetValue = sourceValue;
						Map<String, String> valueMap = toMap(ann.targetValueRef());
						targetValue = valueMap.get(sourceValue);
						targetField.set(target, targetValue);
					}

					else {
						targetField.set(target, sourceValue);
					}

				} else {
					String sourceMethodName = "get" + TextCommon.firstCharToUpperCase(targetField.getName());
					Method sourceMethod = source.getClass().getMethod(sourceMethodName);
					Object sourceValue = sourceMethod.invoke(source);
					targetField.set(target, sourceValue);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block

				// if(DCMFactory.isDebug) {
				System.out.println(
						"can't find target filed:" + target.getClass().getName() + "." + targetField.getName());
				// }
				continue;
			}
			copy_count++;

		}
		float prec = copy_count / targetFields.length;
		// if(DCMFactory.isDebug) {
		String msg = MessageFormat.format("copy prec: {0}/{1}={2}", copy_count, targetFields.length, prec);
		System.out.println(msg);
		// }

		return prec;
	}

	private static Map<String, String> toMap(String[] values) {
		Map<String, String> map = new HashMap<String, String>();

		for (int i = 0, j = 1; j < values.length; i += 2, j += 2) {
			map.put(values[i], values[j]);
		}

		return map;
	}

	public static class CopyFieldStruct {

		public CopyFieldStruct(CopyField CopyField, Field field) {
			this.dcCopyField = CopyField;
			this.field = field;
		}

		public CopyField dcCopyField;
		public Field field;
	}

	protected static Map<String, CopyFieldStruct> getCopyFieldStructs(Object source) {

		Map<String, CopyFieldStruct> sourceAnnFields = new HashMap<String, CopyFieldStruct>();

		for (Class<?> clazz = source.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field[] sourceFields = clazz.getDeclaredFields();

			for (Field sourceField : sourceFields) {
				sourceField.setAccessible(true);
				CopyField ann = sourceField.getAnnotation(CopyField.class);
				if (ann != null) {
					CopyFieldStruct dfs = new CopyFieldStruct(ann, sourceField);
					sourceAnnFields.put(ann.targetName(), dfs);
				}
			}
		}

		return sourceAnnFields;
	}

}
