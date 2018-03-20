package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.Actor;
import de.mhus.cherry.reactive.model.activity.APool;

public class EverybodyActor implements Actor {

	@Override
	public boolean hasAccess(String user) {
		return true;
	}

}
