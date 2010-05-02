package staticproxy.util.proxy;

import static staticproxy.util.proxy.Util.prettyPrint;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class VirtualAspectProxySourceGenerator extends VirtualProxySourceGeneratorNotThreadsafe {

	public class Factory extends ProxySourceGeneratorFactory {

		@Override
		public VirtualProxySourceGenerator create(Class<?> subject,
				Class<? extends MethodInspector> realSubject) {
			return new VirtualAspectProxySourceGenerator(subject, realSubject);
		}

	}

//	@Override
//	protected void addRealSubjectCreation(PrintWriter out, String name,
//			String realName) {
//		//super.addRealSubjectCreation(out, name, realName);
//	}
	
	public VirtualAspectProxySourceGenerator(Class<?> subject, 
			Class<? extends MethodInspector> realSubject) {
		super(subject, realSubject);
		this.implemented.add(prettyPrint(MethodInspector.class));
		this.isAbstract = true;
//		if (!subject.isAssignableFrom(realSubject)) {
//			throw new IllegalArgumentException(
//					String.format(
//							"realSubject(%s) must be an instance of subject(%s)",
//							realSubject,
//							subject
//							));
//		}
	}

//	@Override
//	protected void addImports(PrintWriter out) {
//		super.addImports(out);
//		indentSame(out); 
//		out.printf("import %s;", MethodInspector.class.getName());
//		newLine(out);
//		newLine(out);
//	}


	@Override
	protected void addMethodBody(PrintWriter out, Method m) {
		indentSame(out);
		addMethodInspector(out);
		out.printf(".before(\"%s\"", m.getName());
		if (m.getParameterTypes().length > 0) {
			out.print(", ");
			addMethodCall(out, m);
		}
		out.print(");");
		super.addMethodBody(out, m);
	}
	
	public void addMethodInspector(PrintWriter out) {
		out.printf("((MethodInspector) %s)", getRealSubject());
	}

	@Override
	protected void addMethodStatementBeforeReturn(PrintWriter out, Method m) {
		indentSame(out);
		addMethodInspector(out);
		out.printf(".after(\"%s\"", m.getName());
		if (m.getParameterTypes().length > 0) {
			out.print(", ");
			addMethodCall(out, m);
		}
		out.print(");");
		super.addMethodStatementBeforeReturn(out, m);
	}

	@Override
	protected void addProxyBody(PrintWriter out) {
		addProxiedMethods(out);
	}
	
}