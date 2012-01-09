/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.web.servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.AsynchronousFillHandle;
import net.sf.jasperreports.engine.fill.AsynchronousFilllListener;
import net.sf.jasperreports.engine.fill.FillListener;

/**
 * Generated report accessor used for asynchronous report executions that publishes pages
 * before the entire report has been generated.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class AsyncJasperPrintAccessor implements JasperPrintAccessor, AsynchronousFilllListener, FillListener
{

	private static final Log log = LogFactory.getLog(AsyncJasperPrintAccessor.class);
	
	private final AsynchronousFillHandle fillHandle;
	private final Lock lock;
	private final Condition pageCondition;
	private final Map<Integer, Long> trackedPages = new HashMap<Integer, Long>();
	
	private volatile boolean done;
	private volatile JasperPrint jasperPrint;
	private volatile int pageCount;
	
	/**
	 * Create a report accessor.
	 * 
	 * @param fillHandle the asynchronous fill handle used by this accessor
	 */
	public AsyncJasperPrintAccessor(AsynchronousFillHandle fillHandle)
	{
		this.fillHandle = fillHandle;
		lock = new ReentrantLock();
		pageCondition = lock.newCondition();
		
		fillHandle.addListener(this);
		fillHandle.addFillListener(this);
	}
	
	public ReportPageStatus pageStatus(int pageIdx, Long pageTimestamp)
	{
		if (done)
		{
			return pageIdx < pageCount ? ReportPageStatus.PAGE_FINAL : ReportPageStatus.NO_SUCH_PAGE;
		}
		
		lock.lock();
		try
		{
			while (!done && pageIdx >= pageCount)
			{
				pageCondition.await();
			}
			
			if (pageIdx >= pageCount)
			{
				return ReportPageStatus.NO_SUCH_PAGE;
			}
			
			if (fillHandle.isPageFinal(pageIdx))
			{
				trackedPages.remove(pageIdx);
				return ReportPageStatus.PAGE_FINAL;
			}
			
			long timestamp;
			boolean modified;
			
			Long lastUpdate = trackedPages.get(pageIdx);
			if (lastUpdate == null)
			{
				// we don't know when exactly the page was modified, using current time
				timestamp = System.currentTimeMillis();
				modified = true;
			}
			else
			{
				timestamp = lastUpdate;
				modified = pageTimestamp == null || pageTimestamp < lastUpdate;
			}
			
			ReportPageStatus status = ReportPageStatus.nonFinal(timestamp, modified);
			// add the page to the tracked map so that we catch updates
			trackedPages.put(pageIdx, timestamp);
			return status;
		}
		catch (InterruptedException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			lock.unlock();
		}
	}

	public JasperPrint getJasperPrint()
	{
		return jasperPrint;
	}

	public Integer getTotalPageCount()
	{
		if (done)
		{
			return jasperPrint.getPages().size();
		}
		
		return null;
	}

	public void reportFinished(JasperPrint jasperPrint)
	{
		lock.lock();
		try
		{
			if (this.jasperPrint == null)
			{
				this.jasperPrint = jasperPrint;
			}
			
			pageCount = jasperPrint.getPages().size();
			done = true;
			trackedPages.clear();
			
			pageCondition.signal();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void reportCancelled()
	{
		//FIXME
	}

	public void reportFillError(Throwable t)
	{
		log.error("Error execution report", t);
		//FIXME
	}

	public void pageGenerated(JasperPrint jasperPrint, int pageIndex)
	{
		lock.lock();
		try
		{
			if (this.jasperPrint == null)
			{
				this.jasperPrint = jasperPrint;
			}
			
			pageCount = pageIndex + 1;
			
			pageCondition.signal();
		}
		finally
		{
			lock.unlock();
		}
	}

	public void pageUpdated(JasperPrint jasperPrint, int pageIndex)
	{
		lock.lock();
		try
		{
			// update the timestamp if the page is tracked
			if (trackedPages.containsKey(pageIndex))
			{
				long timestamp = System.currentTimeMillis();
				trackedPages.put(pageIndex, timestamp);
			}
		}
		finally
		{
			lock.unlock();
		}
	}

}
