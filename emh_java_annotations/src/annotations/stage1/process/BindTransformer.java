package annotations.stage1.process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BindTransformer {
	Class<? extends Transformer> value();
}
