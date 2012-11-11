package org.moss.lunar.types;

/**
 * Stores the RGB data for each pixel including it's position
 * 
 * @author Robin
 * 
 */
public class PixelDto extends RgbDto {

	private Long row;
	private Long column;

	public PixelDto(int red, int green, int blue, long row, long column) {
		super(red, green, blue);
		this.setRow(row);
		this.setColumn(column);
	}

	public PixelDto(int[] rgb, long row, long column) {
		super(rgb);

		this.setRow(row);
		this.setColumn(column);
	}

	public Long getRow() {
		return row;
	}

	public void setRow(Long row) {
		this.row = row;
	}

	public Long getColumn() {
		return column;
	}

	public void setColumn(Long column) {
		this.column = column;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		equals = super.equals(obj);
		if (equals && obj instanceof PixelDto) {
			PixelDto compareTo = (PixelDto) obj;
			equals = (compareTo.getColumn().equals(this.getColumn()) && compareTo
					.getRow().equals(this.getRow()));
		}

		return equals;
	}

}
