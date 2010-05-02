package annotations.stage1.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.tools.JavaFileObject;

import com.sun.tools.javac.tree.JCTree;

/**
 * Represent a Java source file and queued transformations on it.
 * 
 * Note: Assumes platform default CharSet for source files.
 * 
 * @author emh
 * 
 */
public class JavaSourceTransformation {

	// public static String ENCODING = "UTF-8";

	public static enum OutputType {
		SOURCECODE,
		XMLSOURCECODE
	}
	
	private final JavaFileObject sourcefile;
	protected byte[] bytes;
	private String fileContentsStr;
	private FilePosMapper filePosMapper;
	private long insertionOrder = 0;
	private OutputType outputType;

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	TreeSet<JavaSourceReplacement> replacements = new TreeSet<JavaSourceReplacement>();
	private String packageName;

	public JavaSourceTransformation(
			String packageName,
			JavaFileObject f) throws IOException {
		this.sourcefile = f;
		this.setPackageName(packageName);
		this.bytes = getBytesFromFile(new File(sourcefile.toString()));
		this.fileContentsStr = new String(this.bytes);
		this.filePosMapper = new FilePosMapper(this.bytes, this.fileContentsStr);
		//this.outputType = ot;
	}
	
	public int mapFromByteToStringPos(int beginIndex) {
		return filePosMapper.mapFromByteToStringPos(beginIndex);
	}
	
	public int mapFromStringToBytePos(int beginIndex) {
		return filePosMapper.mapFromStringToBytePos(beginIndex);
	}
	
	public String substring(int beginIndex, int endIndex) {
		//System.out.printf("b:%d-%d\n", beginIndex, endIndex);
		beginIndex = mapFromByteToStringPos(beginIndex);
		endIndex = mapFromByteToStringPos(endIndex);
		//System.out.printf("a:%d-%d\n", beginIndex, endIndex);
		return fileContentsStr.substring(beginIndex, endIndex);
	}
	
	public byte[] getBytesFromFile() throws IOException {
		return this.bytes;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];

		int offset = 0;
		int bytesRead = 0;
		while (offset < bytes.length
				&& (bytesRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += bytesRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Incomplete file read" + file.getName());
		}
		is.close();
		return bytes;
	}

	public static String getStringFromFile(File file) throws IOException {
		return new String(getBytesFromFile(file));
	}

	public void add(JavaSourceReplacement repl) {
		// TODO: check for overlaps and throw exception
		repl.setInsertionOrder(this.insertionOrder++);
		this.replacements.add(repl);
	}

	public JavaFileObject getFile() {
		return this.sourcefile;
	}

	public String getTransformed() throws IOException {
		byte[] in = this.bytes; //getBytesFromFile(this.sourcefile);

		// build new string: original source with replacements applied
		StringWriter sb = new StringWriter(in.length * 2);
		int laststop = 0;
		
		System.out.println(this.replacements);
		
		for (JavaSourceReplacement repl : this.replacements) {
			if (laststop > repl.startpos 
					|| repl.startpos > in.length) {
				throw new IllegalArgumentException(
						String.format(
						"illegal replacement: %s\n", repl)
						);
			}
			byte[] chunk = Arrays
					.copyOfRange(in, laststop, (int) repl.startpos);
			sb.append(new String(chunk));
			repl.apply(sb);
			laststop = (int) repl.endpos;
		}
		byte[] chunk = Arrays.copyOfRange(in, laststop, in.length);
		sb.append(new String(chunk));

		return sb.toString();
	}

	public void addReplaceIn(BWAnnotationProcessor processor,
			JCTree tree, // TODO: this class shouldn't know about compiler stuff
			String target,
			String replacement) {
		
		// TODO: use String pos everywhere by default
		int startpos = (int) processor.getStartPos(tree);
		int endpos  = (int) processor.getEndPos(tree);
		
//		System.out.printf("1: %d - %d\n", startpos, endpos);
		startpos = mapFromByteToStringPos(startpos);
		endpos = mapFromByteToStringPos(endpos);
//		System.out.printf("2: %d - %d\n", startpos, endpos);
		
		String original = substring(startpos, endpos);
//		System.out.printf("substring: %s, %s -> %s\n", original, 
//				target, replacement);
		String parts[] = original.split(Pattern.quote(target), 2);
		if (parts.length < 2) {
			throw new RuntimeException("no match");
		}
		startpos += parts[0].length();
		endpos = startpos + target.length();
//		System.out.printf("3: %d - %d\n", startpos, endpos);
		
		startpos = mapFromStringToBytePos(startpos);
		endpos = mapFromStringToBytePos(endpos);
		
		add(new JavaSourceReplacement(
						startpos, 
						endpos, 
						replacement)
		);
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}
}
