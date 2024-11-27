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
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.OutputStream; // hide-source
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.output.NullOutputStream; // hide-source

@DemoSource
@PageTitle("FileInfo Demo")
@SuppressWarnings("serial")
@Route(value = "upload-helper/demo2", layout = UploadHelperDemoView.class)
public class FileInfoDemo extends Div {

  public FileInfoDemo() {
    // show-source Upload upload = new Upload(new MultiFileMemoryBuffer());

    // #if vaadin eq 0
    add(new Div(
        """
            Simulates progress updates, incrementing from 0% to 100% and updating the UI in real-time.
            After reaching 100%, the file is set to "Please wait..." with an indeterminate progress state to represent some final processing.
            After a 2-second delay, the system randomly determines if the processing succeeded or failed, updating the UI accordingly."""));

    Upload upload = new Upload(new MultiFileMemoryBuffer() {
      @Override
      public OutputStream receiveUpload(String fileName, String MIMEType) {
        super.receiveUpload(fileName, MIMEType);
        return NullOutputStream.INSTANCE;
      }
    });
    add(upload);
    // #endif

    Map<String,Boolean> processing = new ConcurrentHashMap<>();

    upload.addSucceededListener(ev -> {
      if (processing.put(ev.getFileName(), true) == null) {
        UI ui = ev.getSource().getUI().get();
        new Thread(() -> {
          AtomicInteger progress = new AtomicInteger();
          while (progress.get() <= 100) {
            int p = progress.getAndIncrement();
            ui.access(() -> new FileInfo(ev).status(String.format("Processing (%s%%)", p))
                .progress(p).update());
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

          ui.access(() -> new FileInfo(ev).indeterminate().status("Please wait...").update());

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          if (Math.random() < 0.5) {
            ui.access(() -> new FileInfo(ev).complete().status("").update());
          } else {
            ui.access(() -> new FileInfo(ev).errorMessage("Random failure").status("").update());
          }

          processing.remove(ev.getFileName());
        }).start();
      }
    });

  }


}
