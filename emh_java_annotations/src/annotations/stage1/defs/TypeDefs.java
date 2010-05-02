package annotations.stage1.defs;

import annotations.stage1.process.BindTransformer;
import annotations.stage1.transformers.TypedefsTransformer;

@BindTransformer(TypedefsTransformer.class)
public @interface TypeDefs {
	TypeDef[] value();
}