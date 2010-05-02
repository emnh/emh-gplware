package annotations.stage1.defs;

import annotations.stage1.process.BindTransformer;
import annotations.stage1.transformers.SetTypeParametersTransformer;

@BindTransformer(SetTypeParametersTransformer.class)
public @interface SetTypeParameters {
	Class<?> forClass();
	String typeParameters();
}
