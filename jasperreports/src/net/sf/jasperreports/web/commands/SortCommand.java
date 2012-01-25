package net.sf.jasperreports.web.commands;

import java.util.List;

import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.web.actions.SortData;

public class SortCommand implements Command {
	
	private SortData sortData;
	private ReportContext reportContext;
	private List<JRSortField> oldSortFields;
	
	public SortCommand(ReportContext reportContext, SortData sortData) {
		this.reportContext = reportContext;
		this.sortData = sortData;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		// keep old sortfields
		String currentTableSortFieldsParam = sortData.getSortDatasetName() + HeaderToolbarElement.SORT_FIELDS_PARAM_SUFFIX;
		oldSortFields = (List<JRSortField>) reportContext.getParameterValue(currentTableSortFieldsParam);
		
		// apply new sortData
		String newReportActionData = sortData.getSortColumnName() + ":" + sortData.getSortColumnType() + ":" + sortData.getSortOrder();
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_SORT_DATA, newReportActionData);
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, sortData.getSortDatasetName());
		
	}
	
	public void undo() {
		reportContext.setParameterValue(sortData.getSortDatasetName() + HeaderToolbarElement.SORT_FIELDS_PARAM_SUFFIX, oldSortFields);
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_SORT_DATA, null);
	}

	public void redo() {
		String newReportActionData = sortData.getSortColumnName() + ":" + sortData.getSortColumnType() + ":" + sortData.getSortOrder();
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_SORT_DATA, newReportActionData);
		reportContext.setParameterValue(HeaderToolbarElement.REQUEST_PARAMETER_DATASET_RUN, sortData.getSortDatasetName());
	}

}
