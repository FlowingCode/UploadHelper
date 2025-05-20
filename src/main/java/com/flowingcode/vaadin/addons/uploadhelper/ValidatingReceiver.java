package com.flowingcode.vaadin.addons.uploadhelper;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.function.SerializableConsumer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.tika.mime.MediaType;

public interface ValidatingReceiver {

  void setRejectionListener(SerializableConsumer<FileData> rejectionListener);

  void setAcceptedMimeTypes(MediaType[] acceptedMimeTypes);

  default void setAcceptedMimeTypes(Upload upload, String... additionalMimeTypes) {
    setAcceptedMimeTypes(upload, Arrays.asList(additionalMimeTypes));
  }

  default void setAcceptedMimeTypes(Upload upload, Collection<String> acceptedMimeTypes) {
    setAcceptedMimeTypes(Stream.concat(
        upload.getAcceptedFileTypes().stream(),
        acceptedMimeTypes.stream()));
  }

  default void setAcceptedMimeTypes(String... acceptedMimeTypes) {
    setAcceptedMimeTypes(Stream.of(acceptedMimeTypes));
  }

  default void setAcceptedMimeTypes(Collection<String> acceptedMimeTypes) {
    setAcceptedMimeTypes(acceptedMimeTypes.stream());
  }

  private void setAcceptedMimeTypes(Stream<String> acceptedMimeTypes) {
    setAcceptedMimeTypes(acceptedMimeTypes.map(MediaType::parse)
        .filter(Objects::nonNull)
        .toArray(MediaType[]::new));
  }

}
