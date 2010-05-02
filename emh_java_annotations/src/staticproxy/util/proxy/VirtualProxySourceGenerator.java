package staticproxy.util.proxy;

import static staticproxy.util.proxy.Util.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;

public abstract class VirtualProxySourceGenerator {
	protected final Class<?> subject;
	protected final Class<?> realSubject;
	protected String proxy;
	protected String packageName;
	String extended;
	ArrayList<String> implemented = new ArrayList<String>();
	boolean isAbstract = false;
	
	protected CharSequence charSequence;
	
	protected final String RETVARNAME = "ret";
	
	protected int currentIndent = 0;

	protected static String makeProxyName(Class<?> subject, Concurrency type) {
		return "$$_"
				+ subject.getName().replace('.', '_')
				+ "Proxy_"
				+ Integer.toHexString(System.identityHashCode(subject
						.getClassLoader())) + "_" + type;
	}

	public VirtualProxySourceGenerator(Class<?> subject, Class<?> realSubject,
			Concurrency type) {
		this.subject = subject;
		this.realSubject = realSubject;
		this.proxy = makeProxyName(subject, type);
		if (subject.isInterface()) {
			this.implemented.add(prettyPrint(subject));
		} else {
			this.extended = prettyPrint(subject);
		}
	}
	protected void addClassDefinition(PrintWriter out) {
		addImports(out);
		// class name
		indentSame(out); out.printf("%spublic class %s",
				isAbstract ? "abstract " : "",
				proxy);
		indentEnter();
		if (extended != null) {
			indentSame(out); out.printf("extends %s", extended);
		}
		if (implemented != null) {
			indentSame(out); out.print("implements ");
			boolean first = true;
			for (String im : implemented) {
				if (!first) {
					out.print(", ");
				} else {
					first = false;
				}
				out.print(im);
			}
		}
		out.printf(" {");
		indentExit();
	}
	protected void addImports(PrintWriter out) {
	}

	protected void addMethodBody(PrintWriter out, Method m) {
		addReturnAssignment(out, m);
		addMethodStatementBeforeReturn(out, m);
		// addMethodBodyDelegatingToRealSubject(out, m);
		addReturnStatement(out, m);
	}

	protected void addMethodBodyDelegatingToRealSubject(PrintWriter out,
			Method m) {
		out.printf("%s.%s(", getRealSubject(), m.getName());
		addMethodCall(out, m);
	}

	protected void addMethodCall(PrintWriter out, Method m) {
		Class<?>[] types = m.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			String next = i == types.length - 1 ? "" : ", ";
			out.printf("p%d%s", i, next);
		}
	}

	protected void addMethodSignature(PrintWriter out, Method m) {
		indentSame(out);
		out.printf("public %s", prettyPrint(m.getReturnType()));
		out.printf(" %s(", m.getName());
		addParameterList(out, m);
		out.printf(") {");
	}

	/**
	 * Override if you want to do something right before method exit
	 * 
	 * @param out
	 * @param m
	 */
	protected void addMethodStatementBeforeReturn(PrintWriter out, Method m) {

	}

	private void addPackageStatement(PrintWriter out) {
		if (packageName != null) { 
			out.printf("package %s;", packageName);
			newLine(out);
		}
	}

	protected void addParameterList(PrintWriter out, Method m) {
		Class<?>[] types = m.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			String next = i == types.length - 1 ? "" : ", ";
			out.printf("%s p%d%s", prettyPrint(types[i]), i, next);
		}
	}

	protected boolean addProxiedMethod(PrintWriter out, Method m) {
		if (Modifier.isFinal(m.getModifiers()))
			return false;
		addMethodSignature(out, m);
		indentEnter();
		addMethodBody(out, m);
		indentExit(out); out.printf("}");
		return true;
	}

	protected void addProxiedMethods(PrintWriter out) {
		for (Method m : subject.getMethods()) {
			if (addProxiedMethod(out, m)) {
				newLine(out); // method spacing
			}
		}
		addToStringIfInterface(out);
	}

	protected void addProxyBody(PrintWriter out) {
		addRealSubjectCreation(out, prettyPrint(subject),
				prettyPrint(realSubject));
		addProxiedMethods(out);
	}

	protected abstract void addRealSubjectCreation(PrintWriter out,
			String name, String realName);

	protected void addReturnAssignment(PrintWriter out, Method m) {
		indentSame(out);
		if (m.getReturnType() != void.class) {
			out.printf("%s %s = ", prettyPrint(m.getReturnType()), RETVARNAME);
		}
		addMethodBodyDelegatingToRealSubject(out, m);
		out.printf(");");
	}

	protected void addReturnKeyword(PrintWriter out, Method m) {
		if (m.getReturnType() != void.class) {
			out.print("return ");
		}
	}

	protected void addReturnStatement(PrintWriter out, Method m) {
		if (m.getReturnType() != void.class) {
			indentSame(out);
			addReturnKeyword(out, m);
			out.printf("%s;", RETVARNAME);
		}
	}

	protected void addToStringIfInterface(PrintWriter out) {
		if (subject.isInterface()) {
			indentSame(out); out.println("public String toString() {");
			indentEnter(out); out.printf("return %s.toString();\n", getRealSubject());
			indentExit(out); out.println("}");
		}
	}

	public void generateProxyClass(PrintWriter out) {
		addPackageStatement(out);
		addClassDefinition(out);
		indentEnter();
		addProxyBody(out);
		indentExit(out); out.printf("}");
		out.close();
	}

	public CharSequence getCharSequence() {
		if (charSequence == null) {
			StringWriter sw = new StringWriter();
			generateProxyClass(new PrintWriter(sw));
			charSequence = sw.getBuffer();
		}
		return charSequence;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getProxyName() {
		return proxy;
	}

	public String getRealSubject() {
		return "realSubject()";
	}

	protected void indent(PrintWriter out) {
		indent(out, currentIndent);
	}

	protected void indent(PrintWriter out, int level) {
		for (int i = 0; i < level; i++) {
			out.print("  ");
		}
	}

	protected void indentEnter() {
		currentIndent++;
	}

	protected void indentEnter(PrintWriter out) {
		currentIndent++;
		newLine(out);
		indent(out, currentIndent);
	}
	
	protected void indentExit() {
		currentIndent--;
	}
	
	protected void indentExit(PrintWriter out) {
		indentExit();
		newLine(out);
		indent(out, currentIndent);
	}
	
	protected void indentSame(PrintWriter out) {		
		newLine(out);
		indent(out, currentIndent);
	}
	
	protected void newLine(PrintWriter out) {
		out.println();
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setProxyName(String name) {
		proxy = name;
	}
}