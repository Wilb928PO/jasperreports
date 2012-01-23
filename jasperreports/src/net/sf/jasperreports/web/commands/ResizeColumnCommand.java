package net.sf.jasperreports.web.commands;

import java.util.List;
import java.util.UUID;

import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRIdentifiable;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.web.actions.ResizeColumnData;

public class ResizeColumnCommand implements Command {
	
	private JasperDesignCache cache;
	private CommandTarget target;
	private ResizeColumnData resizeColumnData;
	private CommandStack individualResizeCommandStack;
	
	private StandardColumn modColumn;
	private StandardColumn neighbouringColumn;
	private int oldModColumnWidth = -1;
	private int oldNeighbouringColumnWidth = -1;
	
	private static final String DIRECTION_LEFT = "left";
	private static final String DIRECTION_RIGHT = "right";
	
	
	
	public ResizeColumnCommand(JasperDesignCache cache, ResizeColumnData resizeColumnData) {
		this.cache = cache;
		UUID uuid = null;//FIXMEJIVETABLEUUID UUID.fromString(resizeColumnData.getUuid());
		this.target = cache.getCommandTarget(uuid);
		this.resizeColumnData = resizeColumnData;
		this.individualResizeCommandStack = new CommandStack();
	}

	public void execute() 
	{
		//StandardTable table = (StandardTable) ((JRDesignComponentElement) receiver.getSummary().getElementByKey("table_component")).getComponent();
//		UUID uuid = null;//FIXMEJIVETABLEUUID UUID.fromString(resizeColumnData.getUuid());
//		CommandTarget target = cache.getCommandTarget(uuid);
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
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
			
			cache.resetJasperReport(target.getUri());
		}
	}
	
	private void resizeColumn(StandardColumn column, int amount) {
		resizeCellChildren(column.getColumnHeader(), amount);
		resizeCellChildren(column.getDetailCell(), amount);
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

	public void undo() {
		modColumn.setWidth(oldModColumnWidth);
		if (neighbouringColumn != null) {
			neighbouringColumn.setWidth(oldNeighbouringColumnWidth);
		}
		individualResizeCommandStack.undoAll();
		if (target != null)
		{
			cache.resetJasperReport(target.getUri());
		}
	}

	public void redo() {
		int deltaWidth = resizeColumnData.getWidth() - modColumn.getWidth();
		modColumn.setWidth(resizeColumnData.getWidth());
		if (neighbouringColumn != null) {
			neighbouringColumn.setWidth(neighbouringColumn.getWidth() - deltaWidth);
		}
		individualResizeCommandStack.redoAll();
		if (target != null)
		{
			cache.resetJasperReport(target.getUri());
		}
	}

}
