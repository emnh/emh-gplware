package annotations.stage1.transformers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import staticproxy.util.ClassName;
import staticproxy.util.proxy.MethodInspector;
import staticproxy.util.proxy.ProxySourceGeneratorFactory;
import staticproxy.util.proxy.VirtualProxySourceGenerator;
import annotations.stage1.defs.AspectSubclass;
import annotations.stage1.defs.StaticProxy;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;
import exceptions.FatalHandler;

public class StaticProxyTransformer extends Transformer {

	@Override
	public StaticProxyTransformer init(
			JavaSourceTransformation transform,
			BWAnnotationProcessor processor, 
			Annotation a, Element e)  {
		String originalName = e.asType().toString();
		String className = processor.getGeneratedName(originalName);
		
		Class<? extends MethodInspector> realSubject = processor.getClass(originalName);
		Class<?> subject = null;
		VirtualProxySourceGenerator sgen = null;
		
		//if (a.annotationType())
		Annotation a2 = realSubject.getAnnotation(a.annotationType());
		try {
			if (a2 instanceof StaticProxy) {
				StaticProxy a3 = (StaticProxy) a2;
				subject = a3.value();
				sgen = ((ProxySourceGeneratorFactory) a3.generator().newInstance()).
						create(subject, realSubject);
			} else {
				AspectSubclass a3 = (AspectSubclass) a2;
				subject = a3.value();
				sgen = ((ProxySourceGeneratorFactory) a3.generator().newInstance()).
						create(subject, realSubject);
			}
		} catch (InstantiationException e1) {
			FatalHandler.handle(e1);
		} catch (IllegalAccessException e1) {
			FatalHandler.handle(e1);
		}
		
		//StaticProxy annotation = realSubject.getAnnotation(StaticProxy.class);

		

		//		System.out.printf("src class name: %s\n", className);
		//		System.out.printf("subject: %s\n", subject);
		//		System.out.printf("orig: %s\n", originalName);

		String simpleName = ClassName.getRelativeName(className);
		sgen.setPackageName(ClassName.getPackageName(className));
		sgen.setProxyName(simpleName);

		JavaFileObject srcfile = processor.createSourceFile(className);
		PrintWriter srcpw = null;
		try {
			srcpw = new PrintWriter(srcfile.openWriter());
		} catch (IOException e1) {
			FatalHandler.handle("couldn't write class: " + className, e1);
		}
		sgen.generateProxyClass(srcpw);
		srcpw.printf("public class %s {}\n", simpleName);
		srcpw.close();
		return this;
	}

}
