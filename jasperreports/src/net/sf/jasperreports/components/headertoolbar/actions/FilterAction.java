package net.sf.jasperreports.components.headertoolbar.actions;

import java.util.UUID;

import net.sf.jasperreports.components.sort.actions.FilterCommand;
import net.sf.jasperreports.components.sort.actions.FilterData;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.JRIdentifiable;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.web.commands.CommandStack;
import net.sf.jasperreports.web.commands.CommandTarget;

public class FilterAction extends AbstractTableAction 
{
	
	private FilterData filterData;

	public FilterAction() {
	}

	public FilterData getFilterData() {
		return filterData;
	}

	public void setFilterData(FilterData filterData) {
		this.filterData = filterData;
	}

	public void performAction() 
	{
		if (filterData != null) 
		{
			CommandTarget target = getCommandTarget(UUID.fromString(filterData.getUuid()));
			if (target != null)
			{
				JRIdentifiable identifiable = target.getIdentifiable();
				JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
				StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
				
				JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
				
				String datasetName = datasetRun.getDatasetName();
				
				JasperDesignCache cache = JasperDesignCache.getInstance(getJasperReportsContext(), getReportContext());

				JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());
				JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);
				
				// obtain command stack
				CommandStack commandStack = getCommandStack();
		
				// execute command
				commandStack.execute(new FilterCommand(dataset, filterData));
				
				cache.resetJasperReport(target.getUri());
			}
		}
	}

}
