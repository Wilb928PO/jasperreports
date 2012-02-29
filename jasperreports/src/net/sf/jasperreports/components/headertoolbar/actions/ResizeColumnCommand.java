package net.sf.jasperreports.components.headertoolbar.actions;

import java.util.List;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.components.table.StandardBaseColumn;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.web.commands.Command;
import net.sf.jasperreports.web.commands.CommandStack;

public class ResizeColumnCommand implements Command 
{
	
	private StandardTable table;
	private JRDesignComponentElement componentElement;
	private ResizeColumnData resizeColumnData;
	private CommandStack individualResizeCommandStack;
	
	private StandardColumn modColumn;
	private int oldModColumnWidth;

	private StandardColumnGroup modColumnGroup;
	private int oldModColumnGroupWidth;
	private int newModColumnGroupWidth;
	

	public ResizeColumnCommand(JRDesignComponentElement componentElement, ResizeColumnData resizeColumnData) 
	{
		this.componentElement = componentElement;
		this.table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
		this.resizeColumnData = resizeColumnData;
		this.individualResizeCommandStack = new CommandStack();
	}

	public void execute() 
	{
		
		List<BaseColumn> tableColumns = TableUtil.getAllColumns(table);
		
		int modIndex = resizeColumnData.getColumnIndex();
		
		modColumn = (StandardColumn) tableColumns.get(modIndex);
		oldModColumnWidth = modColumn.getWidth();
		
		
		int deltaWidth = resizeColumnData.getWidth() - modColumn.getWidth();
		
		// resize the component that contains the table
		individualResizeCommandStack.execute(new ResizeElementCommand(componentElement, componentElement.getWidth() + deltaWidth));
		
		// resize the column group that contains modColumn
		modColumnGroup = (StandardColumnGroup) getColumnGroupForColumn(modColumn, table.getColumns());
		if (modColumnGroup != null) {
			oldModColumnGroupWidth = modColumnGroup.getWidth();
			newModColumnGroupWidth = oldModColumnGroupWidth + deltaWidth;
			
			modColumnGroup.setWidth(newModColumnGroupWidth);
			resizeColumnGroupHF(modColumnGroup, deltaWidth);
		}

		// resize the column
		modColumn.setWidth(resizeColumnData.getWidth());
		resizeColumn(modColumn, deltaWidth);
		
	}
	
	private void resizeColumn(StandardColumn column, int amount) {
		resizeChildren(column.getTableHeader(), amount);
		resizeChildren(column.getColumnHeader(), amount);
		resizeChildren(column.getDetailCell(), amount);
		resizeChildren(column.getColumnFooter(), amount);
		resizeChildren(column.getTableFooter(), amount);

		resizeColumnGroupHF(column, amount);
	}

	private void resizeColumnGroupHF(StandardBaseColumn column, int amount) {
		for (GroupCell header: column.getGroupHeaders()) {
			resizeChildren(header.getCell(), amount);
		}

		for (GroupCell footer: column.getGroupFooters()) {
			resizeChildren(footer.getCell(), amount);
		}
	}
	
	private void resizeChildren(JRElementGroup elementGroup, int amount) {
		if (elementGroup != null) {
			for (JRChild child: elementGroup.getChildren()) {
				if (child instanceof JRBaseElement) {
					JRBaseElement be = (JRBaseElement) child;
					individualResizeCommandStack.execute(new ResizeElementCommand(be, be.getWidth() + amount));
				}
				if (child instanceof JRElementGroup) {
					JRElementGroup eg = (JRElementGroup) child;
					resizeChildren(eg, amount);
				}
			}
		}
	}
	
	private ColumnGroup getColumnGroupForColumn(BaseColumn column, List<BaseColumn> columns) {
		for (BaseColumn bc: columns) {
			if (bc instanceof ColumnGroup) {
				ColumnGroup cg = (ColumnGroup) bc;
				if (cg.getColumns().contains(column)) {
					return cg;
				} else {
					return getColumnGroupForColumn(column, cg.getColumns());
				}
			}
		}
		return null;
	}

	public void undo() 
	{
		modColumn.setWidth(oldModColumnWidth);
		
		if (modColumnGroup != null) {
			modColumnGroup.setWidth(oldModColumnGroupWidth);
		}
		
		individualResizeCommandStack.undoAll();
	}

	public void redo() 
	{
		modColumn.setWidth(resizeColumnData.getWidth());
		
		if (modColumnGroup != null) {
			modColumnGroup.setWidth(newModColumnGroupWidth);
		}
		
		individualResizeCommandStack.redoAll();
	}

}
