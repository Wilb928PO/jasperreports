package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.web.commands.CommandStack;

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="actionName")
public abstract class AbstractAction implements Action {
	
	private static final String PARAM_COMMAND_STACK = "net.sf.jasperreports.command.stack";
	
	private ReportContext reportContext;
	private String reportUri;
	private JasperDesign jasperDesign;
	private CommandStack commandStack;
	
	public AbstractAction(){
	}
	
	public void init(ReportContext reportContext, String reportUri) 
	{
		this.reportContext = reportContext;
		this.reportUri = reportUri;
		
		jasperDesign = JasperDesignCache.getInstance(reportContext).getJasperDesign(reportUri);
		commandStack = (CommandStack)reportContext.getParameterValue(PARAM_COMMAND_STACK);
		
		if (commandStack == null) {
			commandStack = new CommandStack();
			reportContext.setParameterValue(PARAM_COMMAND_STACK, commandStack);
		}
	}
	
	public JasperDesign getJasperDesign() {
		return jasperDesign;
	}
	
	public ReportContext getReportContext() {
		return reportContext;
	}
	
	public void run() {
		performAction();
		resetJasperReport();
	}
	
	public void resetJasperReport() {
		JasperDesignCache.getInstance(reportContext).set(reportUri, jasperDesign);
	}
	
	public CommandStack getCommandStack() {
		return commandStack;
	}
	
	public abstract void performAction();
	
}
