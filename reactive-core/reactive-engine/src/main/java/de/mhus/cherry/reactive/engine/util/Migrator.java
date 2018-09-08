package de.mhus.cherry.reactive.engine.util;

import java.io.IOException;
import java.util.UUID;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PCaseInfo;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.PNodeInfo;
import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.pojo.DefaultFilter;
import de.mhus.lib.core.pojo.PojoModel;
import de.mhus.lib.core.pojo.PojoParser;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.Version;
import de.mhus.lib.core.util.VersionRange;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class Migrator {

	private String process;
	private String pool;
	private String activity;
	private String[] ids;
	private Engine engine;
	private boolean test;
	private Monitor monitor;
	private String[] caseRules;
	private String[] nodeRules;

	private PojoModel nodeModel = new PojoParser().parse(PNode.class,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();
	private PojoModel caseModel = new PojoParser().parse(PCase.class,"_",null).filter(new DefaultFilter(true, false, false, false, true) ).getModel();
	private VersionRange version;
	private MUri uri;

	public Migrator(Monitor monitor) {
		this.monitor = monitor;
	}

	public void setSelectedIds(String[] ids) {
		this.ids = ids;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public void suspend() throws IOException, MException {
		monitor.setSteps(0);
		for (PCaseInfo info : engine.storageGetCases(null)) {
			if (info.getState() != STATE_CASE.SUSPENDED && info.getState() != STATE_CASE.CLOSED && filter(info)) {
				monitor.println("*** Suspend " + info);
				if (!test) {
					monitor.incrementStep();
					engine.suspendCase(info.getId());
					engine.prepareMigrateCase(info.getId());
				}
			}
		}
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public void migrate() throws NotFoundException, IOException {
		
		if (caseRules == null && nodeRules == null)
			return;
		
		monitor.setSteps(0);
		for (PCaseInfo caseInfo : engine.storageGetCases(null)) {
			if (filter(caseInfo)) {
				
				if (caseRules != null) {
					PCase caze = engine.getCase(caseInfo.getId());
					if (caze.getState() == STATE_CASE.SUSPENDED || caze.getState() == STATE_CASE.CLOSED) {
						monitor.incrementStep();
						monitor.println(">>> Migrate " + caseInfo);
						if (!test) {
							migrateCase(caze);
							engine.savePCase(caze, null, false);
						} else {
							System.out.println(caze);
							System.out.println(caze.getParameters());
						}
					} else
					if (test)
						monitor.println("--- Incorrect state " + caseInfo);
				}
				if (nodeRules != null) {
					for (PNodeInfo nodeInfo : engine.storageGetFlowNodes(caseInfo.getId(), null)) {
						if (filter(nodeInfo)) {
							PNode node = engine.getFlowNode(nodeInfo.getId());
							if (node.getState() == STATE_NODE.SUSPENDED || node.getState() == STATE_NODE.CLOSED) {
								monitor.println(">>> Migrate " + nodeInfo);
								if (!test) {
									migrateNode(node);
									engine.saveFlowNode(node);
								} else {
									monitor.println(node);
									monitor.println(node.getParameters());
								}
							} else
								if (test)
									monitor.println("--- Incorrect state " + nodeInfo);
						}
					}
				}
			}
		}
	}

	public void resume() throws IOException, MException {
		monitor.setSteps(0);
		for (PCaseInfo info : engine.storageGetCases(null)) {
			if (info.getState() == STATE_CASE.SUSPENDED && filter(info)) {
				monitor.incrementStep();
				monitor.println("*** Resume " + info);
				if (!test)
					engine.resumeCase(info.getId());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void migrateNode(PNode node) {
		
		for (String rule : nodeRules) {
			try {
				if (MString.isIndex(rule, ':')) {
					String action = MString.beforeIndex(rule, ':');
					rule = MString.afterIndex(rule, ':');
					String k = null;
					String v = null;
					if (MString.isIndex(rule, '=')) {
						k = MString.beforeIndex(rule, '=');
						v = MString.afterIndex(rule, '=');
					}
					switch (action) {
					case "name":
						nodeModel.getAttribute("name").set(node, rule);
						break;
					case "canonical":
						nodeModel.getAttribute("canonicalName").set(node, rule);
						break;
					case "type":
						nodeModel.getAttribute("type").set(node, TYPE_NODE.valueOf(rule));
						break;
					case "actor":
						nodeModel.getAttribute("actor").set(node, rule);
						break;
					case "status":
						node.setSuspendedState(STATE_NODE.valueOf(rule));
						break;
					case "":
					case "string":
						node.getParameters().put(k, v);
						break;
					case "date":
						node.getParameters().put(k, MCast.toDate(v, null));
						break;
					case "long":
						node.getParameters().put(k, MCast.tolong(v, 0));
						break;
					case "int":
					case "integer":
						node.getParameters().put(k, MCast.toint(v, 0));
						break;
					case "bool":
					case "boolean":
						node.getParameters().put(k, MCast.toboolean(v, false));
						break;
					case "uuid":
						node.getParameters().put(k, UUID.fromString(v));
						break;
					case "double":
						node.getParameters().put(k, MCast.todouble(v, 0));
						break;
					case "rm":
						node.getParameters().remove(rule);
						break;
					default:
						monitor.println("*** Unknown action " + action);
					}
				}
			} catch (Throwable t) {
				monitor.println("*** Rule: " + rule);
				monitor.println(t);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void migrateCase(PCase caze) {
		for (String rule : caseRules) {
			try {
				if (MString.isIndex(rule, ':')) {
					String action = MString.beforeIndex(rule, ':');
					rule = MString.afterIndex(rule, ':');
					String k = null;
					String v = null;
					if (MString.isIndex(rule, '=')) {
						k = MString.beforeIndex(rule, '=');
						v = MString.afterIndex(rule, '=');
					}
					switch (action) {
					case "name":
						caseModel.getAttribute("name").set(caze, rule);
						break;
					case "canonical":
						caseModel.getAttribute("canonicalName").set(caze, rule);
						break;
					case "milestone":
						caze.setMilestone(rule);
						break;
					case "closeCode":
						caseModel.getAttribute("closecode").set(caze,MCast.toint(rule, 0));
						break;
					case "closeMessage":
						caseModel.getAttribute("closemessage").set(caze,rule);
						break;
					case "status":
						caze.setState(STATE_CASE.valueOf(rule));
						break;
					case "":
					case "string":
						caze.getParameters().put(k, v);
						break;
					case "date":
						caze.getParameters().put(k, MCast.toDate(v, null));
						break;
					case "long":
						caze.getParameters().put(k, MCast.tolong(v, 0));
						break;
					case "int":
					case "integer":
						caze.getParameters().put(k, MCast.toint(v, 0));
						break;
					case "bool":
					case "boolean":
						caze.getParameters().put(k, MCast.toboolean(v, false));
						break;
					case "double":
						caze.getParameters().put(k, MCast.todouble(v, 0));
						break;
					case "uuid":
						caze.getParameters().put(k, UUID.fromString(v));
						break;
					case "rm":
						caze.getParameters().remove(rule);
						break;
					default:
						monitor.println("*** Unknown action " + action);
					}
				}
			} catch (Throwable t) {
				monitor.println("*** Rule: " + rule);
				monitor.println(t);
			}
		}
	}

	private boolean filter(PNodeInfo info) {
		
		boolean filtered = false;
		if (activity != null) {
			filtered = true;
			if (!info.getCanonicalName().equals(activity))
				return false;
		}
		
		if (ids != null && nodeRules != null) {
			filtered = true;
			if (!MCollection.contains(ids, info.getId().toString())) return false;
		}

		if (!filtered)
			return false;

		return true;
	}

	private boolean filter(PCaseInfo info) {
		boolean filtered = false;
		if (uri != null) {
			filtered = true;
			MUri u = MUri.toUri(info.getUri());
			String p = u.getLocation();
			String v = MString.afterIndex(p, ':');
			p = MString.beforeIndex(p, ':');
			if (!p.equals(process)) return false;
			if (version != null && !version.includes(new Version(v))) return false;
			if (MString.isSet(pool) && !pool.equals(p)) return false;
		}
		
		if (ids != null && caseRules != null) {
			filtered = true;
			if (!MCollection.contains(ids, info.getId().toString())) return false;
		}
		
		if (!filtered)
			return false;
		
		return true;
	}

	public void setCaseRules(String[] caseRules) {
		this.caseRules = caseRules;
	}

	public void setNodeRules(String[] nodeRules) {
		this.nodeRules = nodeRules;
	}

	public void setUri(MUri uri) {
		this.uri = uri;
		process = uri.getLocation();
		version = null;
		activity = null;
		if (MString.isIndex(process, ':')) {
			version = new VersionRange(MString.afterIndex(process, ':'));
			process = MString.beforeIndex(process, ':');
		}
		pool = uri.getPath();
		if (MString.isIndex(pool, '/')) {
			activity = MString.afterIndex(pool, '/');
			pool = MString.beforeIndex(pool, '/');
		}
	}

}