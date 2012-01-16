package net.sf.jasperreports.web.actions;

import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.repo.JasperDesignReportResource;
import net.sf.jasperreports.repo.JasperDesignReportResourceCache;
import net.sf.jasperreports.web.commands.CommandStack;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="actionName")
@JsonSubTypes({ 
	 @Type(value = ResizeColumnAction.class, name = "resize"),  
	 @Type(value = UndoAction.class, name = "undo"),  
	 @Type(value = RedoAction.class, name = "redo")  
})
public abstract class AbstractAction implements Action {
	
	private static final String PARAM_COMMAND_STACK = "net.sf.jasperreports.command.stack";
	
	private JasperDesignReportResource jasperDesignResource;
	private CommandStack commandStack;
	
	public AbstractAction(){
	}
	
	public void init(ReportContext reportContext, String reportUri) {
		jasperDesignResource = JasperDesignReportResourceCache.getInstance(reportContext).getResource(reportUri);
		commandStack = (CommandStack)reportContext.getParameterValue(PARAM_COMMAND_STACK);
		
		if (commandStack == null) {
			commandStack = new CommandStack();
			reportContext.setParameterValue(PARAM_COMMAND_STACK, commandStack);
		}
	}
	
	public JasperDesign getJasperDesign() {
		return jasperDesignResource.getJasperDesign();
	}
	
	public void run() {
		performAction();
		resetJasperReport();
	}
	
	public void resetJasperReport() {
		jasperDesignResource.setReport(null);
	}
	
	public CommandStack getCommandStack() {
		return commandStack;
	}
	
	public abstract void performAction();
	
}
