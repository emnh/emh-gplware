package annotations.stage2.process;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaXmlSourceTransformation;
import annotations.stage2.transformers.XmlTransformer;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

import exceptions.FatalHandler;

@SupportedAnnotationTypes(value = { "*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class XmlSourceDumper extends BWAnnotationProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		//boolean dumpxml = this.processingEnv.getOptions().containsKey("dumpxml");
		
		if (!roundEnv.processingOver()) {
			System.out.println("Dumping XML");
			dumpXML(roundEnv);
		}

		// don't claim the annotations so nobody else will see them
		return false;
	}
	
	private void dumpXML(RoundEnvironment roundEnv) {
		// test xml dump
			
		for (Element element : roundEnv.getRootElements()) {
			
			//System.out.printf("visiting: %s %s\n", element, element.getKind());
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
					getCurrentXmlTransform(),
					this
					);
			
			//Class.forName("annotations.transformers.XmlTransformer");
			if (tp != null) {
				this.currentCompilationUnit.accept(v);
				try {
					Writer wr = createXmlResource(element);
					wr.write(v.getTransform().getTransformed());
					wr.close();
				} catch (IOException e) {
					FatalHandler.handle("IO error", e);
				}
			}
			
		}

	}

	public JavaXmlSourceTransformation getCurrentXmlTransform() {
		JavaXmlSourceTransformation transform = null;
		try {
			transform = new JavaXmlSourceTransformation(
					this.currentCompilationUnit.getPackageName().toString(),
					getCurrentSourceFile());
		} catch (IOException e) {
			FatalHandler.handle(e);
		}
		return transform;
	}
	
}
