package edu.drexel.psal.anonymouth.gooie;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 * Big thanks to Rob Camick for his BoundsPopupMenuListener class, which provided the basis for the
 * modified popup below (with some modifications).
 */

public class ComboBox<E> extends JComboBox implements PopupMenuListener {

	private static final long serialVersionUID = 1L;
	protected Vector disabled;
	protected Vector classifiersVector;
	protected List<ImageIcon> folderIcons;
	protected CellRenderer cr;
	protected GUIMain main;
	
	private boolean scrollBarRequired;
	private boolean popupWider;
	private int maximumWidth;
	private int maximumHeight;
	private boolean popupAbove;
	private JScrollPane scrollPane;

	public ComboBox(Vector<E> items, Vector disabled, List<ImageIcon> folderIcons, GUIMain main) {
		super(items);
		this.disabled = disabled;
		this.folderIcons = folderIcons;
		cr = this.new CellRenderer();
		this.setRenderer(cr);
		this.main = main;
		
		scrollBarRequired = false;
		popupWider = true;
		popupAbove = true;
		maximumWidth = -1;
		maximumHeight = 247;
		
		this.addPopupMenuListener(this);
		this.setSelectedItem("SMO");
	}
	
	@Override
	public void setSelectedItem(Object item) {
		if (!disabled.contains(item)) {
			super.setSelectedItem(item);
		}
	}

	class CellRenderer extends JLabel implements ListCellRenderer<Object> {
		
		public CellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList<?> list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			int selectedIndex = main.classifiersVector.indexOf(value);

			this.setIconTextGap(5);
			setIcon(folderIcons.get(selectedIndex));
			
			if (disabled.contains((String)main.classifiersVector.get(selectedIndex)))
				setText((String)main.classifiersVector.get(selectedIndex));
			else
				setText("             " + (String)main.classifiersVector.get(selectedIndex));

			Color background;
			Color foreground;

			UIDefaults defaults = javax.swing.UIManager.getDefaults();
			if (isSelected) {
				if (disabled.contains(main.classifiersVector.get(list.getSelectedIndex()))) {
					background = defaults.getColor("List.background");
					foreground = defaults.getColor("List.foreground");
					isSelected = false;
					cellHasFocus = false;
				} else {
					background = defaults.getColor("List.selectionBackground");
					foreground = defaults.getColor("List.selectionForeground");
				}
			} else {
				background = defaults.getColor("List.background");
				foreground = defaults.getColor("List.foreground");
			};

			setBackground(background);
			setForeground(foreground);

			return this;
		}
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		JComboBox comboBox = (JComboBox)e.getSource();
		
		if (comboBox.getItemCount() == 0) return;

		final Object child = comboBox.getAccessibleContext().getAccessibleChild(0);

		if (child instanceof BasicComboPopup)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customizePopup((BasicComboPopup)child);
				}
			});
		}
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (scrollPane != null)
		{
			scrollPane.setHorizontalScrollBar( null );
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {}
	
	/**
	 *  Return the maximum width of the popup.
	 *
	 *  @return the maximumWidth value
	 */
	public int getMaximumWidth()
	{
		return maximumWidth;
	}

	/**
	 *  Set the maximum width for the popup. This value is only used when
	 *  setPopupWider( true ) has been specified. A value of -1 indicates
	 *  that there is no maximum.
	 *
	 *  @param maximumWidth  the maximum width of the popup
	 */
	public void setMaximumWidth(int maximumWidth)
	{
		this.maximumWidth = maximumWidth;
	}

	/**
	 *  Determine if the popup should be displayed above the combo box.
	 *
	 *  @return the popupAbove value
	 */
	public boolean isPopupAbove()
	{
		return popupAbove;
	}

	/**
	 *  Change the location of the popup relative to the combo box.
	 *
	 *  @param popupAbove  true display popup above the combo box,
	 *                     false display popup below the combo box.
	 */
	public void setPopupAbove(boolean popupAbove)
	{
		this.popupAbove = popupAbove;
	}

	/**
	 *  Determine if the popup might be displayed wider than the combo box
	 *
	 *  @return the popupWider value
	 */
	public boolean isPopupWider()
	{
		return popupWider;
	}

	/**
	 *  Change the width of the popup to be the greater of the width of the
	 *  combo box or the preferred width of the popup. Normally the popup width
	 *  is always the same size as the combo box width.
	 *
	 *  @param popupWider  true adjust the width as required.
	 */
	public void setPopupWider(boolean popupWider)
	{
		this.popupWider = popupWider;
	}

	/**
	 *  Determine if the horizontal scroll bar might be required for the popup
	 *
	 *  @return the scrollBarRequired value
	 */
	public boolean isScrollBarRequired()
	{
		return scrollBarRequired;
	}

	/**
	 *  For some reason the default implementation of the popup removes the
	 *  horizontal scrollBar from the popup scroll pane which can result in
	 *  the truncation of the rendered items in the popop. Adding a scrollBar
	 *  back to the scrollPane will allow horizontal scrolling if necessary.
	 *
	 *  @param scrollBarRequired  true add horizontal scrollBar to scrollPane
	 *                            false remove the horizontal scrollBar
	 */
	public void setScrollBarRequired(boolean scrollBarRequired)
	{
		this.scrollBarRequired = scrollBarRequired;
	}
	
	protected void customizePopup(BasicComboPopup popup)
	{
		scrollPane = getScrollPane(popup);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		if (popupWider) {
			popupAdjust( popup );
		}

		checkHorizontalScrollBar( popup );

		//  For some reason in JDK7 the popup will not display at its preferred
		//  width unless its location has been changed from its default
		//  (ie. for normal "pop down" shift the popup and reset)

		Component comboBox = popup.getInvoker();
		Point location = comboBox.getLocationOnScreen();

		if (popupAbove)
		{
			int height = popup.getPreferredSize().height;
			popup.setLocation(location.x + 5, location.y - 223);
		}
		else
		{
			int height = comboBox.getPreferredSize().height;
			popup.setLocation(location.x, location.y + height - 1);
			popup.setLocation(location.x, location.y + height);
		}
	}

	/*
	 *  Adjust the width of the scrollpane used by the popup
	 */
	protected void popupAdjust(BasicComboPopup popup)
	{
		JList list = popup.getList();

		//  Determine the maximimum width to use:
		//  a) determine the popup preferred width
		//  b) limit width to the maximum if specified
		//  c) ensure width is not less than the scroll pane width

		int popupWidth = list.getPreferredSize().width
					   + 5  // make sure horizontal scrollbar doesn't appear
					   + getScrollBarWidth(popup, scrollPane);
		int popupHeight = list.getPreferredSize().height
					   + 5
					   + getScrollBarHeight(popup, scrollPane);

		if (maximumWidth != -1) {
			popupWidth = Math.min(popupWidth, maximumWidth);
		}
		if (maximumHeight != -1) {
			popupHeight = Math.min(popupHeight, maximumHeight);
		}

		Dimension scrollPaneSize = scrollPane.getPreferredSize();
		popupWidth = Math.max(popupWidth, scrollPaneSize.width);
		popupHeight = Math.max(popupHeight, scrollPaneSize.height);

		//  Adjust the width
		scrollPaneSize.width = popupWidth;
		scrollPaneSize.height = popupHeight;
		scrollPane.setPreferredSize(scrollPaneSize);
		scrollPane.setMaximumSize(scrollPaneSize);
	}

	/*
	 *  This method is called every time:
	 *  - to make sure the viewport is returned to its default position
	 *  - to remove the horizontal scrollbar when it is not wanted
	 */
	private void checkHorizontalScrollBar(BasicComboPopup popup)
	{
		//  Reset the viewport to the left

		JViewport viewport = scrollPane.getViewport();
		Point p = viewport.getViewPosition();
		p.x = 0;
		viewport.setViewPosition( p );

		//  Remove the scrollbar so it is never painted

		if (! scrollBarRequired)
		{
			scrollPane.setHorizontalScrollBar( null );
			return;
		}

		//	Make sure a horizontal scrollbar exists in the scrollpane

		JScrollBar horizontal = scrollPane.getHorizontalScrollBar();

		if (horizontal == null)
		{
			horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
			scrollPane.setHorizontalScrollBar( horizontal );
			scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		}

		//	Potentially increase height of scroll pane to display the scrollbar

		if (horizontalScrollBarWillBeVisible(popup, scrollPane))
		{
			Dimension scrollPaneSize = scrollPane.getPreferredSize();
			scrollPaneSize.height += horizontal.getPreferredSize().height;
			scrollPane.setPreferredSize(scrollPaneSize);
			scrollPane.setMaximumSize(scrollPaneSize);
			scrollPane.revalidate();
		}
	}

	/*
	 *  Get the scroll pane used by the popup so its bounds can be adjusted
	 */
	protected JScrollPane getScrollPane(BasicComboPopup popup)
	{
		JList list = popup.getList();
		Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

		return (JScrollPane)c;
	}

	/*
	 *  I can't find any property on the scrollBar to determine if it will be
	 *  displayed or not so use brute force to determine this.
	 */
	protected int getScrollBarWidth(BasicComboPopup popup, JScrollPane scrollPane)
	{
		int scrollBarWidth = 0;
		JComboBox comboBox = (JComboBox)popup.getInvoker();

		if (comboBox.getItemCount() > comboBox.getMaximumRowCount())
		{
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			scrollBarWidth = vertical.getPreferredSize().width;
		}

		return scrollBarWidth;
	}
	
	protected int getScrollBarHeight(BasicComboPopup popup, JScrollPane scrollPane) {
		int scrollBarHeight = 0;
		JComboBox comboBox = (JComboBox)popup.getInvoker();
		
		if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
			JScrollBar horizontal = scrollPane.getVerticalScrollBar();
			scrollBarHeight = horizontal.getPreferredSize().height;
		}
		
		return scrollBarHeight;
	}

	/*
	 *  I can't find any property on the scrollBar to determine if it will be
	 *  displayed or not so use brute force to determine this.
	 */
	protected boolean horizontalScrollBarWillBeVisible(BasicComboPopup popup, JScrollPane scrollPane)
	{
		JList list = popup.getList();
		int scrollBarWidth = getScrollBarWidth(popup, scrollPane);
		int popupWidth = list.getPreferredSize().width + scrollBarWidth;

		return popupWidth > scrollPane.getPreferredSize().width;
	}
}
