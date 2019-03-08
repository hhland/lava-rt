package lava.rt.connectionpool;

public interface RequestFactory {
	
	
	public Request newRequest();

	
	public Request newProbeRequest();
}
