package annotations.stage1.transformers;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.lang.model.element.Element;

import annotations.stage1.defs.TypeDef;
import annotations.stage1.defs.TypeDefs;
import annotations.stage1.process.BWAnnotationProcessor;
import annotations.stage1.process.JavaSourceTransformation;
import annotations.stage1.process.Transformer;

import com.sun.tools.javac.tree.JCTree.JCIdent;

public class TypedefsTransformer extends Transformer {

	protected HashMap<String, String> typedefs = null;

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
	
	@Override
	public void visitIdent(JCIdent tree) {
		String name = tree.getName().toString();
		System.out.printf("name=%s, start-end=%d-%d, src=%s\n", name,
				this.processor.getStartPos(tree), this.processor.getEndPos(tree), "");
		if (this.typedefs.containsKey(name)) {
			this.processor.replace(tree, this.typedefs.get(name));
		}
		super.visitIdent(tree);
	}

	/*
	@Override
	public void visitVarDef(JCVariableDecl tree) {
		String name = tree.getName().toString();
		System.out.printf("tree=%s, name=%s\n", tree.toString(), tree
				.getName());
		// TODO: get source code and do a text substitution
		if (this.typedefs.containsKey(name)) {
			String replacement = this.typedefs.get(name);
			replacement = tree.toString().replace(name, replacement);
			this.processor.replace(tree, replacement);
		}
		super.visitVarDef(tree);
	}
	 */

}
