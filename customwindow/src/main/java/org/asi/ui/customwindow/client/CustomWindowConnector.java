package org.asi.ui.customwindow.client;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.window.WindowMode;
import org.asi.ui.customwindow.CustomWindow;

@Connect(CustomWindow.class)
public class CustomWindowConnector extends WindowConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        getWidget().minimized=uidl.getBooleanAttribute("minimize");
        getWidget().minX=uidl.getIntAttribute("minimizeleft");
        getWidget().minY=uidl.getIntAttribute("minimizetop");
        getWidget().minwidth=uidl.getIntAttribute("minimizewidth");
        getWidget().updateMinimize();
        getWidget().minimizeRestoreBox
                .setId(getConnectorId() + "_window_minimizerestore");
    }

    @Override
    public VCustomWindow getWidget() {
        return (VCustomWindow) super.getWidget();
    }
    @Override
    protected void onMaximizeRestore() {
        if(!getWidget().minimized){
            super.onMaximizeRestore();
            getWidget().winState=(getState().windowMode==WindowMode.MAXIMIZED);
        }
    }
}
