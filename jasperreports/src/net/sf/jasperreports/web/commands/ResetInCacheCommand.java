package net.sf.jasperreports.web.commands;

import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.repo.JasperDesignCache;

public class ResetInCacheCommand implements Command 
{
	
	private Command command;
	private ReportContext reportContext;
	private String uri;
	
	public ResetInCacheCommand(Command command, ReportContext reportContext, String uri) 
	{
		this.command = command;
		this.reportContext = reportContext;
		this.uri = uri;
	}

	public void execute() 
	{
		command.execute();
		
		JasperDesignCache.getInstance(reportContext).resetJasperReport(uri);
	}
	
	public void undo() 
	{
		command.undo();
		
		JasperDesignCache.getInstance(reportContext).resetJasperReport(uri);
	}

	public void redo() 
	{
		execute();
	}

}
