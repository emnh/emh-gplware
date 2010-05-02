package annotations.stage1.transformers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import staticproxy.util.ClassName;
import staticproxy.util.proxy.ProxySourceGeneratorFactory;
import staticproxy.util.proxy.VirtualProxySourceGenerator;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;
import exceptions.FatalHandler;

public class AspectSubclassTransformer extends Transformer {
	
		
	@Override
	public AspectSubclassTransformer init(
			JavaSourceTransformation transform,
			BWAnnotationProcessor processor, 
			Annotation a, Element e)  {
		super.init(transform, processor, a, e);
		
		String originalName = e.asType().toString();
		String className = processor.getGeneratedName(originalName);
		
		//Class<? extends MethodInspector> realSubject = processor.getClass(originalName);
		Class<?> subject = null;
		Class<?> generator = null;
		VirtualProxySourceGenerator sgen = null;
		
		subject = processor.getAnnotationClassValue(e, a, "value");
		generator = processor.getAnnotationClassValue(e, a, "generator");		
		
		try {
			sgen = ((ProxySourceGeneratorFactory) generator.newInstance()).
					create(subject, null);
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		

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
		srcpw.close();
		return this;
	}

}
