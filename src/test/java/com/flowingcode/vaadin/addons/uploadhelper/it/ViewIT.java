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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.flowingcode.vaadin.testbench.rpc.HasRpcSupport;
import com.vaadin.flow.component.progressbar.testbench.ProgressBarElement;
import com.vaadin.testbench.TestBenchElement;
import org.junit.Before;
import org.junit.Test;

public class ViewIT extends AbstractViewTest implements HasRpcSupport {

  IntegrationCallables $server = createCallableProxy(IntegrationCallables.class);

  public ViewIT() {
    super("it");
  }

  private TestBenchElement file() {
    return $("vaadin-upload-file").waitForFirst();
  }

  @Before
  public void before() {
    file();
  }

  @Test
  public void indeterminate() {
    $server.indeterminate();
    assertTrue("has attribute indeterminate", file().hasAttribute("indeterminate"));
  }

  @Test
  public void complete() {
    $server.complete();
    assertTrue("has attribute complete", file().hasAttribute("complete"));
  }

  @Test
  public void errorMessage() {
    $server.errorMessage("msg1");
    assertTrue("has attribute error", file().hasAttribute("error"));
    assertEquals("msg1",
        file().$("div").withAttribute("part", "error").first().getText());
  }

  @Test
  public void status() {
    $server.status("msg2");
    assertEquals("msg2",
        file().$("div").withAttribute("part", "status").first().getText());
  }

  @Test
  public void progress() {
    $server.progress(42);
    assertEquals(0.42, file().$(ProgressBarElement.class).first().getValue(), 0.001);
  }

}
