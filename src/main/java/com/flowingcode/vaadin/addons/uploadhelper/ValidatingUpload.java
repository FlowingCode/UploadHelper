package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;

public class ValidatingUpload extends Upload {

  public ValidatingUpload(ValidatingReceiver receiver) {
    super(receiver);
    getReceiver().setRejectionListener(file -> {
      rejectionListeners.forEach(listener->listener.accept(file));
      // add the interruptUpload logic here or ensure the user's listener can call it.pload'
    });
  }

  @Override
  public ValidatingReceiver getReceiver() {
    return (ValidatingReceiver) super.getReceiver();
  }

  @Override
  public void setAcceptedFileTypes(String... acceptedFileTypes) {
    super.setAcceptedFileTypes(acceptedFileTypes);
    getReceiver().setAcceptedMimeTypes(acceptedFileTypes);
  }

  private List<SerializableConsumer<FileData>> rejectionListeners = new ArrayList<>();

  public Registration addRejectionListener(SerializableConsumer<FileData> listener) {
    rejectionListeners.add(listener);
    return () -> rejectionListeners.remove(listener);
  }

}

