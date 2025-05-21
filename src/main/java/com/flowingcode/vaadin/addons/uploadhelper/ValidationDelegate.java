package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.function.SerializableConsumer; // Assuming SerializableConsumer
import com.vaadin.flow.function.SerializableFunction;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.apache.tika.mime.MediaType;

class ValidationDelegate {

  // How many bytes to inspect for MIME type detection
  private static final int HEADER_LENGTH = 64;

  private final Map<String, MediaType> detectedMimeTypes = new HashMap<>();
  private final Set<String> rejectedFiles = new HashSet<>();
  private SerializableConsumer<FileData> rejectionListener;
  private MediaType[] acceptedMimeTypes;

  public void setRejectionListener(SerializableConsumer<FileData> rejectionListener) {
    this.rejectionListener = rejectionListener;
  }

  public void setAcceptedMimeTypes(String... acceptedMimeTypes) {
    this.acceptedMimeTypes = Stream.of(acceptedMimeTypes).map(MediaType::parse)
        .filter(Objects::nonNull)
        .toArray(MediaType[]::new);
  }

  // superGetFileData will call the super.getFileData method of the ValidatingReceiver instance.
  public OutputStream receiveUpload(String fileName, String mimeType, OutputStream out,
      SerializableFunction<String, FileData> superGetFileData
  ) {
    if (acceptedMimeTypes == null) {
      throw new IllegalArgumentException("No acceptedMimeTypes configured");
    }

    rejectedFiles.remove("");
    detectedMimeTypes.remove(fileName);
    AtomicBoolean rejected = new AtomicBoolean();

    return new ValidatingOutputStream(out, HEADER_LENGTH, headerBytes -> {
      MediaType detectedMimeType = MediaTypeUtils.detect(headerBytes);
      detectedMimeTypes.put(fileName, detectedMimeType);
      return Stream.of(acceptedMimeTypes)
          .anyMatch(type -> MediaTypeUtils.isInstanceOf(detectedMimeType, type));
    }, () -> { // onRejectCallback: Runnable (called by ValidatingOutputStream if validation fails)
      rejected.set(true);
    }) {
      @Override
      public void close() throws IOException {
        super.close();
        if (rejected.get()) {
          rejectedFiles.add(fileName);
          if (rejectionListener != null) {
            FileData fileData = getFileData(fileName, superGetFileData);
            rejectionListener.accept(fileData);
          }
        }
      }
    };
  }

  // superGetFileData will call the super.getFileData() method of the ValidatingReceiver instance.
  public FileData getFileData(String fileName,
      SerializableFunction<String, FileData> superGetFileData) {
    FileData file = superGetFileData.apply(fileName);
    if (file != null) {
      MediaType detectedType = detectedMimeTypes.get(fileName);
      if (detectedType != null) {
        String detectedMimeTypeStr = detectedType.toString();
        if (!detectedMimeTypeStr.equals(file.getMimeType())) {
          return new FileData(file.getFileName(), detectedMimeTypeStr, file.getOutputBuffer());
        }
      }
    }
    return file;
  }

  // getInputStreamCallback will call the super.getInputStreamCallback method of the
  // ValidatingReceiver instance.
  public InputStream getInputStream(String fileName,
      SerializableFunction<String, InputStream> superGetInputStream) {
    InputStream in = superGetInputStream.apply(fileName);
    // If the file was rejected, it should ideally not be available.
    if (in == null && rejectedFiles.contains(fileName) && detectedMimeTypes.containsKey(fileName)) {
      throw new IllegalStateException(
          "MIME type rejected, file not available: " + detectedMimeTypes.get(fileName));
    }
    return in;
  }
}