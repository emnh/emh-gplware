package staticproxy.util.proxy;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.annotation.processing.SupportedAnnotationTypes;

@SupportedAnnotationTypes(value = { "annotations.stage2.defs.*" })
public class AspectSubclassGenerator extends VirtualAspectProxySourceGenerator {

	@Override
	public String getRealSubject() {
		return "super";
	}

	@Override
	public void addMethodInspector(PrintWriter out) {
		out.printf("this");
	}

	public static class Factory extends ProxySourceGeneratorFactory {

		@Override
		public VirtualProxySourceGenerator create(Class<?> subject,
				Class<? extends MethodInspector> realSubject) {
			return new AspectSubclassGenerator(subject, realSubject);
		}
	}
	
	public AspectSubclassGenerator(Class<?> subject,
			Class<? extends MethodInspector> realSubject) {
		super(subject, realSubject);
	}

//	@Override
//	protected void addClassDefinition(PrintWriter out) {
//		addImports(out);
//		out.printf("public class %s extends %s {%n", proxy,
//				prettyPrint(realSubject), prettyPrint(subject));
//		//super.addClassDefinition(out);
//	}

	@Override
	protected void addMethodBodyDelegatingToRealSubject(PrintWriter out,
			Method m) {
		// TODO Auto-generated method stub
		out.printf("super.%s(", m.getName());
		addMethodCall(out, m);
	}

}
