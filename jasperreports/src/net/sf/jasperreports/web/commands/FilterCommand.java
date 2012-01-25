package net.sf.jasperreports.web.commands;

import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.web.actions.FilterData;

public class FilterCommand implements Command {
	
	private FilterData filterData;
	private ReportContext reportContext;
	private DatasetFilter oldFilter;
	
	public FilterCommand(ReportContext reportContext, FilterData filterData) {
		this.reportContext = reportContext;
		this.filterData = filterData;
	}

	public void execute() {
		// keep old filter
		String currentTableFiltersParam = filterData.getFilterDatasetName() + HeaderToolbarElement.FILTER_FIELDS_PARAM_SUFFIX;
		oldFilter = (DatasetFilter) reportContext.getParameterValue(currentTableFiltersParam);
		
		// apply new filter data
		setReportContextParameters(reportContext, filterData);
	}
	
	public void undo() {
		reportContext.setParameterValue(filterData.getFilterDatasetName() + HeaderToolbarElement.FILTER_FIELDS_PARAM_SUFFIX, oldFilter);
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_FIELD, null);
	}

	public void redo() {
		setReportContextParameters(reportContext, filterData);
	}
	
	private void setReportContextParameters(ReportContext reportContext, FilterData filterData) {
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, filterData.getFilterDatasetName());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_FIELD, filterData.getFieldName());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_VALUE_START, filterData.getFieldValueStart());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_VALUE_END, filterData.getFieldValueEnd());
		
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_TYPE, filterData.getFilterType());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_TYPE_OPERATOR, filterData.getFilterTypeOperator());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_PATTERN, filterData.getFilterPattern());
		
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_REMOVE_FILTER, null); // FIXMEJIVE this is for clear, to prevent it
	}

}
