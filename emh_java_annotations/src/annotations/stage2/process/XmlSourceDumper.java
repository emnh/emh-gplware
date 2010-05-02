package annotations.stage2.process;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

import exceptions.FatalHandler;

import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;
import annotations.stage2.transformers.XmlTransformer;

@SupportedOptions({"dumpxml"})
public class XmlSourceDumper extends BWAnnotationProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		boolean dumpxml = this.processingEnv.getOptions().containsKey("dumpxml");
		
		if (!roundEnv.processingOver()) {
			if (dumpxml) {
				System.out.println("Dumping XML");
				dumpXML(roundEnv);
			}
		}

		// claim the annotations so nobody else will see them
		return true;
	}

	
	private void dumpXML(RoundEnvironment roundEnv) {
		// test xml dump
			
		for (Element element : roundEnv.getRootElements()) {
			
			System.out.printf("visiting: %s %s\n", element, element.getKind());
			//JCTree tree = (JCTree) this.trees.getTree(element);
			
			TreePath tp = this.trees.getPath(element);
			
			if (tp == null) {
				System.out.printf("warning: FAILED to get treepath for %s. " +
						"add the file to build command line.\n", 
						element);
				System.out.println(element.getClass());
				continue;
			}
			
			this.currentCompilationUnit = (JCCompilationUnit) tp.getCompilationUnit();
			
			XmlTransformer v = new XmlTransformer(
					createXmlResource(element),
					this
					);
		
			//Class.forName("annotations.transformers.XmlTransformer");
			if (tp != null) {
				this.currentCompilationUnit.accept(v);
				try {
					v.finish();
				} catch (IOException e) {
					FatalHandler.handle("failed to close file", e);
				}
			}
			
		}

	}

}
