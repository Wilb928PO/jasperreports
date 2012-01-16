package net.sf.jasperreports.web.actions;


public class ResizeColumnData {
	private int columnIndex;
	private int width;
	private String direction;
	
	public ResizeColumnData() {
	}
	
	public ResizeColumnData(int columnIndex, int width, String direction) {
		this.columnIndex = columnIndex;
		this.width = width;
		this.direction = direction;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return "columnIndex: " + columnIndex + "; width: " + width + "; direction: " + direction;
	}
}
