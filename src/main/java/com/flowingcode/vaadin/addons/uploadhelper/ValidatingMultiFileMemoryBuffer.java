package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.function.SerializableConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.Setter;
import org.apache.tika.mime.MediaType;

public class ValidatingMultiFileMemoryBuffer extends MultiFileMemoryBuffer
    implements ValidatingReceiver {

  private Map<String, MediaType> detectedMimeTypes = new HashMap<>();

  private Set<String> rejectedFiles = new HashSet<>();

  @Setter
  private MediaType[] acceptedMimeTypes;

  @Setter
  private SerializableConsumer<FileData> rejectionListener;

  @Override
  public OutputStream receiveUpload(String fileName, String mimeType) {
    if (acceptedMimeTypes == null) {
      throw new IllegalArgumentException("No acceptedMimeTypes configured");
    }
    detectedMimeTypes.remove(fileName);
    AtomicBoolean rejected = new AtomicBoolean();
    return new ValidatingOutputStream(super.receiveUpload(fileName, mimeType), 5, header -> {
      MediaType detectedMimeType = MediaTypeUtils.detect(header);
      detectedMimeTypes.put(fileName, detectedMimeType);
      return Stream.of(acceptedMimeTypes)
          .anyMatch(type -> MediaTypeUtils.isInstanceOf(detectedMimeType, type));
    }, () -> {
      rejected.set(true);
    }) {
      @Override
      public void close() throws IOException {
        super.close();
        if (rejected.get()) {
          rejectedFiles.add(fileName);
          if (rejectionListener != null) {
            rejectionListener.accept(getFileData(fileName));
          }
        }
      }
    };
  }

  @Override
  public FileData getFileData(String fileName) {
    FileData file = super.getFileData(fileName);
    if (file != null) {
      String detectedMimeType = detectedMimeTypes.get(fileName).toString();
      if (!detectedMimeType.equals(file.getMimeType())) {
        file = new FileData(file.getFileName(), detectedMimeType, file.getOutputBuffer());
      }
    }
    return file;
  }

  @Override
  public InputStream getInputStream(String fileName) {
    InputStream in = super.getInputStream(fileName);
    if (in == null && detectedMimeTypes.containsKey(fileName)) {
      throw new IllegalStateException("Rejected MIME type: " + detectedMimeTypes.get(fileName));
    }
    return in;
  }

}
