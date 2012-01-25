package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.web.commands.CommandStack;
import net.sf.jasperreports.web.commands.SortCommand;

public class SortAction extends AbstractAction {
	
	private SortData sortData;

	public SortAction() {
	}

	public SortData getSortData() {
		return sortData;
	}

	public void setSortData(SortData sortData) {
		this.sortData = sortData;
	}

	public void performAction() {
		if (sortData != null) {
			// obtain command stack
			CommandStack commandStack = getCommandStack();
	
			// execute command
			commandStack.execute(new SortCommand(getReportContext(), sortData));
		}
	}

}
