/*-
 * #%L
 * Upload Helper Add-on
 * %%
 * Copyright (C) 2022 - 2024 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.flowingcode.vaadin.addons.uploadhelper.it;

import com.flowingcode.vaadin.addons.uploadhelper.FileInfo;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

@Route("it")
public class IntegrationView extends Div implements IntegrationCallables {

  private Upload upload;

  public IntegrationView() {
    add(upload = new Upload(new MemoryBuffer()));
    file().create();
  }

  private FileInfo file() {
    return new FileInfo(upload, "test");
  }

  @Override
  @ClientCallable
  public void indeterminate() {
    file().indeterminate().update();
  }

  @Override
  @ClientCallable
  public void complete() {
    file().complete().update();
  }

  @Override
  @ClientCallable
  public void progress(int progress) {
    file().progress(progress).update();
  }

  @Override
  @ClientCallable
  public void status(String status) {
    file().status(status).update();
  }

  @Override
  @ClientCallable
  public void errorMessage(String errorMessage) {
    file().errorMessage(errorMessage).update();
  }

}
