package net.sf.jasperreports.web.actions;

public class FilterData {
	
	public static final String FILTER_DATASET_NAME = "filterDatasetName";
	public static final String FIELD_NAME = "fieldName";
	public static final String FIELD_VALUE_START = "fieldValueStart";
	public static final String FIELD_VALUE_END = "fieldValueEnd";
	public static final String FILTER_TYPE = "filterType";
	public static final String FILTER_TYPE_OPERATOR = "filterTypeOperator";
	public static final String FILTER_PATTERN = "filterPattern";
	
	private String filterDatasetName;
	private String fieldName;
	private String fieldValueStart;
	private String fieldValueEnd;
	private String filterType;
	private String filterTypeOperator;
	private String filterPattern;
	
	public FilterData() {
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

	public String getFieldValueStart() {
		return fieldValueStart;
	}

	public void setFieldValueStart(String fieldValueStart) {
		this.fieldValueStart = fieldValueStart;
	}

	public String getFieldValueEnd() {
		return fieldValueEnd;
	}

	public void setFieldValueEnd(String fieldValueEnd) {
		this.fieldValueEnd = fieldValueEnd;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterTypeOperator() {
		return filterTypeOperator;
	}

	public void setFilterTypeOperator(String filterTypeOperator) {
		this.filterTypeOperator = filterTypeOperator;
	}

	public String getFilterPattern() {
		return filterPattern;
	}

	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
	}

}
