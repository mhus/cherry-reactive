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
package de.mhus.app.reactive.model.activity;

import de.mhus.app.reactive.model.util.IndexValuesProvider;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.FormControl;
import de.mhus.lib.form.IFormInformation;

/**
 * Interface for user handled tasks. Providing from information.
 *
 * @author mikehummel
 * @param <P>
 */
public interface AUserTask<P extends APool<?>>
        extends ATask<P>, IndexValuesProvider, IFormInformation {

    IProperties getFormValues() throws MException;

    void doSubmit(IProperties values) throws MException;

    MProperties doAction(String action, IProperties values);

    @Override
    Class<? extends FormControl> getFormControl();
}
