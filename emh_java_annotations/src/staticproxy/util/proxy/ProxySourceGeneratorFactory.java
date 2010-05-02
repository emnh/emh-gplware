package staticproxy.util.proxy;

public abstract class ProxySourceGeneratorFactory {

	public abstract VirtualProxySourceGenerator create(Class<?> subject, 
			Class<? extends MethodInspector> realSubject);
	
}
