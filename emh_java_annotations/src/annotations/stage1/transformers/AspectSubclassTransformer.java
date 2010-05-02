package annotations.stage1.transformers;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import staticproxy.util.ClassName;
import staticproxy.util.proxy.ProxySourceGeneratorFactory;
import staticproxy.util.proxy.VirtualProxySourceGenerator;
import annotations.stage1.defs.AspectSubclass;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;

import com.sun.tools.javac.tree.JCTree.Visitor;

import exceptions.FatalHandler;

class Bogus2 extends Visitor {
	
}

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
		
		//processor.getClass(Visitor.class.toString().replace('$', '.'));
		
		// standard procedure for getting class parameters from Annotation		
//		try {
//			subject = a2.value();
//		} catch (MirroredTypeException e1) {
//			TypeMirror tm = e1.getTypeMirror();
//			subject = processor.getClass(tm.toString());
//		}
//		try {
//			generator = a2.generator();
//		} catch (MirroredTypeException e1) {
//			TypeMirror tm = e1.getTypeMirror();
//			generator = processor.getClass(tm.toString());
//		}
		
		
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
