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
package de.mhus.app.reactive.examples.simple1.sub;

import de.mhus.app.reactive.examples.simple1.S1Pool;
import de.mhus.app.reactive.examples.simple1.S1TheEnd;
import de.mhus.app.reactive.model.annotations.ActivityDescription;
import de.mhus.app.reactive.model.annotations.Output;
import de.mhus.app.reactive.model.annotations.SubDescription;
import de.mhus.app.reactive.model.engine.ProcessContext;
import de.mhus.app.reactive.model.engine.RuntimeNode;
import de.mhus.app.reactive.util.activity.RSubStart;

@ActivityDescription(outputs = @Output(activity = S1TheEnd.class))
@SubDescription(start = S1StartSpock.class)
public class S1ExecuteSubStart extends RSubStart<S1Pool> {

    @Override
    protected void prepareNewRuntime(RuntimeNode runtime) {
        System.out.println(">>> prepareNewRuntime");
    }

    @Override
    protected String doExecuteAfterSub(ProcessContext<?> closingContext) {
        System.out.println("<<< doExecuteAfterSub");
        return null;
    }
}
