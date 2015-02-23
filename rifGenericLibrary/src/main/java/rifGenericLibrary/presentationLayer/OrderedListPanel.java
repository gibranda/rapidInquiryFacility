
package rifGenericLibrary.presentationLayer;


import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;


/**
 *
 *
 * <hr>
 * The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
 * that rapidly addresses epidemiological and public health questions using 
 * routinely collected health and population data and generates standardised 
 * rates and relative risks for any given health outcome, for specified age 
 * and year ranges, for any given geographical area.
 *
 * <p>
 * Copyright 2014 Imperial College London, developed by the Small Area
 * Health Statistics Unit. The work of the Small Area Health Statistics Unit 
 * is funded by the Public Health England as part of the MRC-PHE Centre for 
 * Environment and Health. Funding for this project has also been received 
 * from the United States Centers for Disease Control and Prevention.  
 * </p>
 *
 * <pre> 
 * This file is part of the Rapid Inquiry Facility (RIF) project.
 * RIF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RIF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
 * to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
 * Boston, MA 02110-1301 USA
 * </pre>
 *
 * <hr>
 * Kevin Garwood
 * @author kgarwood
 * @version
 */

/*
 * Code Road Map:
 * --------------
 * Code is organised into the following sections.  Wherever possible, 
 * methods are classified based on an order of precedence described in 
 * parentheses (..).  For example, if you're trying to find a method 
 * 'getName(...)' that is both an interface method and an accessor 
 * method, the order tells you it should appear under interface.
 * 
 * Order of 
 * Precedence     Section
 * ==========     ======
 * (1)            Section Constants
 * (2)            Section Properties
 * (3)            Section Construction
 * (7)            Section Accessors and Mutators
 * (6)            Section Errors and Validation
 * (5)            Section Interfaces
 * (4)            Section Override
 *
 */


public final class OrderedListPanel {


// ==========================================
// Section Constants
// ==========================================

// ==========================================
// Section Properties
// ==========================================
	
	//Data
	/** The alphabetically sort items. */
	private boolean alphabeticallySortItems;
	/** The list items. */
	private Vector<String> listItems;
	
	//GUI Components
	/** The item from list name. */
	private HashMap<String, DisplayableListItemInterface> itemFromListName;	
	/** The panel. */
	private JPanel panel;
	/** The list. */
	private JList<String> list;	
	/** The list scroll pane. */
	private JScrollPane listScrollPane;
		
// ==========================================
// Section Construction
// ==========================================
    /**
     * Instantiates a new ordered list panel.
     *
     * @param listTitle the list title
     * @param listToolTipText the list tool tip text
     * @param userInterfaceFactory the user interface factory
     * @param allowMultipleItemSelection the allow multiple item selection
     */
	public OrderedListPanel(
    	String listTitle,
    	String listToolTipText,
		UserInterfaceFactory userInterfaceFactory,
		boolean allowMultipleItemSelection) {
	
		itemFromListName = new HashMap<String, DisplayableListItemInterface>();
		listItems = new Vector<String>();
	
		alphabeticallySortItems = true;
				
		panel = userInterfaceFactory.createPanel();
		GridBagConstraints panelGC = userInterfaceFactory.createGridBagConstraints();		
		
		JLabel listTitleLabel
			= userInterfaceFactory.createLabel(listTitle);
		if (listToolTipText != null) {
			listTitleLabel.setToolTipText(listToolTipText);
		}
		panel.add(listTitleLabel, panelGC);
		
		panelGC.gridy++;
		panelGC.fill = GridBagConstraints.BOTH;
		panelGC.weightx = 1.0;
		panelGC.weighty = 1.0;
		list = userInterfaceFactory.createList(listItems);
		userInterfaceFactory.setPlainFont(list);
		ListSelectionModel listSelectionModel
			= list.getSelectionModel();
		if (allowMultipleItemSelection == true) {
			listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		else {
			listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);			
		}
		
		listScrollPane
			= userInterfaceFactory.createScrollPane(list);
		panel.add(listScrollPane, panelGC);
		
    }

// ==========================================
// Section Accessors and Mutators
// ==========================================
    
    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
	public boolean isEmpty() {
		
    	return listItems.isEmpty();
    }
    
    /**
     * Gets the number of items.
     *
     * @return the number of items
     */
    public int getNumberOfItems() {
    	
    	return listItems.size();
    }
    
    /**
     * Sets the alphabetically sort items.
     *
     * @param alphabeticallySortItems the new alphabetically sort items
     */
    public void setAlphabeticallySortItems(
    	boolean alphabeticallySortItems) {
   
    	this.alphabeticallySortItems = alphabeticallySortItems;
    }
    
    /**
     * Checks if is alphabetically sorted.
     *
     * @return true, if is alphabetically sorted
     */
    public boolean isAlphabeticallySorted() {
    	
    	return alphabeticallySortItems;
    }
    
    /**
     * Clear list.
     */
    public void clearList() {
    	
    	itemFromListName.clear();
    	listItems.clear();
    }
    
    /**
     * Sets the enabled.
     *
     * @param isEnabled the new enabled
     */
    public void setEnabled(
    	boolean isEnabled) {
    	
    	list.setEnabled(isEnabled);
    	if (isEnabled == false) {
    		clearList();
    	}
    }
    
	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public JPanel getPanel() {
		
		return panel;
	}
	
	/**
	 * Adds the list selection listener.
	 *
	 * @param listSelectionListener the list selection listener
	 */
	public void addListSelectionListener(
		ListSelectionListener listSelectionListener) {
		
		list.addListSelectionListener(listSelectionListener);
	}
	
	/**
	 * Adds the list item.
	 *
	 * @param listItem the list item
	 */
	public void addListItem(
		DisplayableListItemInterface listItem) {
		
		itemFromListName.put(listItem.getDisplayName(), listItem);	
		listItems.add(listItem.getDisplayName());	
		
		if (alphabeticallySortItems == true) {
			Collections.sort(listItems);	
		}
	}
	
	/**
	 * Replace item.
	 *
	 * @param originalItem the original item
	 * @param revisedItem the revised item
	 */
	public void replaceItem(
			DisplayableListItemInterface originalItem,
			DisplayableListItemInterface revisedItem) {
		
		String displayName
			= originalItem.getDisplayName();
		
		itemFromListName.remove(displayName);		
		listItems.remove(displayName);	
		
		itemFromListName.put(revisedItem.getDisplayName(), revisedItem);
		listItems.add(revisedItem.getDisplayName());
		if (alphabeticallySortItems == true) {
			Collections.sort(listItems);	
		}
	}
	
	/**
	 * Delete selected list items.
	 */
	public void deleteSelectedListItems() {
		
		int[] currentlySelectedRows
			= list.getSelectedIndices();
		ArrayList<DisplayableListItemInterface> itemsToDelete
			= new ArrayList<DisplayableListItemInterface>();
		for (int i = 0; i < currentlySelectedRows.length; i++) {
			String displayNameToDelete
				= listItems.get(currentlySelectedRows[i]);
			itemsToDelete.add(itemFromListName.get(displayNameToDelete));
		}
		
		//now delete the items
		for (DisplayableListItemInterface itemToDelete : itemsToDelete) {
			String displayNameToDelete 
				= itemToDelete.getDisplayName();
			itemFromListName.remove(displayNameToDelete);
			listItems.remove(displayNameToDelete);
		}
		updateUI();
	}
	
	
	/**
	 * Gets the all items.
	 *
	 * @return the all items
	 */
	public ArrayList<DisplayableListItemInterface> getAllItems() {
		
		ArrayList<DisplayableListItemInterface> results
			= new ArrayList<DisplayableListItemInterface>();
		results.addAll(itemFromListName.values());
		return results;
	}
		
	/**
	 * Update ui.
	 */
	public void updateUI() {
		
		list.setListData(listItems);
		listScrollPane.revalidate();
		listScrollPane.repaint();
	}
	

	/**
	 * No items selected.
	 *
	 * @return true, if successful
	 */
	public boolean noItemsSelected() {
		
		int[] selectedIndices
			= list.getSelectedIndices();
		if (selectedIndices.length == 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the selected item.
	 *
	 * @return the selected item
	 */
	public DisplayableListItemInterface getSelectedItem() {
		
		String selectedDisplayName
			= (String) list.getSelectedValue();
		if (selectedDisplayName == null) {
			return null;
		}
		else {
			DisplayableListItemInterface selectedItem
				= itemFromListName.get(selectedDisplayName);
			return selectedItem;
		}
	}
	
	/**
	 * Gets the selected items.
	 *
	 * @return the selected items
	 */
	public ArrayList<DisplayableListItemInterface> getSelectedItems() {
		
		ArrayList<DisplayableListItemInterface> results
			= new ArrayList<DisplayableListItemInterface>();

		java.util.List<String> selectedDisplayNames
			= list.getSelectedValuesList();
		Iterator<String> iterator
			= selectedDisplayNames.iterator();
		while (iterator.hasNext() == true) {
			DisplayableListItemInterface selectedItem
				= itemFromListName.get(iterator.next());
			results.add(selectedItem);		
		}
		
		return results;
	}
	
	/**
	 * Select first item.
	 */
	public void selectFirstItem() {
		
		if (listItems.size() > 0) {
			list.setSelectedIndex(0);			
		}
	}
	
	/**
	 * Sets the selected item.
	 *
	 * @param index the new selected item
	 */
	public void setSelectedItem(
		int index) {
		
		list.setSelectedIndex(index);
	}
	
	/**
	 * Sets the selected item.
	 *
	 * @param selectableItem the new selected item
	 */
	public void setSelectedItem(
		DisplayableListItemInterface selectableItem) {
		
		list.setSelectedValue(selectableItem.getDisplayName(), true);
	}
	
// ==========================================
// Section Errors and Validation
// ==========================================

// ==========================================
// Section Interfaces
// ==========================================

// ==========================================
// Section Override
// ==========================================

}