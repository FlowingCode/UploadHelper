package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.function.SerializableConsumer;

public interface ValidatingReceiver extends Receiver {

  void setRejectionListener(SerializableConsumer<FileData> rejectionListener);

  void setAcceptedMimeTypes(String... acceptedMimeTypes);

}
