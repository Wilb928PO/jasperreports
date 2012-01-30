package net.sf.jasperreports.components.headertoolbar.actions;

import java.util.List;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.web.commands.Command;
import net.sf.jasperreports.web.commands.CommandStack;

public class ResizeColumnCommand implements Command 
{
	
	private StandardTable table;
	private ResizeColumnData resizeColumnData;
	private CommandStack individualResizeCommandStack;
	
	private StandardColumn modColumn;
	private StandardColumn neighbouringColumn;
	private int oldModColumnWidth = -1;
	private int oldNeighbouringColumnWidth = -1;
	
	private static final String DIRECTION_LEFT = "left";
	private static final String DIRECTION_RIGHT = "right";
	
	
	
	public ResizeColumnCommand(StandardTable table, ResizeColumnData resizeColumnData) 
	{
		this.table = table;
		this.resizeColumnData = resizeColumnData;
		this.individualResizeCommandStack = new CommandStack();
	}

	public void execute() 
	{
		List<BaseColumn> tableColumns = TableUtil.getAllColumns(table);
		
		int modIndex = resizeColumnData.getColumnIndex();
		
		modColumn = (StandardColumn) tableColumns.get(modIndex);
		neighbouringColumn = null;
		oldModColumnWidth = modColumn.getWidth();
		
		if(DIRECTION_LEFT.equals(resizeColumnData.getDirection())) {
			if (modIndex > 0) {
				neighbouringColumn = (StandardColumn) tableColumns.get(modIndex - 1);
			}
			
		} else if (DIRECTION_RIGHT.equals(resizeColumnData.getDirection())) {
			if (modIndex < tableColumns.size() - 1) {
				neighbouringColumn = (StandardColumn) tableColumns.get(modIndex + 1);
			}
		}
		
		int deltaWidth = resizeColumnData.getWidth() - modColumn.getWidth();

		modColumn.setWidth(resizeColumnData.getWidth());
		resizeColumn(modColumn, deltaWidth);
		
		if (neighbouringColumn != null) {
			oldNeighbouringColumnWidth = neighbouringColumn.getWidth();
			neighbouringColumn.setWidth(neighbouringColumn.getWidth() - deltaWidth);
			resizeColumn(neighbouringColumn, -deltaWidth);
		}
	}
	
	private void resizeColumn(StandardColumn column, int amount) {
		resizeCellChildren(column.getTableHeader(), amount);
		resizeCellChildren(column.getColumnHeader(), amount);
		resizeCellChildren(column.getDetailCell(), amount);
		resizeCellChildren(column.getColumnFooter(), amount);
		resizeCellChildren(column.getTableFooter(), amount);
	}
	
	private void resizeCellChildren(Cell cell, int amount) {
		if (cell != null) {
			for (JRChild child: cell.getChildren()) {
				if (child instanceof JRBaseElement) {
					JRBaseElement be = (JRBaseElement) child;
					individualResizeCommandStack.execute(new ResizeElementCommand(be, be.getWidth() + amount));
				}
			}
		}
	}

	public void undo() 
	{
		modColumn.setWidth(oldModColumnWidth);
		if (neighbouringColumn != null) {
			neighbouringColumn.setWidth(oldNeighbouringColumnWidth);
		}
		individualResizeCommandStack.undoAll();
	}

	public void redo() 
	{
		int deltaWidth = resizeColumnData.getWidth() - modColumn.getWidth();
		modColumn.setWidth(resizeColumnData.getWidth());
		if (neighbouringColumn != null) {
			neighbouringColumn.setWidth(neighbouringColumn.getWidth() - deltaWidth);
		}
		individualResizeCommandStack.redoAll();
	}

}
