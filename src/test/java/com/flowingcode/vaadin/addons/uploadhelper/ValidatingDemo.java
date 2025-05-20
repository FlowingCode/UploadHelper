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

import com.flowingcode.vaadin.addons.demo.DemoSource;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@DemoSource
@PageTitle("Content Validation")
@SuppressWarnings("serial")
@Route(value = "upload-helper/validate", layout = UploadHelperDemoView.class)
public class ValidatingDemo extends Div {

  public ValidatingDemo() {
    Upload upload = new Upload(new ValidatingMultiFileMemoryBuffer());
    // upload.setAcceptedFileTypes("application/pdf");
    // ((ValidatingReceiver) upload.getReceiver()).setAcceptedMimeTypes(upload);
    ((ValidatingReceiver) upload.getReceiver()).setAcceptedMimeTypes("application/pdf");
    ((ValidatingReceiver) upload.getReceiver()).setRejectionListener(file -> {
      UI.getCurrent().access(() -> {
        new FileInfo(upload, file.getFileName()).errorMessage(file.getMimeType() + " rejected")
            .update();
        upload.interruptUpload();
      });
    });
    add(upload);

    // #if vaadin eq 0
    upload.setMaxFiles(3);
    upload.setMaxFileSize(1024 * 1024);
    add(new Div(
        """
            TBD."""));
    // #endif

  }


}
