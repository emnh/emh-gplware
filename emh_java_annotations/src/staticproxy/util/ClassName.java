package staticproxy.util;

public class ClassName {

	public static String getRelativeName(String qualifiedName) {
		return qualifiedName.replaceFirst(".*\\.", "");
	}
	
	public static String getPackageName(String qualifiedName) {
		return qualifiedName.replaceFirst("\\.?[^\\.]*$", "");
	}
	
}
