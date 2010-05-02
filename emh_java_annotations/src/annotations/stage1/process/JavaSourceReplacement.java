package annotations.stage1.process;

import java.io.IOException;
import java.io.Writer;

public class JavaSourceReplacement implements Comparable<JavaSourceReplacement> {
	public long startpos;
	public long endpos;
	private long insertionOrder = 0;
	public String replacement;

	public JavaSourceReplacement(
			long startpos, 
			long endpos,
			String replacement) {
		this.startpos = startpos;
		this.endpos = endpos;
		this.replacement = replacement;
	}

	@Override
	public int compareTo(JavaSourceReplacement o) {
		int ret = new Long(this.startpos).compareTo(o.startpos);
		if (ret == 0) {
			ret = new Long(this.getInsertionOrder()).compareTo(o.getInsertionOrder());
		}
		return ret;
	}

	public void setInsertionOrder(long insertionOrder) {
		this.insertionOrder = insertionOrder;
	}

	public long getInsertionOrder() {
		return insertionOrder;
	}
	
	public void apply(Writer out) throws IOException {
		out.append(replacement);
	}

	@Override
	public String toString() {
		return "JavaSourceReplacement [endpos=" + endpos + ", insertionOrder="
				+ insertionOrder + ", replacement=" + replacement
				+ ", startpos=" + startpos + "]";
	}
	
	
	
}
