package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.engine.design.JasperDesign;


public class SaveAction extends AbstractAction {

	public SaveAction() {
	}

	public String getName() {
		return "save_action";
	}

	public void performAction() 
	{
		JasperDesign jasperDesign = getJasperDesign();
	}

}
