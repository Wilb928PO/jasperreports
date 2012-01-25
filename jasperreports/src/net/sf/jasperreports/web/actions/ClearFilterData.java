package net.sf.jasperreports.web.actions;

public class ClearFilterData {
	
	public static final String FIELD_NAME = "fieldName";
	
	private String filterDatasetName;
	private String fieldName;
	
	public ClearFilterData() {
	}
	
	public String getFilterDatasetName() {
		return filterDatasetName;
	}

	public void setFilterDatasetName(String filterDatasetName) {
		this.filterDatasetName = filterDatasetName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
