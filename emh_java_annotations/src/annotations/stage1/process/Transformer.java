package annotations.stage1.process;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.sun.tools.javac.tree.TreeTranslator;

public abstract class Transformer extends TreeTranslator {

	protected BWAnnotationProcessor processor = null;
	protected JavaSourceTransformation transform = null;


	// usually subclass will override init,
	// do some processing on a and e and save the result instead
	// protected Annotation a;
	// protected Element e;

	/** May choose to return a different object than this **/
	public Transformer init(
			JavaSourceTransformation transform,
			BWAnnotationProcessor processor, 
			Annotation a, Element e) {
		this.transform = transform;
		this.processor = processor;
		return this;
	}
	
	public JavaSourceTransformation getTransform() {
		return this.transform;
	}

	public void finish() {
		
	}
	
}
