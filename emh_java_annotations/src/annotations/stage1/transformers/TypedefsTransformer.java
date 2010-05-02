package annotations.stage1.transformers;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.lang.model.element.Element;

import annotations.stage1.defs.TypeDef;
import annotations.stage1.defs.TypeDefs;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class TypedefsTransformer extends Transformer {

	protected HashMap<String, String> typedefs = null;
	boolean claimed = false;

	@Override
	public TypedefsTransformer init(
			JavaSourceTransformation transform,
			BWAnnotationProcessor processor, 
			Annotation a, 
			Element e) {
		super.init(transform, processor, a, e);
		this.typedefs = new HashMap<String, String>();
		TypeDefs atypedefs = (TypeDefs) a;
		for (TypeDef typedef : atypedefs.value()) {
			this.typedefs.put(typedef.from(), typedef.to());
		}
		return this;
	}
	
	public boolean doReplace(JCTree tree, String name) {
		//String name = tree.getName().toString();
		System.out.printf("name=%s, start-end=%d-%d, src=%s\n", name,
				this.processor.getStartPos(tree), this.processor.getEndPos(tree), "");
		if (this.typedefs.containsKey(name)) {
			System.out.println("match");
			String replacement = this.typedefs.get(name);
			this.processor.replace(tree, replacement);
			return true;
		}
		return false;
	}
	
	@Override
	public void visitIdent(JCIdent tree) {
		//if (tree.type != null) {
		//System.out.printf("%s/%s/%s\n", tree.name, tree.sym, tree.type);
		//}
		if (!claimed && tree.sym == null && tree.type == null) {
			doReplace(tree, tree.getName().toString());
		}
		super.visitIdent(tree);
	}

	@Override
	public void visitTypeIdent(JCPrimitiveTypeTree tree) {
		// TODO Auto-generated method stub
		System.out.printf("%s/%s\n", tree.type, tree.typetag);
		if (!claimed && tree.type != null) {
			doReplace(tree, tree.type.toString());
		}
		super.visitTypeIdent(tree);
	}

	@Override
	public void visitTypeApply(JCTypeApply tree) {
		claimed = doReplace(tree, tree.toString());
		super.visitTypeApply(tree);
		claimed = false;
	}
	
	
	// ident covers it
//	@Override
//	public void visitVarDef(JCVariableDecl tree) {
//		String name = tree.vartype.toString(); //tree.getName().toString();
//		System.out.printf("tree=%s, name=%s\n", tree.toString(), name);
//		// TODO: get source code and do a text substitution
//		if (this.typedefs.containsKey(name)) {
//			System.out.println("match2");
//			String replacement = this.typedefs.get(name);
//			replacement = tree.toString().replace(name, replacement);
//			this.processor.getCurrentTransform().addReplaceIn(
//					this.processor, tree, 
//					name, this.typedefs.get(name));
//		}
//		super.visitVarDef(tree);
//	}

}
