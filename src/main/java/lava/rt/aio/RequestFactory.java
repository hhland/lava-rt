package lava.rt.aio;

public interface RequestFactory<S> {
	
	
	public Request<S> newRequest();

	
	public Request<S> newProbeRequest();
}
