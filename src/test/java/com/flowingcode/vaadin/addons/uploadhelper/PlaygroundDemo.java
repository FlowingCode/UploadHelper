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
package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Data;
import lombok.experimental.Accessors;

@PageTitle("Playground")
@SuppressWarnings("serial")
@Route(value = "upload-helper/demo", layout = UploadHelperDemoView.class)
public class PlaygroundDemo extends Div {

  private String fileName;
  private Upload upload;

  @Data
  @Accessors(fluent = true)
  private static class Bean {
    boolean complete;
    boolean indeterminate;
    int progress;
    String status;
    String errorMessage;
  }

  public PlaygroundDemo() {
    add(upload = new NullUpload());
    new FileInfo(upload, fileName = "test").create();

    upload.addSucceededListener(ev -> {
      fileName = ev.getFileName();
      new FileInfo(ev).indeterminate().status("Processing...").update();
    });

    Binder<Bean> binder = new Binder<>();

    FormLayout formLayout = new FormLayout();
    formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    add(formLayout);

    Checkbox indeterminate = new Checkbox();
    Checkbox complete = new Checkbox();
    TextField status = new TextField();
    TextField errorMessage = new TextField();

    IntegerField progress = new IntegerField();
    progress.setStepButtonsVisible(true);

    formLayout.addFormItem(indeterminate, "Indeterminate");
    formLayout.addFormItem(complete, "Complete");
    formLayout.addFormItem(status, "Status");
    formLayout.addFormItem(errorMessage, "Error Message");
    formLayout.addFormItem(progress, "Progress");

    binder.forField(indeterminate).bind(Bean::indeterminate, Bean::indeterminate);
    binder.forField(complete).bind(Bean::complete, Bean::complete);
    binder.forField(status).bind(Bean::status, Bean::status);
    binder.forField(errorMessage).bind(Bean::errorMessage, Bean::errorMessage);
    binder.forField(progress).bind(Bean::progress, Bean::progress);

    binder.setBean(new Bean());

    binder.addValueChangeListener(ev -> {
      Bean bean = binder.getBean();
      var file = new FileInfo(upload, fileName);
      file.indeterminate(bean.indeterminate);
      file.complete(bean.complete);
      file.status(bean.status);
      file.errorMessage(bean.errorMessage);
      file.progress(bean.progress);
      file.update();
    });

    indeterminate.setValue(true);
  }


}
