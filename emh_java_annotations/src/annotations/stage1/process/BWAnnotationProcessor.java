package annotations.stage1.process;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import staticproxy.util.ClassName;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

import exceptions.FatalHandler;

@SupportedAnnotationTypes(value = { "annotations.stage1.defs.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BWAnnotationProcessor extends AbstractProcessor {

	protected Trees trees;
	protected ProcessingEnvironment processingEnv;
	HashMap<String, JavaSourceTransformation> transforms = new HashMap<String, JavaSourceTransformation>();
	protected JCCompilationUnit currentCompilationUnit;

	public static String getPackage(Element cls) {
		if (cls.getKind() != ElementKind.CLASS) {
			return null;
		}
		Element pkg = cls.getEnclosingElement();
		return pkg.getSimpleName().toString();
	}

	public FileObject createResource(String qualifiedName, String extension) {
		FileObject srcfile = null;
		try {
			srcfile = this.processingEnv.getFiler().createResource(
					StandardLocation.SOURCE_OUTPUT,
					ClassName.getPackageName(qualifiedName), 
					ClassName.getRelativeName(qualifiedName) + "." + extension
					);
		} catch (FilerException e1) {
			if (!e1.getMessage().matches("Attempt to recreate a file.*")) {
				e1.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return srcfile;
	}
	
	public JavaFileObject createSourceFile(String className) {
		JavaFileObject srcfile = null;
		try {
			srcfile = this.processingEnv.getFiler().createSourceFile(className);
		} catch (FilerException e1) {
			if (!e1.getMessage().matches("Attempt to recreate a file.*")) {
				e1.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return srcfile;
	}

	public String genWrap(String replacement) {
		return "/*GEN*/" + replacement + "/*END*/";
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getClass(String name) {
		Class<T> ret = null;
		boolean done = false;
		String origname = name;
		while (!done) {
			try {
				ret = (Class<T>) Class.forName(name);
				done = true;
			} catch (ClassNotFoundException e) {
				// try to make binary form of name and look up again
				String[] parts = name.split("\\.");
				String newname = name.substring(0, name.length() - parts[parts.length - 1].length() - 1)
						 		+ "$" + parts[parts.length - 1];
				
				//name.replaceFirst("\\.\\([^\\.]*\\)$", "$\\1");
				//System.out.println("newname: " + newname);
				if (!newname.equals(name)) {
					//System.out.println("newname: " + newname);
					name = newname;
				} else {
					FatalHandler.handle("failed to load class: " + origname, e);
				}
			}
		}
		return ret;
	}

	private JavaFileObject getCurrentSourceFile() {
		if (this.currentCompilationUnit == null) {
			throw new IllegalStateException("must set currentCompilationUnit") ;
		}
		return this.currentCompilationUnit.getSourceFile();
	}

	public JavaSourceTransformation getNewCurrentTransform() {
		String fpath = getCurrentSourceFile().toString();
		JavaSourceTransformation transform = null;
		try {
			transform = new JavaSourceTransformation(new File(fpath));
		} catch (IOException e) {
			FatalHandler.handle(e);
		}
		return transform;
	}
	
	public JavaSourceTransformation getCurrentTransform() {
		String fpath = getCurrentSourceFile().toString();
		JavaSourceTransformation transform = null;
		if (this.transforms.containsKey(fpath)) {
			transform = this.transforms.get(fpath);
		} else {
			transform = getNewCurrentTransform();
			this.transforms.put(fpath, transform);
		}
		return transform;
	}

	public long getEndPos(JCTree tree) {
		// System.out.printf("endp: %s\n",
		// this.currentCompilationUnit.endPositions);
		// return tree.getEndPosition(this.currentCompilationUnit.endPositions);
		return this.trees.getSourcePositions().getEndPosition(
				this.currentCompilationUnit, tree);
	}

	/**
	 * Supports 2 naming schemes
	 * 
	 * If forClassName ends with In:
	 * Input: control.TestIn
	 * Output: control.Test
	 * 
	 * Otherwise:
	 * Input: control.Test
	 * Output: control.TestGen
	 * 
	 * @param forClassName
	 * @return
	 */
	public String getGeneratedName(String forClassName) {
		if (forClassName.endsWith("In")) {
			return forClassName.replaceFirst("In$", "");
		} else {
			return forClassName + "Gen";
		}
	}

	public long getStartPos(JCTree tree) {
		return this.trees.getSourcePositions().getStartPosition(
				this.currentCompilationUnit, tree);
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		// this.make = TreeMaker
		// .instance(((JavacProcessingEnvironment) processingEnv)
		// .getContext());
		this.trees = Trees.instance(processingEnv);
		this.processingEnv = processingEnv;
		super.init(processingEnv);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		//		for (TypeElement element : annotations) {
		//			//			if (element instanceof TypeDef) {
		//			//				TypeDef td =
		//			//			}
		//			System.out.println(element.getClass());
		//			System.out.println(element.getQualifiedName());
		//			System.out.println(element.getEnclosedElements());
		//		}

		//boolean processAnnotations = this.processingEnv.getOptions().containsKey("stage1");
		
		if (!roundEnv.processingOver()) {

			//if (processAnnotations) {
			System.out.println("Processing annotations");
			for (TypeElement element : annotations) {
				System.out.println("doing @" + element.getQualifiedName());
				Class<Annotation> annotation = null;
				try {
					annotation = (Class<Annotation>) Class.forName(element.getQualifiedName().toString());
				} catch (ClassNotFoundException e) {
					FatalHandler.handle(e);
				}
				processAnnotation(annotation, roundEnv);
			}
			//}

		} else {
			
			for (JavaSourceTransformation transform : this.transforms.values()) {
				try {
					System.out.printf("DUMP(%s):\n%s", transform.getFile(),
							transform.getTransformed());
				} catch (IOException e) {
					System.out.printf("error transforming \"%s\": %s\n",
							transform.getFile(), e);
				}
				//transform.getFile()
				//				String className = "";
				//				createSourceFile(className);
			}
			// this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
			// this.tally + " roman numerals processed.");
		}

		// claim the annotations so nobody else will see them
		return true;
	}
		
	public Writer createXmlResource(Element e) {
		String xmlname = ((TypeElement) e).getQualifiedName().toString();
		FileObject jsrc = createResource(xmlname, "xml");
		System.out.printf("writing %s\n", xmlname + ".xml");
		//getTransform().substring(beginIndex, endIndex);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(jsrc.openWriter());
		} catch (IOException e1) {
			FatalHandler.handle(e1);
		}
		return pw;
	}

	public void processAnnotation(Class<? extends Annotation> annotationclass, RoundEnvironment roundEnv) {
		System.out.printf("working: %s\n", annotationclass);
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotationclass);

		for (Element element : elements) {
			Annotation annotation = element.getAnnotation(annotationclass);
			BindTransformer btf = annotationclass.getAnnotation(BindTransformer.class);

			boolean elementHasAnnotation = (annotation != null);
			// TODO: use @Target on the annotations instead
			boolean elementIsClass = (element.getKind() == ElementKind.CLASS);
			boolean annotationHasProcessor = (btf != null) && (btf.value() != null);
			System.out.printf("@%s(%s): hasAnnotation=%s, isClass=%s, hasProcessor=%s\n",
					annotationclass.getName(),
					element.getSimpleName().toString(),
					elementHasAnnotation, elementIsClass, annotationHasProcessor);

			if (elementHasAnnotation && elementIsClass & annotationHasProcessor) {

				Class<? extends Transformer> ctf = btf.value();
				Transformer transformer = null;
				try {
					transformer = ctf.newInstance();
				} catch (IllegalAccessException e) {
					FatalHandler.handle(e);
				} catch (InstantiationException e) {
					FatalHandler.handle(e);
				}
				this.currentCompilationUnit = (JCCompilationUnit) this.trees
				.getPath(element).getCompilationUnit();
				
				transformer = transformer.init(getNewCurrentTransform(), 
						this, annotation, element);

				//	System.out.printf("name of %s: %s\n", element.getClass(), element
				//	.getSimpleName());
				// getSourceFile(each);

				JCTree tree = (JCTree) this.trees.getTree(element);
				//System.out.printf("classname: %s\n", element.getSimpleName());
				// System.out.printf("name: %s\n", ((JCClassDecl)
				// tree).get);

				// we need the end positions for replacing code
				if (this.currentCompilationUnit.endPositions == null) {
					throw new RuntimeException(
					"you need to supply -Xjcov option to javac");
				}

				tree.accept(transformer);
				// System.out.println(tree);
			}
		}
	}

	/**
	 * Insert string in source file.
	 * Strings at same position appear in order of insertion.
	 * @param pos
	 * @param insert
	 */
	public void insertAt(int pos, String insert) {
		JavaSourceReplacement repl = new JavaSourceReplacement(
				pos, 
				pos,
				insert);
		getCurrentTransform().add(repl);	
	}
	
	public void replace(JCTree tree, String replacement) {
		JavaSourceReplacement repl = new JavaSourceReplacement(tree
				.getStartPosition(), getEndPos(tree), replacement);
		getCurrentTransform().add(repl);
	}
	
	public Class<?>
	getAnnotationClassValue(Element e, Annotation a, String classAttribute) {
	e.getAnnotationMirrors();
	
	// find TypeMirror for Annotation
	for (AnnotationMirror tm : e.getAnnotationMirrors()) {
//		System.out.printf("tm: %s, %s, %s\n", 
//				tm.getAnnotationType().toString(),
//				a.annotationType().toString(),
//				
//				);

		if (tm.getAnnotationType().toString().equals(a.annotationType().getName())) {
			// find class attribute
			
			for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : 
				this.processingEnv.getElementUtils().getElementValuesWithDefaults(tm).entrySet()
				) {
				System.out.printf("%s?=%s %s\n", classAttribute, 
						entry.getKey().getSimpleName(), entry.getValue());
				if (entry.getKey().getSimpleName().toString().equals(classAttribute)) {
					System.out.println("helo: " + entry.getValue().getValue());
					return getClass(entry.getValue().getValue().toString());
				}
			}
			throw new RuntimeException("bug2. use correct annotation attributes");
		}
	}
	throw new RuntimeException("bug. use correct annotation attributes");
	//return this.processor.getClass(name);
}

}