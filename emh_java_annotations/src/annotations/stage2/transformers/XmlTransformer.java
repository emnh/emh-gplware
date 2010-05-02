package annotations.stage2.transformers;

import java.io.IOException;

import staticproxy.util.proxy.MethodInspector;
import annotations.stage1.defs.AspectSubclass;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaXmlSourceReplacement;
import annotations.stage1.process.JavaXmlSourceTransformation;

import com.generationjava.io.xml.XmlWriter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

@AspectSubclass(TreeTranslator.class)
public class XmlTransformer 
	extends XmlTransformerGen implements MethodInspector {
	
	protected BWAnnotationProcessor processor;
	protected JavaXmlSourceTransformation transform;
	
	public JavaXmlSourceTransformation getTransform() {
		return transform;
	}

	public void setTransform(JavaXmlSourceTransformation transform) {
		this.transform = transform;
	}

	public XmlTransformer(
			JavaXmlSourceTransformation transform,
			BWAnnotationProcessor processor) {
		this.processor = processor;
		this.transform = transform;
	}
	
	public void startElement(final int pos, final String name) {
		//System.out.printf("start: %d, %s\n", ctr++, name);
		
		transform.add(new JavaXmlSourceReplacement(pos, pos) {
			
			@Override
			public void apply(XmlWriter out) throws IOException {
				out.writeEntity(name);
			}
			
		});
		
	}
	
	public void endElement(int pos, String name) {
		//System.out.printf("end: %d, %s\n", ctr--, name);
	
		transform.add(new JavaXmlSourceReplacement(pos, pos) {
			
			@Override
			public void apply(XmlWriter out) throws IOException {
				out.endEntity();
			}
			
		});
	}

	public void after(String methodName, Object... args)  {
		if (methodName.startsWith("visit")) {
			JCTree tree = (JCTree) args[0];
			int startpos = (int) this.processor.getStartPos(tree);
			int endpos = (int) this.processor.getEndPos(tree);
			if (startpos != -1 && endpos != -1) {
				endElement((int) this.processor.getEndPos(tree),
						methodName.replaceFirst("visit", ""));
			} else if (endpos != -1) {
//				endElement((int) this.processor.getEndPos(tree),
//						methodName.replaceFirst("visit", "END"));
			}
		}
	}
	
	public void before(String methodName, Object... args) {
		if (methodName.startsWith("visit")) {
			JCTree tree = (JCTree) args[0];
//			assert this.processor.getEndPos(tree) != -1 : 
//				"use javac -Xjcov and check that compilation unit is set";
			int startpos = (int) this.processor.getStartPos(tree);
			int endpos = (int) this.processor.getEndPos(tree);
			if (startpos != -1 && endpos != -1) {
				startElement((int) this.processor.getStartPos(tree),
						methodName.replaceFirst("visit", "")); 
			} else if (startpos != -1) {
//				startElement((int) this.processor.getStartPos(tree),
//						methodName.replaceFirst("visit", "START"));
			} else {
				//System.out.printf("before(%s): %s\n", methodName, tree);
			}
		}
	}

	
	//	@Override
	//	public Object invoke(Object proxy, Method method, Object[] args)
	//	throws Throwable {
	//		System.out.printf("%s %s %s\n", null, method, args);
	//		System.out.println(method.getName());
	//		method.invoke(this, args);
	//		return null;
	//	}
}
