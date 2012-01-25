package net.sf.jasperreports.web.actions;



public class SortData {
	
	private String sortColumnName;
	private String sortColumnType;
	private String sortOrder;
	private String sortDatasetName;
	
	public SortData() {
	}
	
	public SortData(String sortColumnName, String sortColumnType, String sortOrder, String sortDatasetName) {
		this.sortColumnName = sortColumnName;
		this.sortColumnType = sortColumnType;
		this.sortOrder = sortOrder;
		this.sortDatasetName = sortDatasetName;
	}

	public String getSortColumnName() {
		return sortColumnName;
	}

	public void setSortColumnName(String sortColumnName) {
		this.sortColumnName = sortColumnName;
	}

	public String getSortColumnType() {
		return sortColumnType;
	}

	public void setSortColumnType(String sortColumnType) {
		this.sortColumnType = sortColumnType;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortDatasetName() {
		return sortDatasetName;
	}

	public void setSortDatasetName(String sortDatasetName) {
		this.sortDatasetName = sortDatasetName;
	}

	@Override
	public String toString() {
		return "sortColumnName: " + sortColumnName + "; sortColumnType: " + sortColumnType + "; sortOrder: " + sortOrder + "; sortDatasetName: " + sortDatasetName;
	}
}
