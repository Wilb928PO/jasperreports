package net.sf.jasperreports.web.commands;

import net.sf.jasperreports.engine.base.JRBaseElement;

public class ResizeElementCommand implements Command {
	
	private int width;
	private int oldWidth;
	private JRBaseElement receiver;
	
	public ResizeElementCommand(JRBaseElement receiver, int width) {
		this.receiver = receiver;
		this.width = width;
		this.oldWidth = receiver.getWidth();
	}

	public void execute() {
		receiver.setWidth(width);
	}		
	
	public void undo() {
		receiver.setWidth(oldWidth);
	}

	public void redo() {
		execute();
	}

}
