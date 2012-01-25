package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.web.commands.CommandStack;
import net.sf.jasperreports.web.commands.ClearFilterCommand;

public class ClearFilterAction extends AbstractAction {
	
	private ClearFilterData clearFilterData;

	public ClearFilterAction() {
	}

	public ClearFilterData getClearFilterData() {
		return clearFilterData;
	}

	public void setClearFilterData(ClearFilterData clearFilterData) {
		this.clearFilterData = clearFilterData;
	}

	public void performAction() {
		if (clearFilterData != null) {
			// obtain command stack
			CommandStack commandStack = getCommandStack();
	
			// execute command
			commandStack.execute(new ClearFilterCommand(getReportContext(), clearFilterData));
		}
	}

}
