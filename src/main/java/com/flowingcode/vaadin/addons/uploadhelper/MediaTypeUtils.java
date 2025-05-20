package com.flowingcode.vaadin.addons.uploadhelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;

public class MediaTypeUtils {

  private static final MediaTypeRegistry registry =
      TikaConfig.getDefaultConfig().getMediaTypeRegistry();

  private static final Detector detector = TikaConfig.getDefaultConfig().getDetector();

  /**
   * Normalizes the given a media type.
   *
   * @param type a media type
   * @return type, normalized
   */
  public static MediaType normalize(MediaType type) {
    return registry.normalize(type);
  }

  /**
   * Checks whether the given media type equals the given base type or is a specialization of it.
   * Both types should be already normalized.
   *
   * @param a media type, normalized
   * @param b base type, normalized
   * @return <code>true</code> if b equals a or is a specialization of it, <code>false</code>
   *         otherwise
   */
  public static boolean isInstanceOf(MediaType a, MediaType b) {
    return registry.isInstanceOf(a, b);
  }

  /**
   * Detects the media type of the given document. The type detection is based on the first few
   * bytes of a document.
   * <p>
   * For best results at least a few kilobytes of the document data are needed. See also the other
   * detect() methods for better alternatives when you have more than just the document prefix
   * available for type detection.
   *
   * @param prefix first few bytes of the document
   * @return detected media type
   */
  public static MediaType detect(byte[] prefix) {
    try {
      return registry.normalize(
          detector.detect(new ByteArrayInputStream(prefix), new Metadata()).getBaseType());
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected IOException", e);
    }
  }

}
