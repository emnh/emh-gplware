package annotations.stage1.defs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import staticproxy.util.proxy.ProxySourceGeneratorFactory;
import staticproxy.util.proxy.VirtualAspectProxySourceGenerator;
import annotations.stage1.process.BindTransformer;
import annotations.stage1.transformers.StaticProxyTransformer;

@Target(ElementType.TYPE)
@BindTransformer(StaticProxyTransformer.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticProxy {
	/**
	 * Delegation source code generator
	 */
	public Class<?> generator() 
		default VirtualAspectProxySourceGenerator.Factory.class;

	/**
	 * Interface or abstract class to delegate to
	 */
	public Class<?> value();
}