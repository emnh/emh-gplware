package staticproxy.util.proxy;

public class Util {
  public static String prettyPrint(Class<?> clazz) {
    return prettyPrint(clazz, "");
  }

  public static String prettyPrint(Class<?> c, String postfix) {
    if (c.isArray()) {
      return prettyPrint(c.getComponentType(), postfix + "[]");
    } else {
      Package pack = c.getPackage();
      if (pack != null && pack.getName().equals("java.lang")) {
        return c.getSimpleName() + postfix;
      }
      return (c.getName() + postfix).replace('$', '.');
    }
  }
}