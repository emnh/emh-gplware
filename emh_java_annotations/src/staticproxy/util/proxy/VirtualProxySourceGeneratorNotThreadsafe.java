package staticproxy.util.proxy;

import java.io.*;

public class VirtualProxySourceGeneratorNotThreadsafe
    extends VirtualProxySourceGenerator {

  public VirtualProxySourceGeneratorNotThreadsafe(
      Class<?> subject, Class<?> realSubject) {
    super(subject, realSubject, Concurrency.NONE);
  }

  protected void addRealSubjectCreation(PrintWriter out,
                                        String name,
                                        String realName) {
    indentSame(out); out.printf("private %s realSubject;", name);
    newLine(out);
    indentSame(out); out.printf("private %s realSubject() {", name);
    indentEnter(out); out.printf("if (realSubject == null) {");
    indentEnter(out); out.printf("realSubject = new %s();", realName);
    indentExit(out); out.println("}");
    indentSame(out); out.println("return realSubject;");
    indentExit(out); out.println("}");
  }
}