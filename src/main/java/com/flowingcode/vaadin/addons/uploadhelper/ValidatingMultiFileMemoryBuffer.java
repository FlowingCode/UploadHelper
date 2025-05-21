package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.function.SerializableConsumer;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class ValidatingMultiFileMemoryBuffer extends MultiFileMemoryBuffer
    implements ValidatingReceiver {

  private final ValidationDelegate delegate = new ValidationDelegate();

  @Override
  public void setRejectionListener(SerializableConsumer<FileData> rejectionListener) {
    delegate.setRejectionListener(rejectionListener);
  }

  @Override
  public void setAcceptedMimeTypes(String... acceptedMimeTypes) {
    delegate.setAcceptedMimeTypes(acceptedMimeTypes);
  }

  @Override
  public OutputStream receiveUpload(String fileName, String mimeType) {
    OutputStream out = super.receiveUpload(fileName, mimeType);
    return delegate.receiveUpload(fileName, mimeType, out, super::getFileData);
  }

  @Override
  public FileData getFileData(String fileName) {
    return delegate.getFileData(fileName, super::getFileData);
  }

  @Override
  public InputStream getInputStream(String fileName) {
    return delegate.getInputStream(fileName, super::getInputStream);
  }

}
