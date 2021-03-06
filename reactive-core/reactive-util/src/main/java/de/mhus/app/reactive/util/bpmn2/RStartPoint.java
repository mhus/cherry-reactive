/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.util.bpmn2;

import de.mhus.app.reactive.model.activity.AStartPoint;
import de.mhus.app.reactive.model.annotations.Output;
import de.mhus.app.reactive.model.engine.EElement;
import de.mhus.app.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.app.reactive.util.activity.RActivity;

/**
 * A start point.
 *
 * @author mikehummel
 * @param <P>
 */
public class RStartPoint<P extends RPool<?>> extends RActivity<P> implements AStartPoint<P> {

    @Override
    public void doExecuteActivity() throws Exception {
        EElement eNode = getContext().getENode();
        for (Output output : eNode.getActivityDescription().outputs()) {
            try {
                getContext().createActivity(output.activity());
            } catch (Throwable t) {
                log().w(output, t);
            }
        }
        getContext().getPNode().setState(STATE_NODE.CLOSED);
    }
}
