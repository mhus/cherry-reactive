package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.mhus.cherry.reactive.model.ui.ICase;
import de.mhus.cherry.reactive.model.ui.IEngine;

public class VCaseDetails extends Panel {

	private static final long serialVersionUID = 1L;
	private GridLayout grid;
	private Button bCancel;
	private int row;

	public VCaseDetails() {
		grid = new GridLayout();
		setContent(grid);
        setSizeFull();
		grid.setWidth("100%");
		
		bCancel = new Button("Exit");
		bCancel.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				onCancel();
			}
			
		});
	}
	
	public void configure(IEngine engine, CaseItem item) {
		
		try {
			ICase caze = engine.getCase(item.getId().toString(), "*");
		
			setCaption(caze.getCanonicalName());
			grid.removeAllComponents();
			grid.setRows(1);
			grid.setColumns(2);
			grid.setColumnExpandRatio(0, 0.1f);
			grid.setColumnExpandRatio(1, 0.9f);
			row = 0;
			
			addLine("State", caze.getState());
			addLine("Created",caze.getCreated());
			addLine("CustomerId",caze.getCustomerId());
			addLine("CustomId",caze.getCustomId());
			addLine("Id",caze.getId());
			addLine("Modified",caze.getModified());
			addLine("Priority",caze.getPriority());
			addLine("Score",caze.getScore());
			Map<String, String> prop = caze.getProperties();
			if (prop != null && prop.size() > 0) {
				addTitle("Node:");
				for (Entry<String, String> entry : prop.entrySet())
					addLine(entry.getKey(), entry.getValue());
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		row++;
		grid.setRows(row);
		grid.addComponent(bCancel, 1, row-1);
	}

	private void addTitle(String text) {
		row++;
		grid.setRows(row);
		Label label = new Label();
		label.setCaptionAsHtml(true);
		label.setCaption("<b>" + text + "</b>");
		grid.addComponent(label, 0, row-1, 1, row-1);
	}

	private void addLine(String label, Object value) {
		row++;
		grid.setRows(row);
		grid.addComponent(new Label(label), 0, row-1);
		grid.addComponent(new Label(String.valueOf(value)), 1, row-1);
	}

	protected void onCancel() {
		
	}

}