package annotations.stage1.process;

import java.util.NoSuchElementException;

public class FilePosMapper {

	private final int[] mapFromByteToStringPos;
	private final int[] mapFromStringToBytePos;
	public static int INVALID_POS = -1;

	public FilePosMapper(byte[] data, String sdata) {
		
		this.mapFromStringToBytePos = new int[sdata.length() + 1];
		this.mapFromByteToStringPos = new int[data.length + 1];

		int oldbpos = 0;

		for (int spos = 1; spos <= sdata.length(); spos++) {
			// add current char length to bpos
			int newbpos = oldbpos + sdata.substring(spos - 1, spos).getBytes().length;

			// mark positions that don't map to start of character as invalid
			for (int bpos = oldbpos + 1; bpos < newbpos; bpos++) {
				this.mapFromByteToStringPos[newbpos] = INVALID_POS;
			}

			this.mapFromStringToBytePos[spos] = newbpos;
			this.mapFromByteToStringPos[newbpos] = spos;
			oldbpos = newbpos;
		}
	}

	public int mapFromByteToStringPos(int i) {
		if (this.mapFromByteToStringPos[i] == INVALID_POS) {
			throw new NoSuchElementException(
					"byte pos " + i + " doesn't map to start of a character");
		}
		return this.mapFromByteToStringPos[i];
	}

	public int mapFromStringToBytePos(int i) {
		return this.mapFromStringToBytePos[i];
	}
}
