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

import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.Upload;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;

/** Represents a file in the file list of an {@link Upload} component. */
@SuppressWarnings("serial")
public final class FileInfo implements Serializable {

  private final String name;

  @Getter(AccessLevel.PACKAGE)
  private Upload upload;

  private Boolean complete;
  private Boolean indeterminate;
  private String errorMessage;
  private Integer progress;
  private String status;

  /**
   * Constructs a {@code FileInfo} object with the specified upload and file name.
   *
   * @param upload the {@code Upload} instance representing the file upload source
   * @param name the name of the file being uploaded
   */
  public FileInfo(Upload upload, String name) {
    this.upload = upload;
    this.name = name;
  }

  /**
   * Constructs a {@code FileInfo} object from the specified finished upload event.
   *
   * @param ev the {@code FinishedEvent} representing the succeeded or failed event
   */
  public FileInfo(FinishedEvent ev) {
    upload = ev.getSource();
    name = ev.getFileName();
  }

  /** Updates this file in the upload component. */
  public void update() {
    update(false);
  }

  /** Adds a new file to the upload component. */
  public void create() {
    update(true);
  }

  private void update(boolean createIfNotExists) {
    upload.getElement().executeJs(
        """
            var d = this;
            var i = d.files.findIndex(f=>f.name==$0.name);
            if (i<0) {
               if ($1) d.files = [... d.files, $0]; else return;
            } else {
              if (d.files.some((e,j)=>e.name==$0.name && j>i)) d.files=d.files.filter((e,j)=>e.name!=$0.name || j<=i);
              delete $0.name;
              d.files[i] = Object.assign(d.files[i], $0);
            }
            d.files = Array.from(d.files);
            """,
        toJson(), createIfNotExists);
  }

  private JsonObject toJson() {
    JsonObject json = Json.createObject();
    json.put("name", name);
    if (complete != null) {
      json.put("complete", complete);
    }
    if (indeterminate != null) {
      json.put("indeterminate", indeterminate);
    }
    if (errorMessage != null) {
      json.put("error", errorMessage);
    }
    if (progress != null) {
      json.put("progress", progress);
    }
    if (status != null) {
      json.put("status", status);
    }
    return json;
  }

  /**
   * True if uploading is completed, false otherwise.
   *
   * @return This instance for method chaining
   */
  public FileInfo complete(Boolean complete) {
    this.complete = complete;
    return this;
  }

  /**
   * Configure the upload in complete state (i.e. uploading is completed)
   *
   * @return This instance for method chaining
   */
  public FileInfo complete() {
    complete = true;
    return this;
  }

  /**
   * Configure the upload in indeterminate state (i.e. the remaining time is unknown)
   *
   * @return This instance for method chaining
   */
  public FileInfo indeterminate() {
    return indeterminate(true);
  }

  /**
   * True if the remaining time is unknown, false otherwise.
   *
   * @return This instance for method chaining
   */
  public FileInfo indeterminate(Boolean indeterminate) {
    this.indeterminate = indeterminate;
    return this;
  }

  /**
   * Error message returned by the server, if any.
   *
   * @return This instance for method chaining
   */
  public FileInfo errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Number representing the uploading progress.
   *
   * @return This instance for method chaining
   */
  public FileInfo progress(Integer progress) {
    this.progress = progress;
    return this;
  }

  /**
   * Uploading status message.
   *
   * @return This instance for method chaining
   */
  public FileInfo status(String status) {
    this.status = status;
    return this;
  }

  /** Returns the name of the uploaded file. */
  public String getName() {
    return name;
  }

  /** Returns {@code true} if uploading is completed, false otherwise. */
  public Boolean getComplete() {
    return complete;
  }

  /** Returns {@code true} if the remaining progress is unknown, false otherwise. */
  public Boolean getIndeterminate() {
    return indeterminate;
  }

  /** Returns the error message returned by the server. */
  public String getErrorMessage() {
    return errorMessage;
  }

  /** Returns a number between 0 and 100, representing the uploading progress. */
  public Integer getProgress() {
    return progress;
  }

  /** Returns the uploading status. */
  public String getStatus() {
    return status;
  }

}
