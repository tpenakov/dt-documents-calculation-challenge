package design.technologies.api.test.utils;

import io.vavr.control.Try;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/*
 * Created by triphon 12.11.22 г.
 */
@Getter
@SuppressWarnings("unused")
public class UnitTestUtils {
  public static final Function<InputStream, String> READ_TEXT_FILE_FN =
      (s ->
          Try.of(() -> IOUtils.toString(s, StandardCharsets.UTF_8))
              .getOrElseThrow(throwable -> new RuntimeException(throwable)));

  public static UnitTestUtils of() {
    return new UnitTestUtils();
  }

  public String readFromTextFile(final String path) {
    return READ_TEXT_FILE_FN.apply(getClass().getClassLoader().getResourceAsStream(path));
  }
}
