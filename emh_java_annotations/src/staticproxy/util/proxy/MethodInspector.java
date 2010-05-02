package staticproxy.util.proxy;

public interface MethodInspector {
	public void after(String methodName, Object... args);

	public void before(String methodName, Object... args);
}