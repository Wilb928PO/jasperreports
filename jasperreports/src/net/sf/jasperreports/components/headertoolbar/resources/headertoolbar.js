/**
 * Defines 'headertoolbar' module in JasperReports namespace
 */
(function(global) {
	if (typeof global.JasperReports.modules.headertoolbar !== 'undefined') {
		return;
	}
	
	var js = {
				filters: {
					filterContainerId: "jasperreports_filters"
				}
		};
	
	/**
	 * Creates a unique filter div
	 * 
	 * @param uniqueId
	 * @param arrFilterDiv an array with div's html
	 * @param filtersJsonString a JSON string of a java.util.List<net.sf.jasperreports.components.sort.FieldFilter>
	 */
	js.createFilterDiv = function (uniqueId, arrFilterDiv, filtersJsonString) {
		var gm = global.JasperReports.modules.global,
			filterContainerId = js.filters.filterContainerId,
			filterContainerDiv = "<div id='" + filterContainerId + "'></div>",
			fcuid = '#' + filterContainerId,
			uid = '#' + uniqueId;
		
		// if filter container does not exist, create it
		if (jQuery(fcuid).size() == 0) {
			 jQuery(gm.reportContainerSelector).append(filterContainerDiv);
		}
		
		// if filter with id of 'uniqueId' does not exist, append it to filter container
		if (jQuery(uid).size() == 0) {
			jQuery(fcuid).append(arrFilterDiv.join(''));
			var filterDiv = jQuery(uid);
			
			// attach filter form events
			jQuery('.hidefilter', filterDiv).bind(('createTouch' in document) ? 'touchend' : 'click', function(event){
				jQuery(this).parent().hide();
			});
			
			filterDiv.draggable();
			
			// 'Enter' key press for filter value triggers 'contextual' submit
			jQuery('.filterValue', filterDiv).bind('keypress', function(event) {
				if (event.keyCode == 13) {
					event.preventDefault();
					if ('createTouch' in document) {	// trigger does not seem to work on safari mobile; doing workaround
						var el = jQuery('.submitFilter', filterDiv).get(0);
						var evt = document.createEvent("MouseEvents");
						evt.initMouseEvent("touchend", true, true);
						el.dispatchEvent(evt);
					} else {
						jQuery('.submitFilter', filterDiv).trigger('click');
					}
				}
			});
			
			jQuery('.submitFilter', filterDiv).live(('createTouch' in document) ? 'touchend' : 'click', function(event){
				var params = {},
					parentForm = jQuery(this).parent(),
					currentHref = parentForm.attr("action"),
					parentFilterDiv = jQuery(this).closest('.filterdiv'),
					contextStartPoint = jQuery('.' + parentFilterDiv.attr('data-forsortlink') + ':first');
				
				// extract form params
				jQuery('.postable', parentForm).each(function(){
					// prevent disabled inputs to get posted
					if(!jQuery(this).is(':disabled')) {
						params[this.name] = this.value;
					}
				});
				
				var ctx = gm.getExecutionContext(contextStartPoint, currentHref, params);
				
				if (ctx) {
					parentFilterDiv.hide();
					ctx.run();
				}		
			});
			
			// show the second filter value for options containing 'between'
			jQuery('.filterOperatorTypeValueSelector', filterDiv).live('change', function (event) {
				var optionValue = jQuery(this).val();
				if (optionValue && optionValue.toLowerCase().indexOf('between') != -1) {
					jQuery('.filterValueEnd', filterDiv)
						.removeClass('hidden')
						.removeAttr('disabled');
				} else {
					jQuery('.filterValueEnd', filterDiv)
						.addClass('hidden')
						.attr('disabled', true);
				}
			});
			
			jQuery('.clearFilter', filterDiv).live(('createTouch' in document) ? 'touchend' : 'click', function(event){
				var params = {},
					parentForm = jQuery(this).parent(),
					currentHref = parentForm.attr("action"),
					parentFilterDiv = jQuery(this).closest('.filterdiv'),
					contextStartPoint = jQuery('.' + parentFilterDiv.attr('data-forsortlink') + ':first');
				
				// extract form params
				jQuery('.forClear', parentForm).each(function(){
					// prevent disabled inputs to get posted
					if(!jQuery(this).is(':disabled')) {
						params[this.name] = this.value;
					}
				});
				
				var ctx = gm.getExecutionContext(contextStartPoint, currentHref, params);
				
				if (ctx) {
					parentFilterDiv.hide();
					ctx.run();
				}		
			});
		} else {
			// update existing filter with values from filtersJsonString
			var arrFilters = jQuery.parseJSON(filtersJsonString);
			var found = false;
			if (arrFilters) {
				var filterDiv = jQuery(uid),
					currentFilterField = jQuery('.filterField', filterDiv).val();
				
				for (var i=0, ln = arrFilters.length; i < ln; i++) {
					var filter = arrFilters[i];
					if (filter.field === currentFilterField) {
						jQuery('.filterValueStart', filterDiv).val(filter.filterValueStart);
						jQuery('.filterValueEnd', filterDiv).val(filter.filterValueEnd);
						jQuery('.filterOperatorTypeValueSelector', filterDiv).val(filter.filterTypeOperator);
						
						if (filter.filterTypeOperator && filter.filterTypeOperator.toLowerCase().indexOf('between') != -1) {
							jQuery('.filterValueEnd', filterDiv).removeClass('hidden').removeAttr('disabled');
						} else {
							jQuery('.filterValueEnd', filterDiv).addClass('hidden').attr('disabled', true);
						}
						
						// show clear button
						jQuery('.clearFilter', filterDiv).show();
						
						found = true;
						break;
					}
				}
				
				// reset filter controls
				if (!found) {
					jQuery('.filterValueStart', filterDiv).val("");
					jQuery('.filterValueEnd', filterDiv).val("");
					jQuery('.filterOperatorTypeValueSelector :selected', filterDiv).attr('selected', false);
					
					// hide clear button
					jQuery('.clearFilter', filterDiv).hide();
				}
			}
		}
		
	};

	/**
	 * Initialization and event registration for non-dynamic JR elements
	 */
	js.init = function() { 
		var gm = global.JasperReports.modules.global,
			headertoolbarEvent = gm.events.HEADERTOOLBAR_INIT;
		
		// init should be done only once
		if (headertoolbarEvent.status === 'default') {
			// disable browser contextual menu when right-clicking
			jQuery(document).bind("contextmenu",function(e){  
		        return false;  
		    });
	
            jQuery('.columnHeader').live('click',	// FIXMEJIVE 'columnHeader' hardcoded in TableReport.java
            		function(event) {
            			// hide all other popupdivs
            			jQuery('.popupdiv').fadeOut(100);
            			
		            	var self = jQuery(this),
		            		popupId = self.attr('data-popupId'),
		            		popupDiv = jQuery('#tbl_' + popupId),
		            		headerToolbar = jQuery('.headerToolbar', popupDiv),
		            		headerToolbarMask = jQuery('.headerToolbarMask', popupDiv),
		            		columnSelectorPrefix = '.col_',
		            		columnNameSel = columnSelectorPrefix + self.attr('data-popupColumn'), // FIXMEJIVE 'col_' prefix hardcoded in TableReport.java
		            		firstElem = jQuery(columnNameSel + ':first'),
		            		lastElem = jQuery(columnNameSel + ':last'),
		            		headerSelectorPrefix = '.header_',
		            		headerNameSel = headerSelectorPrefix + self.attr('data-popupColumn');
		            	
		            	// determine left and right columns
		            	var leftColName = self.prev('.columnHeader').attr('data-popupColumn'),
		            		rightColName = self.next('.columnHeader').attr('data-popupColumn'),
		            		leftColumnSelector = leftColName != null ? columnSelectorPrefix + leftColName : null,
		                	rightColumnSelector = rightColName !=null ? columnSelectorPrefix + rightColName : null;
		            	
		            	if (firstElem && lastElem) {
	            			headerToolbar.css({
	            				left: '0px'
	            			});

	            			headerToolbarMask.css({
		            			position: 'absolute',
		            			'z-index': 999999,
		            			width: firstElem.width() + 'px',
		            			height: (firstElem.height() + lastElem.offset().top - self.offset().top + lastElem.height()) + 'px',
		            			left: '0px'
		            		});
		            	
			            	popupDiv.css({
			                    'z-index': 999998,
			                    left: (self.position().left + self.parent().position().left)  + 'px',
			                    top: (self.position().top + self.parent().position().top/* - popupDiv.height()*/) + 'px'
			                });
			            	
			            	var handlesArr = [];
			            	if (leftColumnSelector) {
			            		handlesArr.push('w');
			            	}
			            	if (rightColumnSelector) {
			            		handlesArr.push('e');
			            	}
			            	
			            	headerToolbarMask.resizable({
			            		handles: handlesArr.join(', '),
			                	resize: function(event, ui) {
			                		var self = jQuery(this);
			                		self.prev().css({left: self.css('left')});
			                	},
			                	stop: function(event, ui) {
			                		var self = jQuery(this),
			                			currentLeftPx = self.css('left'),
			                			currentLeft = parseInt(currentLeftPx.substring(0, currentLeftPx.indexOf('px'))),
			                			deltaLeft = ui.originalPosition.left - currentLeft,
			                			deltaWidth = self.width() - ui.originalSize.width,
			                			direction;
			                		
			                	    if (deltaWidth != 0 && deltaLeft == 0) {	// deltaWidth > 0 ? 'resize column right positive' : 'resize column right negative'
		                	    		direction = 'right';
			                	    } else if (deltaLeft != 0) {				// deltaLeft > 0 ? 'resize column left positive' : 'resize column left negative'
			                	    	direction = 'left';
			                	    }
			                	    var uuid = jQuery(headerNameSel+':first').parent('.jrtableframe').attr('data-uuid');
			                	    var actionData = {	actionName: 'resize',
			                	    					resizeColumnData: {
			                	    						uuid: uuid,
			                	    						columnIndex: jQuery('.columnHeader').index(jQuery(headerNameSel+':first')),
			                	    						direction: direction,
			                	    						width: self.width()
			                	    					}
			                	    	},
			                	    	resizeActionLink = self.attr('data-resizeAction'),
			                	    	toolbarId = self.closest('.mainReportDiv').find('.toolbarDiv').attr('id'),
			                	    	ctx = gm.getToolbarExecutionContext(jQuery('div.columnHeader:first'), 
			                	    											resizeActionLink, 
			                	    											'jr.action=' + gm.toJsonString(actionData), 
			                	    											gm.performAction, 
			                	    											[toolbarId], 
			                	    											true);

			                        if (ctx) {
			                            ctx.run();
			                        }
			                	    
			                	    
			                	}
			                });
			            	
			            	popupDiv.fadeIn(100);
	            		}
		         	}
            );
            
			headertoolbarEvent.status = 'finished';
			gm.processEvent(headertoolbarEvent.name);
		}
		
	};
	
	js.registerTableHeaderEvents = function (popupId, arrPopupHtml) {
		var gm = global.JasperReports.modules.global,
			filterContainerId = "jive_dialogs", //js.filters.filterContainerId,
			filterContainerDiv = "<div id='" + filterContainerId + "'></div>",
			fcuid = '#' + filterContainerId,
			uid = '#tbl_' + popupId;
		
		// if filter container does not exist, create it
		if (jQuery(fcuid).size() == 0) {
			 jQuery(gm.reportContainerSelector).append(filterContainerDiv);
		}
		
		// if filter with id of 'uniqueId' does not exist, append it to filter container
		if (jQuery(uid).size() == 0) {
			jQuery(fcuid).append(arrPopupHtml.join(''));
			var popupDiv = jQuery(uid);
			
			popupDiv.bind('dblclick', function(event) {
				jQuery(this).fadeOut(100);
			});

			/**
			 * Handle sort when clicking sort icons
			 */
			jQuery('.sortSymbolImage', popupDiv).bind('click', function(event) {
				event.preventDefault();
                var self = jQuery(this);
                	currentHref = jQuery(this).attr("data-href"),
                	param = 'jr.action=' + jQuery(this).attr("data-sortdata"),
                	toolbarId = self.closest('.mainReportDiv').find('.toolbarDiv').attr('id'),
//                	ctx = gm.getExecutionContext(this, currentHref, param);
                	ctx = gm.getToolbarExecutionContext(jQuery('div.columnHeader:first'), // getToolbarExecutionContext(startPoint, requestedUrl, params, callback, arrCallbackArgs, isJSON) 
                										currentHref, 
                										param, 
                										gm.performAction, 
    	    											[toolbarId],  
                										true);

                if (ctx) {
                    ctx.run();
                }
			});
			
			/**
             * Show filter div when clicking the filter icon
             */
            jQuery('.filterSymbolImage', popupDiv).bind('click', function(event) {
                var self = jQuery(this),
                	filterDiv = jQuery('#' + jQuery(this).parents('.popupdiv').attr('data-filterid'));

                // hide all other open filters FIXMEJIVE: this will close all visible filters from all reports on the same page
                jQuery('.filterdiv').filter(':visible').each(function (index, element) {
                    jQuery(element).hide();
                });
                
                if (filterDiv.size() == 1) {
                    filterDiv.css({
                        position: 'absolute',
                        'z-index': 999998,
                        left: (40 + self.closest('.popupdiv').position().left)  + "px", // FIXMEJIVE filterdiv should be moved into popupdiv	
                        top: (self.closest('.headerToolbar').position().top + self.closest('.popupdiv').position().top + 3) + "px"
                    });
                    filterDiv.show();
                }
            });
            
            /**
             * Sort and filter hover
             */
            jQuery('.sortSymbolImage, .filterSymbolImage', popupDiv).live('mouseenter', function(event) {
            	var self = jQuery(this),
            		hoverSrc = self.attr('data-hover');
            	if (hoverSrc && hoverSrc.length > 0) {
            		self.attr('src', hoverSrc);
            	}
            	
            });
            jQuery('.sortSymbolImage, .filterSymbolImage', popupDiv).live('mouseleave', function(event) {
            	var self = jQuery(this),
	        		hoverSrc = self.attr('data-hover');
	        	if (hoverSrc && hoverSrc.length > 0) {	// if there were any hover src, reset original src
	        		self.attr('src', self.attr('data-src'));
	        	}
            });
            
		}
	};
	
	global.JasperReports.modules.headertoolbar = js;
} (this));