package staticproxy.util.gen;

import javax.tools.*;
import java.io.*;
import java.net.*;

class GeneratedJavaSourceFile extends SimpleJavaFileObject {
  private CharSequence javaSource;

  public GeneratedJavaSourceFile(String className,
                                 CharSequence javaSource) {
    super(URI.create(className + ".java"),
        Kind.SOURCE);
    this.javaSource = javaSource;
  }

  public CharSequence getCharContent(boolean ignoreEncodeErrors)
      throws IOException {
    return javaSource;
  }
}