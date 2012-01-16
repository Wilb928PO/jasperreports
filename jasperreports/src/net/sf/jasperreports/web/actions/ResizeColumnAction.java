package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.web.commands.CommandStack;
import net.sf.jasperreports.web.commands.ResizeColumnCommand;

public class ResizeColumnAction extends AbstractAction {

	private ResizeColumnData resizeColumnData;
	
	public ResizeColumnAction(){
	}
	
	public void setResizeColumnData(ResizeColumnData resizeColumnData) {
		this.resizeColumnData = resizeColumnData;
	}

	public ResizeColumnData getResizeColumnData() {
		return resizeColumnData;
	}

	public String getName() {
		return "resize_column_action";
	}

	public void performAction() {
		if (resizeColumnData != null) {
			// obtain command stack
			CommandStack commandStack = getCommandStack();
			
			// execute command
			commandStack.execute(new ResizeColumnCommand(getJasperDesign(), resizeColumnData));
		}
	}

}
