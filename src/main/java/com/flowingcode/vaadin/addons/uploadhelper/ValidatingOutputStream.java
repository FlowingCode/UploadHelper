package com.flowingcode.vaadin.addons.uploadhelper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Predicate;

public class ValidatingOutputStream extends FilterOutputStream {

  private ByteBuffer header;

  private boolean rejected;

  private Predicate<byte[]> predicate;

  private Runnable onRejection;

  public ValidatingOutputStream(OutputStream out, int maxHeaderLength,
      Predicate<byte[]> predicate, Runnable onRejection) {
    super(out);
    header = ByteBuffer.allocate(maxHeaderLength);
    this.predicate = Objects.requireNonNull(predicate);
    this.onRejection = Objects.requireNonNull(onRejection);
  }

  @Override
  public void write(int b) throws IOException {
    if (!rejected) {
      if (header != null && header.hasRemaining()) {
        header.put((byte) b);
        if (!header.hasRemaining()) {
          validate();
        }
      }

      super.write(b);
    }
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (!rejected) {
      if (header != null && header.hasRemaining()) {
        header.put(b, off, Math.min(len, header.remaining()));
        if (!header.hasRemaining()) {
          validate();
        }
      }

      super.write(b, off, len);
    }
  }

  @Override
  public void close() throws IOException {
    super.close();
    if (header != null) {
      validate();
    }
  }

  private void validate() {
    rejected = !predicate.test(header.array());
    header = null;
    if (rejected) {
      onRejection.run();
    }
  }

}
