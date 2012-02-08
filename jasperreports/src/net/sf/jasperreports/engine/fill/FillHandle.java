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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Interface implemented by classes that are used to perform report filling asychronously.
 * <p>
 * An instance of this type can be used as a handle to an asychronous fill process.
 * The main benefit of this method is that the filling process can be cancelled.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface FillHandle
{

	/**
	 * Adds a listener to the filling process.
	 * 
	 * @param listener the listener to be added
	 */
	void addListener(AsynchronousFilllListener listener);
	
	/**
	 * Removes a listener from the filling process.
	 * 
	 * @param listener the listener to be removed
	 * @return <tt>true</tt> if the listener was found and removed
	 */
	boolean removeListener(AsynchronousFilllListener listener);
	
	/**
	 * Adds a fill listener to the filling process.
	 * 
	 * The fill listener is notified of intermediate events that occur
	 * during the report generation.
	 * 
	 * @param listener the listener to add
	 */
	void addFillListener(FillListener listener);
	
	/**
	 * Starts the filling process asychronously.
	 * <p>
	 * The filling can be launched on a new thread and the method exits
	 * after the execution is started.
	 * <p>
	 * When the filling finishes either in success or failure, the listeners
	 * are notified.  
	 */
	void startFill();
	
	/**
	 * Cancels the fill started by the handle.
	 * <p>
	 * The method sends a cancel signal to the filling process.
	 * When the filling process will end, the listeners will be notified 
	 * that the filling has been cancelled.
	 * 
	 * @throws JRException
	 */
	void cancellFill() throws JRException;
	
	/**
	 * Determines wheter a page generated by the fill process is final or not.
	 * 
	 * @param pageIdx the page index
	 * @return whether the page at the specified index is final or can be subject to 
	 * future changes
	 * @see FillListener#pageUpdated(JasperPrint, int)
	 */
	boolean isPageFinal(int pageIdx);
	
}
