/*
 * Copyright 2014 Abhiram.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.asi.ui.customwindow.client;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VWindow;

/**
 *
 * @author Abhiram
 */
public class VCustomWindow extends VWindow {

    public Element minimizeRestoreBox = DOM.createDiv();
    boolean minimized = false;
    
    int maxwidth = 200;
    int maxheight = 150;
    int minwidth = 86;
    int minheight = 37;
    int maxX = 86;
    int maxY = 52;
    
    int minX = 6;
    int minY = 90;
    
    private boolean draggable = true;
    boolean winState=false;
    public VCustomWindow() {
        super();
    }
    
    void updateMinimize() {

        DOM.setElementProperty(minimizeRestoreBox, "className", "v-window-minimizebox");
        DOM.setElementAttribute(minimizeRestoreBox, "tabindex", "0");

        Roles.getButtonRole().set(minimizeRestoreBox);
        Roles.getButtonRole().setAriaLabelProperty(minimizeRestoreBox, "minimize button");
        DOM.insertChild((Element) getElement().getFirstChildElement().getFirstChildElement(), minimizeRestoreBox, 2);
        closeBox.setTitle("Close");
        header.setTitle(header.getInnerText());
        if(minimized){
            header.removeClassName("minimizable");
            minimizeRestoreBox.addClassName("minimized");
            minimizeRestoreBox.setTitle("Minimize Restore");
            maximizeRestoreBox.addClassName("minimized");
            updateMinimizeData(minimized,false);
        }else{
            header.addClassName("minimizable");
            minimizeRestoreBox.removeClassName("minimized");
            minimizeRestoreBox.setTitle("Minimize");
            maximizeRestoreBox.removeClassName("minimized");
            if(maximizeRestoreBox.hasClassName("v-window-maximizebox")){
                maximizeRestoreBox.setTitle("Full Screen");
            }else{
                maximizeRestoreBox.setTitle("Maximize Restore");
            }
            
        }
        
//        DOM.sinkEvents(minimizeRestoreBox, Event.ONCLICK);
    }

    @Override
    public void onBrowserEvent(Event event) {
        final Element target = DOM.eventGetTarget(event);
        if (target == minimizeRestoreBox) {
            if (event.getTypeInt() == Event.ONCLICK) {
                onClickMinimize();
                event.stopPropagation();
            }
        } else {
            super.onBrowserEvent(event);
        }

    }

    private void onClickMinimize() {
        updateMinimizeData(!minimized,true);
    }
    @Override
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        super.setDraggable(draggable);
    }
    private boolean isDraggable() {
        return draggable;
    }
    public void updateMinimizeData(boolean minimize,boolean updateVariable) {
        this.minimized = minimize;
        setDraggable(!winState && !minimized);
        setResizable(!winState && !minimized);
        if (minimized) {
            if(updateVariable){
            maxwidth = getElement().getOffsetWidth();
            maxheight = getElement().getOffsetHeight();
            maxX=getPopupLeft();
            maxY=getPopupTop();
            }
            setWidth(minwidth + "px");
            setHeight(minheight + "px");
            setPopupPosition(minX, minY);
        } else {
            setWidth(maxwidth + "px");
            setHeight(maxheight + "px");
            setPopupPosition(maxX, maxY);
        }
        
        if(updateVariable){
            sendMinimizeUpdate();
        }
        
    }
    
    private int getMinHeight() {
        return getPixelValue(getElement().getStyle().getProperty("minHeight"));
    }

    private int getMinWidth() {
        return getPixelValue(getElement().getStyle().getProperty("minWidth"));
    }

    private static int getPixelValue(String size) {
        if (size == null || !size.endsWith("px")) {
            return -1;
        } else {
            return Integer.parseInt(size.substring(0, size.length() - 2));
        }
    }

    private void sendMinimizeUpdate() {
        client.updateVariable(id, "minimizeClickEvent", minimized, true);
    }
}
