package de.mhus.cherry.reactive.vaadin.widgets;

import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import de.mhus.cherry.reactive.model.ui.IEngine;
import de.mhus.cherry.reactive.model.ui.INode;

public class VNodeDetails extends Panel {

	private static final long serialVersionUID = 1L;
	private GridLayout grid;
	private Button bCancel;
	private int row;

	public VNodeDetails() {
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
	
	public void configure(IEngine engine, NodeItem item) {
		
		try {
			INode node = engine.getNode(item.getId().toString(), "*");
		
			setCaption(node.getCanonicalName());
			grid.removeAllComponents();
			grid.setRows(1);
			grid.setColumns(2);
			grid.setColumnExpandRatio(0, 0.1f);
			grid.setColumnExpandRatio(1, 0.9f);
			row = 0;
			
			addLine("State", node.getNodeState());
			addLine("Actor", node.getActor());
			addLine("Assigned",node.getAssigned());
			addLine("Created",node.getCreated());
			addLine("CustomerId",node.getCustomerId());
			addLine("CustomId",node.getCustomId());
			addLine("Id",node.getId());
			addLine("Modified",node.getModified());
			addLine("Priority",node.getPriority());
			addLine("Score",node.getScore());
			addLine("Type",node.getType());
			Map<String, String> prop = node.getProperties();
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