package image.palette;

public abstract class Section {

	protected int[] min;
	protected int[] max;
	protected float minAltitude;
	protected float maxAltitude;

	boolean red;
	boolean green;
	boolean blue;

	public Section(int[] min, int[] max, float minAltitude, float maxAltitude,
			boolean red, boolean gree, boolean blue) {
		this.min = min;
		this.max = max;
		this.minAltitude = minAltitude;
		this.maxAltitude = maxAltitude;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * Run interpretation on the given rgb to find its altitude
	 * 
	 * @param rgb
	 * @return
	 */
	public abstract float process(int[] rgb);

	/**
	 * 
	 * @param rgb
	 * @return
	 */
	public boolean inSection(int[] rgb) {
		boolean inSection = false;
		if ((min[0] <= rgb[0] && rgb[0] >= max[0])
				&& (min[1] <= rgb[1] && rgb[1] >= max[1])
				&& (min[2] <= rgb[2] && rgb[2] >= max[2])) {
			inSection = true;
		}
		return inSection;
	}
}
