package net.sf.jasperreports.web.commands;

import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.web.actions.ClearFilterData;

public class ClearFilterCommand implements Command {
	
	private ClearFilterData clearFilterData;
	private ReportContext reportContext;
	private DatasetFilter oldFilter;
	
	public ClearFilterCommand(ReportContext reportContext, ClearFilterData clearFilterData) {
		this.reportContext = reportContext;
		this.clearFilterData = clearFilterData;
	}

	public void execute() {
		// keep old filter
		String currentTableFiltersParam = clearFilterData.getFilterDatasetName() + HeaderToolbarElement.FILTER_FIELDS_PARAM_SUFFIX;
		oldFilter = (DatasetFilter) reportContext.getParameterValue(currentTableFiltersParam);
		
		// apply new filter data
		setReportContextParameters(reportContext, clearFilterData);
	}
	
	public void undo() {
		reportContext.setParameterValue(clearFilterData.getFilterDatasetName() + HeaderToolbarElement.FILTER_FIELDS_PARAM_SUFFIX, oldFilter);
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_REMOVE_FILTER, null);
	}

	public void redo() {
		setReportContextParameters(reportContext, clearFilterData);
	}
	
	private void setReportContextParameters(ReportContext reportContext, ClearFilterData clearFilterData) {
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, clearFilterData.getFilterDatasetName());
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_REMOVE_FILTER, clearFilterData.getFieldName());
		
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_FILTER_FIELD, null); // FIXMEJIVE this is for filter, to prevent it
	}

}
