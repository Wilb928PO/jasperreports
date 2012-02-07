/**
 * Defines 'headertoolbar' module in jasperreports namespace
 */
(function(global) {
	if (typeof global.jasperreports.tableheadertoolbar !== 'undefined') {
		return;
	}
	
	var js = {
				filters: {
					filterContainerId: "jasperreports_filters"
				},
				drag: {
					dragStarted: false,
					canDrop: false,
					draggedColumnHeaderClass: null,
					dragTableFrameUuid: null,
					cursorInsideMaskPosition: null,
					moveColumnActionData: null,
					dragMaskPosition: null,
					whichTableFrameIndex: null
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
		var gm = global.jasperreports.global,
			jvt = global.jasperreports.reportviewertoolbar,
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
			
			jQuery('.submitFilter', filterDiv).bind(('createTouch' in document) ? 'touchend' : 'click', function(event){
				var self = jQuery(this),
					params = {},
					parentForm = self.parent(),
					currentHref = parentForm.attr("action"),
					parentFilterDiv = self.closest('.filterdiv'),
					actionData = jQuery.parseJSON(parentFilterDiv.attr('data-filter')),
					contextStartPoint = jQuery('.' + parentFilterDiv.attr('data-forsortlink') + ':first');
				
				// extract form params
				jQuery('.postable', parentForm).each(function(){
					// prevent disabled inputs to get posted
					if(!jQuery(this).is(':disabled')) {
						params[this.name] = this.value;
					}
				});
				
				actionData.filterData = params;
//				var ctx = gm.getExecutionContext(contextStartPoint, currentHref, params);
				var toolbarId = self.closest('.mainReportDiv').find('.toolbarDiv').attr('id'),
					ctx = gm.getToolbarExecutionContext(jQuery('div.columnHeader:first'), 
						currentHref, 
						'jr.action=' + gm.toJsonString(actionData), 
						jvt.performAction, 
						[toolbarId], 
						true);
				
				if (ctx) {
					parentFilterDiv.hide();
					ctx.run();
				}		
			});
			
			// show the second filter value for options containing 'between'
			jQuery('.filterOperatorTypeValueSelector', filterDiv).bind('change', function (event) {
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
			
			jQuery('.clearFilter', filterDiv).bind(('createTouch' in document) ? 'touchend' : 'click', function(event){
				var self = jQuery(this),
					parentForm = self.parent(),
					currentHref = parentForm.attr("action"),
					parentFilterDiv = self.closest('.filterdiv'),
					actionData = jQuery.parseJSON(parentFilterDiv.attr('data-clear')),
					params = actionData.filterData,
					contextStartPoint = jQuery('.' + parentFilterDiv.attr('data-forsortlink') + ':first');
				
				// extract form params
				jQuery('.forClear', parentForm).each(function(){
					// prevent disabled inputs to get posted
					if(!jQuery(this).is(':disabled')) {
						params[this.name] = this.value;
					}
				});
				
//				var ctx = gm.getExecutionContext(contextStartPoint, currentHref, params);
				var toolbarId = self.closest('.mainReportDiv').find('.toolbarDiv').attr('id'),
					ctx = gm.getToolbarExecutionContext(jQuery('div.columnHeader:first'), 
							currentHref, 
							'jr.action=' + gm.toJsonString(actionData), 
							jvt.performAction, 
							[toolbarId], 
							true);
				
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
		var gm = global.jasperreports.global,
			headertoolbarEvent = gm.events.HEADERTOOLBAR_INIT;
		
		// init should be done only once
		if (headertoolbarEvent.status === 'default') {
			// disable browser contextual menu when right-clicking
			jQuery(document).bind("contextmenu",function(e){  
		        return false;  
		    });
			
//			jQuery('.jrtableframe').live('click', 
//					function(event) {
//						event.stopPropagation();
//						
//						var target = jQuery(event.target),
//							currentTarget = jQuery(this),
//							colHeader = target.closest('.columnHeader'); 
//						if (colHeader.size() == 1) {						// first look for column header
//							colHeader.trigger('highlight');
//						} else {											// look for columns
//							var column = target.closest('.column');
//							if (column.size() == 1) {
//								var colName = /col_(\w+)/.exec(column.attr('class'));
//								if(colName && colName.length > 1) {
//									currentTarget.find('.header_' + colName[1]).trigger('highlight');
//								}
//							}
//						}
//					}
//			);

			jQuery('.jrtableframe').live('click',
					/**
					 * Highlight a column by determining where the click was performed inside the table frame
					 */
					function(event) {
						var target = jQuery(event.target),
							currentTarget = jQuery(this),
							arrHeaderData = currentTarget.data('cachedHeaderData');
						
						if (!arrHeaderData) {
							// find headers inside frame
							var headers = jQuery('.columnHeader', currentTarget),
								header,
								headerName;
							
							arrHeaderData = [];
							for (var i=0, ln = headers.length; i < ln; i++) {
								header = jQuery(headers[i]);
								headerName = /header_(\w+)/.exec(header.attr('class'));
								if(headerName && headerName.length > 1) {
									arrHeaderData.push({
										maxLeft: header.position().left,
										maxRight: header.position().left + header.width(),
										headerClass: '.header_' + headerName[1],
										toString: function () {return '{' + this.maxRight + ', ' + this.headerClass + '}'}
									});
								}
							}
							currentTarget.data('cachedHeaderData', arrHeaderData);
						}			
						
						// determine click position inside frame
						var clickPositionInFrame = event.pageX - currentTarget.offset().left,
							hd;
						
						for (var i = 0, ln = arrHeaderData.length; i < ln; i++) {
							hd = arrHeaderData[i];
							if (clickPositionInFrame <= hd.maxRight) {
								event.stopPropagation(); // cancel event bubbling here to prevent parent frames to respond to the same event
								currentTarget.find(hd.headerClass).trigger('highlight');
								break;
							}
						}
					}
			);
			
			jQuery('.jrPage').live('mousemove', function(event) {
				var dragObj = global.jasperreports.tableheadertoolbar.drag;
				
				if (dragObj.dragStarted) {
					var currentDraggedColumnHeader = dragObj.draggedColumnHeaderClass,
						tableFrameElement = jQuery('.jrtableframe[data-uuid=' + dragObj.dragTableFrameUuid + ']').get(dragObj.whichTableFrameIndex), // find tableFrame by uuid
						tableFrame = jQuery(tableFrameElement),
						arrHeaderData = tableFrame.data('cachedHeaderData'),
						centerOfHeaderMaskPos = event.pageX - tableFrame.offset().left + dragObj.cursorInsideMaskPosition,
						currentHeader = jQuery(currentDraggedColumnHeader, tableFrame),
						currentColPosition = jQuery('.columnHeader', tableFrame).index(currentHeader),
						hd;
					
					for (var i = 0, ln = arrHeaderData.length; i < ln; i++) {
						hd = arrHeaderData[i];
						if (centerOfHeaderMaskPos <= hd.maxRight) {
							
							if (currentDraggedColumnHeader == hd.headerClass) {
								dragObj.canDrop = false;
							} else {
								dragObj.canDrop = true;
								dragObj.moveColumnActionData = {actionName: 'move',
															moveColumnData: {
															uuid: dragObj.dragTableFrameUuid,
															columnToMoveIndex: currentColPosition,
															columnToMoveNewIndex: i,
														}};
							}
							
							break;
						}
					}
				}
			});

            jQuery('.columnHeader').live('highlight',	// FIXMEJIVE 'columnHeader' hardcoded in TableReport.java
            		function(event) {
            			// hide all other popupdivs
            			jQuery('.popupdiv').fadeOut(100);
            			
		            	var self = jQuery(this),
		            		popupId = self.attr('data-popupId'),
		            		popupDiv = jQuery('#tbl_' + popupId),
		            		headerToolbar = jQuery('.headerToolbar', popupDiv),
		            		headerToolbarMask = jQuery('.headerToolbarMask', popupDiv),
		            		parentFrame = self.closest('.jrtableframe'),
		            		columnSelectorPrefix = '.col_',
		            		columnNameSel = columnSelectorPrefix + self.attr('data-popupColumn'), // FIXMEJIVE 'col_' prefix hardcoded in TableReport.java
		            		firstElem = jQuery(columnNameSel + ':first', parentFrame),
		            		lastElem = jQuery(columnNameSel + ':last', parentFrame),
		            		headerSelectorPrefix = '.header_',
		            		headerNameSel = headerSelectorPrefix + self.attr('data-popupColumn');
		            	
		            	headerToolbarMask.data('columnHeaderClass', headerNameSel);
		            	headerToolbarMask.data('tableFrameUuid', parentFrame.attr('data-uuid'));
		            	headerToolbarMask.data('tableFrameIndex', jQuery('.jrtableframe[data-uuid=' + parentFrame.attr('data-uuid') + ']').index(parentFrame));
		            	
		            	// determine left and right columns
		            	var leftColName = self.prev('.columnHeader').attr('data-popupColumn'),
		            		rightColName = self.next('.columnHeader').attr('data-popupColumn'),
		            		leftColumnSelector = leftColName != null ? columnSelectorPrefix + leftColName : null,
		                	rightColumnSelector = rightColName !=null ? columnSelectorPrefix + rightColName : null;
		            	
		            	if (firstElem && lastElem) {
	            			headerToolbar.css({
	            				left: '0px'
	            			});
	            			
	            			/**
	            			 * The popupDiv is in the first 'jrPage'; the column header is in a 'jrtableframe'
	            			 * So, to calculate popupDiv's top and left we need to add each parent's top/left until jrPage is reached
	            			 */
	            			var popupTop = self.position().top,
	            				popupLeft = self.position().left,
	            				closestPage = self.closest('.jrPage'),	            			
	            				selfParents = self.parents();
	            			
	            			for (var i = 0, ln = selfParents.size(); i < ln; i ++) {
	            				var parent = jQuery(selfParents[i]);
	            				if (parent.is(closestPage)) {
	            					break;
	            				}
	            				if (parent.position()) {
	            					popupTop += parent.position().top;
	            					popupLeft += parent.position().left;
	            				}
	            			}
	            			
	            			
	            			// the popup div contains headerToolbar(fixed size) and headerToolbarMask divs
	            			
	            			headerToolbarMask.css({
		            			position: 'absolute',
		            			'z-index': 999999,
		            			width: firstElem.width() + 'px',
		            			height: (lastElem.position().top + lastElem.height() - self.position().top) + 'px',
		            			left: '0px',
		            			top: '0px'
		            		});
		            	
			            	popupDiv.css({
			                    'z-index': 999998,
			                    width: firstElem.width() + 'px',
			                    left: popupLeft  + 'px',
			                    top: popupTop + 'px'
			                });
			            	
			            	var handlesArr = [];
			            	if (leftColumnSelector) {
			            		handlesArr.push('w');
			            	}
			            	if (rightColumnSelector) {
			            		handlesArr.push('e');
			            	}
			            	
			            	headerToolbarMask.draggable({
			            		start: function(event, ui) {
			            			var dragObj = global.jasperreports.tableheadertoolbar.drag,
			            				self = jQuery(this);
			            			
			            			self.prev().hide();
			            			
			            			dragObj.dragStarted = true;
			            			dragObj.draggedColumnHeaderClass = self.data('columnHeaderClass');
			            			dragObj.dragTableFrameUuid = self.data('tableFrameUuid');
			            			dragObj.cursorInsideMaskPosition = self.width()/2 - (event.originalEvent.pageX - self.offset().left);	// relative to the middle
			            			dragObj.dragMaskPosition = self.position();
			            			
			            			// which table frame with same uuid
			            			dragObj.whichTableFrameIndex =  self.data('tableFrameIndex');
			            		},
			            		drag: function(event, ui) {
			            		},
			            		stop: function(event, ui) {
			            			var	self = jQuery(this),
			            				dragObj = global.jasperreports.tableheadertoolbar.drag;
			            			
			            			dragObj.dragStarted = false;
			            			
			            			if (dragObj.canDrop) {
						            	var	gm = global.jasperreports.global,
					            			resizeActionLink = self.attr('data-resizeAction'),
				                	    	toolbarId = self.closest('.mainReportDiv').find('.toolbarDiv').attr('id'),
				                	    	ctx = gm.getToolbarExecutionContext(self, 
				                	    										resizeActionLink, 
				                	    										'jr.action=' + gm.toJsonString(dragObj.moveColumnActionData), 
				                	    										js.highlightColumn, 
				                	    										[dragObj.draggedColumnHeaderClass, dragObj.dragTableFrameUuid, dragObj.whichTableFrameIndex, toolbarId], 
				                	    										true);
				                        if (ctx) {
				                            ctx.run();
				                        }
			            			} else {
			            				// move mask back to its place
			            				self.animate(dragObj.dragMaskPosition, function() {
			            					// show the toolbar
			            					jQuery(this).prev().show();
			            				});
			            			}
			            		}
			            	});
			            	
			            	headerToolbarMask.resizable({
			            		handles: handlesArr.join(', '),
			                	resize: function(event, ui) {
			                		var self = jQuery(this);
			                		self.prev().css({left: self.css('left')}); // ensures that headerToolbar moves along with the mask
			                	},
			                	stop: function(event, ui) {
			                		var jvt = global.jasperreports.reportviewertoolbar,
			                			self = jQuery(this),
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
			                	    											jvt.performAction, 
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
	
	js.highlightColumn = function (columnHeaderClass, tableFrameUuid, tableFrameIndex, toolbarId) {
		var jvt = global.jasperreports.reportviewertoolbar;
		jvt.performAction(toolbarId);
		
		var tableFrame = jQuery('.jrtableframe[data-uuid=' + tableFrameUuid + ']').get(tableFrameIndex);
		jQuery(columnHeaderClass, tableFrame).trigger('click').trigger('highlight');
	};
	
	js.registerTableHeaderEvents = function (popupId, arrPopupHtml) {
		var gm = global.jasperreports.global,
			jvt = global.jasperreports.reportviewertoolbar,
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
			
			// hide popup when mouse is out
			popupDiv.bind('dblclick', function(event) {
//			popupDiv.bind('mouseleave', function(event) {
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
                										jvt.performAction, 
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
	
	global.jasperreports.tableheadertoolbar = js;
} (this));