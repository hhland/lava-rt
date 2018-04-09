package lava.rt.common;

import java.util.stream.Stream;

public class LangCommon {

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
}
