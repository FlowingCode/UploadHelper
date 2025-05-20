package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.function.SerializableConsumer;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;
import lombok.Setter;
import org.apache.tika.mime.MediaType;

public class ValidatingMemoryBuffer extends MemoryBuffer implements ValidatingReceiver {

  private MediaType detectedMimeType;

  @Setter
  private MediaType[] acceptedMimeTypes;

  @Setter
  private SerializableConsumer<FileData> rejectionListener;

  private boolean rejected;

  @Override
  public OutputStream receiveUpload(String fileName, String mimeType) {
    if (acceptedMimeTypes == null) {
      throw new IllegalArgumentException("No acceptedMimeTypes configured");
    }
    rejected = false;
    detectedMimeType = null;
    return new ValidatingOutputStream(super.receiveUpload(fileName, mimeType), 5, header -> {
      detectedMimeType = MediaTypeUtils.detect(header);
      return Stream.of(acceptedMimeTypes)
          .anyMatch(type -> MediaTypeUtils.isInstanceOf(detectedMimeType, type));
    }, () -> {
      rejected = true;
      rejectionListener.accept(getFileData());
    });
  }

  @Override
  public FileData getFileData() {
    FileData file = super.getFileData();
    if (file != null && !detectedMimeType.toString().equals(file.getMimeType())) {
      file = new FileData(file.getFileName(), detectedMimeType.toString(), file.getOutputBuffer());
    }
    return file;
  }

  @Override
  public InputStream getInputStream() {
    if (rejected) {
      throw new IllegalStateException("Rejected MIME type: " + detectedMimeType);
    }
    return super.getInputStream();
  }

}
