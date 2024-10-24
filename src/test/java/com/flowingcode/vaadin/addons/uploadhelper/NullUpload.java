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

import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import java.io.OutputStream;
import org.apache.commons.io.output.NullOutputStream;

// this is just a helper class that discards the uploaded file
@SuppressWarnings("serial")
class NullUpload extends Upload {

  public NullUpload(MultiFileReceiver receiver) {
    super(new MultiFileMemoryBuffer() {
      @Override
      public OutputStream receiveUpload(String fileName, String MIMEType) {
        super.receiveUpload(fileName, MIMEType);
        return NullOutputStream.INSTANCE;
      }
    });
  }

  public NullUpload() {
    super(new MemoryBuffer() {
      @Override
      public OutputStream receiveUpload(String fileName, String MIMEType) {
        super.receiveUpload(fileName, MIMEType);
        return NullOutputStream.INSTANCE;
      }
    });
  }

}
