package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.Actor;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class RSwimlane<P extends APool<?>> implements ASwimlane<P>, ContextRecipient {

	private Class<? extends Actor> actor;

	@Override
	public void setContext(ProcessContext<?> context) {
		actor = (Class<? extends Actor>) context.getEPool().getPoolDescription().actorDefault();
	}

	@Override
	public Class<? extends Actor> getActor() {
		return actor;
	}

}
