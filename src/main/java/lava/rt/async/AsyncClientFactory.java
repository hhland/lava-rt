package lava.rt.async;

public interface AsyncClientFactory {

	abstract AsyncGenericQueryClient newInstance();
}
