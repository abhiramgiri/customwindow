package org.asi.ui.customwindow;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomWindow extends Window {

    private boolean minimize = false;
    private int minimizeWidth = 0;
    private int minimizeLeft = 0;
    private boolean minimizeLeftSet=false;
    private int minimizeTop = 0;
    private boolean minimizeTopSet=false;
    private int winCount = 0;
    private final List<CustomWindow> windowList = new ArrayList<CustomWindow>();

    /**
     * Creates a new, empty window
     */
    public CustomWindow() {
        this("", null);
    }

    /**
     * Creates a new, empty window with a given title.
     *
     * @param caption the title of the window.
     */
    public CustomWindow(String caption) {
        this(caption, null);
    }

    /**
     * Creates a new, empty window with the given content and title.
     *
     * @param caption the title of the window.
     * @param content the contents of the window
     */
    public CustomWindow(String caption, Component content) {
        super(caption, content);

        setSizeUndefined();
    }
    /* ********************************************************************* */

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#paintContent(com.vaadin.server.PaintTarget)
     */
    @Override
    public synchronized void paintContent(PaintTarget target)
            throws PaintException {
        target.addAttribute("minimize", getMinimize());
        target.addAttribute("minimizeleft", getMinimizeLeftPos());
        target.addAttribute("minimizetop", getMinimizeTopPos());
        target.addAttribute("minimizewidth", getMinimizeWidth());
        // Contents of the window panel is painted
        super.paintContent(target);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Panel#changeVariables(java.lang.Object, java.util.Map)
     */

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey("minimizeClickEvent")) {
            final Boolean minimized = (Boolean) variables.get("minimizeClickEvent");
            setMinimize(minimized);
            fireEvent(new MinimizeClickEvent(this, minimized));
        }

    }

    /**
     * An interface used for listening to Window close events. Add the
     * CloseListener to a window and
     * {@link CloseListener#windowClose(CloseEvent)} will be called whenever the
     * user closes the window.
     *
     * <p>
     * Since Vaadin 6.5, removing a window using {@link #removeWindow(Window)}
     * fires the CloseListener.
     * </p>
     */
    public static class MinimizeClickEvent extends Component.Event {

        public static final Method MINIMIZE_CLICK_METHOD;
        private boolean minimize = false;

        static {
            try {
                MINIMIZE_CLICK_METHOD = MinimizeClickListener.class
                        .getDeclaredMethod("minimizeClick",
                                new Class[]{MinimizeClickEvent.class});
            } catch (final java.lang.NoSuchMethodException e) {
                // This should never happen
                e.printStackTrace();
                throw new java.lang.RuntimeException(e);
            }
        }

        public MinimizeClickEvent(Component source, boolean minimize) {
            super(source);
            this.minimize = minimize;
        }

        /**
         * Gets the minimize value
         *
         * @return
         */
        public boolean getMinimizeValue() {
            return minimize;
        }
    }

    /**
     * Interface for the listener for double header column header mouse click
     * events. The doubleHeaderClick method is called when the user presses a
     * double header column cell.
     */
    public interface MinimizeClickListener extends Serializable {

        /**
         * Called when a user clicks a double header column cell
         *
         * @param event The event which contains information about the double
         * header column and the mouse click event
         */
        public void minimizeClick(MinimizeClickEvent event);
    }

    /**
     * Adds a double header click listener which handles the click events when
     * the user clicks on a double header column header cell in the Table.
     * <p>
     * The listener will receive events which contain information about which
     * double header column was clicked and some details about the mouse event.
     * </p>
     *
     * @param listener The handler which should handle the double header click
     * events.
     */
    public void addMinimizeClickListener(MinimizeClickListener listener) {
        addListener("MINIMIZE_CLICK",
                MinimizeClickEvent.class, listener,
                MinimizeClickEvent.MINIMIZE_CLICK_METHOD);
    }

    /**
     * @param listener
     * @deprecated , replaced by
     * {@link #addDoubleHeaderClickListener(DoubleHeaderClickListener)}
     *
     */
    @Deprecated
    public void addListener(MinimizeClickListener listener) {
        addMinimizeClickListener(listener);
    }

    /**
     * Removes a double header click listener
     *
     * @param listener The listener to remove.
     */
    public void removeMinimizeClickListener(MinimizeClickListener listener) {
        removeListener("MINIMIZE_CLICK",
                MinimizeClickEvent.class, listener);
    }

    /**
     * @param listener
     * @deprecated , replaced by
     * {@link #removeDoubleHeaderClickListener(DoubleHeaderClickListener)}
     *
     */
    @Deprecated
    public void removeListener(MinimizeClickListener listener) {
        removeMinimizeClickListener(listener);
    }

    public void setMinimize(boolean minimize) {
        this.minimize = minimize;
    }

    public boolean getMinimize() {
        return minimize;
    }

    public void setMinimizeWidth(int minimizeWidth) {
        if (minimizeWidth < 86) {
            minimizeWidth = 86;
        }
        this.minimizeWidth = minimizeWidth;
    }

    public int getMinimizeWidth() {
        if (minimizeWidth < 86)
            minimizeWidth=86;
        if (minimizeWidth < 150 && this.getCaption().length() > 0) {
            minimizeWidth = 150;
        }
        return minimizeWidth;
    }

    public void setMinimizeLeftPos(int minimizeLeft) {
        minimizeLeftSet=true;
        this.minimizeLeft = minimizeLeft;
    }

    public int getMinimizeLeftPos() {
        UI ui = this.getUI();
        if (ui != null && !minimizeLeftSet) {
            minimizeLeftSet=true;
            minimizeLeft = 5;
            Iterator<Window> a = UI.getCurrent().getWindows().iterator();
        while (a.hasNext()) {
            Window next = a.next();
            if (next instanceof CustomWindow) {
                if(!this.equals((CustomWindow)next)){
                minimizeLeft = minimizeLeft + ((CustomWindow)next).getMinimizeWidth() + 15;
                }
            }
        }
        }
        return minimizeLeft;
    }

    public void setMinimizeTopPos(int minimizeTop) {
        minimizeTopSet=true;
        this.minimizeTop = minimizeTop;
    }

    public int getMinimizeTopPos() {
        if (!minimizeTopSet) {
            UI ui = this.getUI();
            if (ui != null) {
                minimizeTopSet=true;
                minimizeTop = ui.getPage().getBrowserWindowHeight() - 50;
            }
        }
        return minimizeTop;
    }
}
