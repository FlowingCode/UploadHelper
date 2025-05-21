package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.function.SerializableConsumer;
import java.io.InputStream;
import java.io.OutputStream;

public class ValidatingFileBuffer extends FileBuffer implements ValidatingReceiver {

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
    return delegate.receiveUpload("", mimeType, out, _fileName -> getFileData());
  }

  @Override
  public FileData getFileData() {
    return delegate.getFileData("", _fileName -> getFileData());
  }

  @Override
  public InputStream getInputStream() {
    return delegate.getInputStream("", _fileName -> getInputStream());
  }

}
