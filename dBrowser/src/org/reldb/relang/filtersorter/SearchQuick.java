package org.reldb.relang.filtersorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SearchQuick extends Composite implements Searcher {
	/*
	private StyledText findText;
	private boolean wholeWordSearch = false;
	private boolean caseSensitiveSearch = false;
	private boolean regexSearch = false;
	
	private FilterSorter filterSorter;
	*/

	public SearchQuick(FilterSorter filterSorter, Composite contentPanel) {
		super(contentPanel, SWT.NONE);
	/*	
		this.filterSorter = filterSorter;
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		findText = new StyledText(this, SWT.BORDER | SWT.SINGLE);
		findText.addListener(SWT.Traverse, event -> {
			if (event.detail == SWT.TRAVERSE_RETURN) {
				fireUpdate();
			}
		});
		findText.addListener(SWT.Modify, event -> {
			if (findText.getText().trim().length() > 0) {
				findText.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
				findText.setBackground(SWTResourceManager.getColor(255, 225, 225));
			}
		});
		findText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		
		ToolItem wholeWord = new ToolItem(toolBar, SWT.PUSH);
		wholeWord.addListener(SWT.Selection, e -> {
			wholeWordSearch = !wholeWordSearch;
			wholeWord.setText(wholeWordSearch ? "Whole word" : "Any match");
			layout();
			fireUpdateIfSearch();
		});
		wholeWord.setText("Any match");

		ToolItem caseSensitive = new ToolItem(toolBar, SWT.PUSH);
		caseSensitive.addListener(SWT.Selection, e -> {
			caseSensitiveSearch = !caseSensitiveSearch;
			caseSensitive.setText(caseSensitiveSearch ? "Case sensitive" : "Case insensitive");
			layout();
			fireUpdateIfSearch();
		});
		caseSensitive.setText("Case insensitive");
		
		ToolItem regex = new ToolItem(toolBar, SWT.CHECK);
		regex.addListener(SWT.Selection, e -> {
			regexSearch = regex.getSelection();
			wholeWord.setEnabled(!regexSearch);
			caseSensitive.setEnabled(!regexSearch);
			fireUpdateIfSearch();
		});
		regex.setText("Regex");
		
		ToolItem clear = new ToolItem(toolBar, SWT.PUSH);
		clear.addListener(SWT.Selection, e -> {
			if (findText.getText().trim().length() == 0)
				return;
			findText.setText("");
			fireUpdate();
		});
		clear.setText("Clear");
		
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		*/
	}
	
	public String getQuery() {
		/*
		String needle = findText.getText().trim();
		if (needle.length() == 0)
			return "";
		String regex;
		if (regexSearch)
			regex = needle;
		else {
			String delimiter = wholeWordSearch ? "\\b" : ".*";
			regex = delimiter + Pattern.quote(needle) + delimiter;
			if (!caseSensitiveSearch)
				regex = "(?i)" + regex;
		}
		// TODO - fix
//		return " WHERE SEARCH(TUP {*}, \"" + StringUtils.quote(regex) + "\")";
		 * 
		 */
		return null;
	}

	public String getState() {
		// return findText.getText();
		return "";
	}

	public void ok() {
		fireUpdateIfSearch();
	}

	/*
	private void fireUpdate() {
		findText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		findText.setBackground(null);
		filterSorter.refresh();
	}
	*/
	
	private void fireUpdateIfSearch() {
		/*
		if (findText.getText().trim().length() > 0)
			fireUpdate();
		*/
	}
	
}
