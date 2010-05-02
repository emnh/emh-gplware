package annotations.stage1.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

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
	
	private final File sourcefile;
	private byte[] bytes;
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

	public JavaSourceTransformation(File f) throws IOException {
		this.sourcefile = f;
		this.bytes = getBytesFromFile(sourcefile);
		this.fileContentsStr = new String(this.bytes);
		this.filePosMapper = new FilePosMapper(this.bytes, this.fileContentsStr);
		//this.outputType = ot;
	}
	
	public String substring(int beginIndex, int endIndex) {
		beginIndex = filePosMapper.mapFromByteToStringPos(beginIndex);
		endIndex = filePosMapper.mapFromByteToStringPos(endIndex);
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

	public File getFile() {
		return this.sourcefile;
	}

	public String getTransformed() throws IOException {
		byte[] in = this.bytes; //getBytesFromFile(this.sourcefile);

		// build new string: original source with replacements applied
		StringBuilder sb = new StringBuilder(in.length);
		int laststop = 0;
		for (JavaSourceReplacement repl : this.replacements) {
			byte[] chunk = Arrays
					.copyOfRange(in, laststop, (int) repl.startpos);
			sb.append(new String(chunk));
			sb.append(repl.replacement);
			laststop = (int) repl.endpos;
		}
		byte[] chunk = Arrays.copyOfRange(in, laststop, in.length);
		sb.append(new String(chunk));

		return sb.toString();
	}
}
