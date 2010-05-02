package annotations.stage1.defs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import staticproxy.util.proxy.AspectSubclassGenerator;
import annotations.stage1.process.BindTransformer;
import annotations.stage1.transformers.AspectSubclassTransformer;

@Target(ElementType.TYPE)
@BindTransformer(AspectSubclassTransformer.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectSubclass {
	public Class<?> value();
	public Class<?> generator() default AspectSubclassGenerator.Factory.class;
}
