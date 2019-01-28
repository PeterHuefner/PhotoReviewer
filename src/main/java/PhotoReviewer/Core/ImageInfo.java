package PhotoReviewer.Core;

public class ImageInfo {

	public String path;
	public String fileName;
	final public int key;

	public ImageInfo(int key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return fileName;
	}
}
