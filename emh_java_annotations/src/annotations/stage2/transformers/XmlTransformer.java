package annotations.stage2.transformers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import com.generationjava.io.xml.SimpleXmlWriter;
import com.generationjava.io.xml.XmlWriter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.Visitor;

import exceptions.FatalHandler;

import staticproxy.util.proxy.MethodInspector;
import annotations.stage1.defs.AspectSubclass;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceReplacement;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;

//class XmlSerializer {
//	
//	PrintWriter out;
//	int curIndent = 0;
//	
//	public XmlSerializer(Writer out) {
//		this.out = new PrintWriter(out);
//	}
//	
//	public XmlSerializer(PrintWriter out) {
//		this.out = out;
//	}
//	
//	public void indent() {
//		for (int i = 0; i < curIndent; i++) {
//			out.print("  ");
//		}
//	}
//	
//	public void newLine() {
//		out.println();
//	}
//	
//	public void indentEnter() {
//		curIndent++;
//		newLine();
//		indent();
//	}
//	
//	public void indentSame() {
//		newLine();
//		indent();
//	}
//	
//	public void indentExit() {
//		curIndent--;
//		newLine();
//		indent();
//	}
//	
//	public void startElement(String name, Map<String,Object> attributes) {
//		out.printf("<%s", name);
//		for (Map.Entry<String, Object> attr : attributes.entrySet()) {
//			out.printf("%s=\"%s\"", attr.getKey(), attr.getValue());
//		}
//		out.printf(">");
//		indentEnter();
//	}
//	
//	public void endElement(String name) {
//		indentExit();
//		out.printf("<%s>", name);
//	}
//	
//}

class Bogus extends Visitor {
	
}

@AspectSubclass(Visitor.class)
public class XmlTransformer 
	extends XmlTransformerGen implements MethodInspector {
	
	BWAnnotationProcessor processor;
	//Writer outstream;
	SimpleXmlWriter outstream;
	
	public XmlTransformer(
			Writer outstream,
			BWAnnotationProcessor processor) {
		//return create().init(processor, a, e);
		this.processor = processor;
		this.outstream = new SimpleXmlWriter(outstream);
	}
	
	public void startElement(int pos, String element) {
		
	}
	
	public void endElement(int pos, String element) {
		
	}

	public void after(String methodName, Object... args) {
		if (methodName.startsWith("visit")) {
			JCTree tree = (JCTree) args[0];
			int startpos = (int) this.processor.getStartPos(tree);
			int endpos = (int) this.processor.getEndPos(tree);
			if (startpos != -1 && endpos != -1) {
				endElement((int) this.processor.getEndPos(tree),
						methodName.replaceFirst("visit", ""));
			} else if (endpos != -1) {
				endElement((int) this.processor.getEndPos(tree),
						methodName.replaceFirst("visit", "END"));
			}
		}
	}
	
	@Override
	public void visitImport(JCImport tree) {
		System.out.println("visitImport");
		super.visitImport(tree);
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
				startElement((int) this.processor.getStartPos(tree),
						methodName.replaceFirst("visit", "START"));
			} else {
				//System.out.printf("before(%s): %s\n", methodName, tree);
			}
		}
	}

	public void finish() throws IOException {
		this.outstream.close();
//		try {
//			xs = new XmlSerializer(jsrc.openWriter());
//		} catch (IOException e1) {
//			FatalHandler.handle(e1);
//		}
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
