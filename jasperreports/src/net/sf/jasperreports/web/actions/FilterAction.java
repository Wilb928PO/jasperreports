package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.web.commands.CommandStack;
import net.sf.jasperreports.web.commands.FilterCommand;

public class FilterAction extends AbstractAction {
	
	private FilterData filterData;

	public FilterAction() {
	}

	public FilterData getFilterData() {
		return filterData;
	}

	public void setFilterData(FilterData filterData) {
		this.filterData = filterData;
	}

	public void performAction() {
		if (filterData != null) {
			// obtain command stack
			CommandStack commandStack = getCommandStack();
	
			// execute command
			commandStack.execute(new FilterCommand(getReportContext(), filterData));
		}
	}

}
