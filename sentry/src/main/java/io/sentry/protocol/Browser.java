package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.IUnknownPropertiesConsumer;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonObjectWriter;
import io.sentry.JsonSerializable;
import io.sentry.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Browser implements IUnknownPropertiesConsumer, JsonSerializable {
  public static final String TYPE = "browser";
  /** Display name of the browser application. */
  private @Nullable String name;
  /** Version string of the browser. */
  private @Nullable String version;

  @SuppressWarnings("unused")
  private @Nullable Map<String, @NotNull Object> unknown;

  public Browser() {}

  Browser(final @NotNull Browser browser) {
    this.name = browser.name;
    this.version = browser.version;
    this.unknown = CollectionUtils.newConcurrentHashMap(browser.unknown);
  }

  public @Nullable String getName() {
    return name;
  }

  public void setName(final @Nullable String name) {
    this.name = name;
  }

  public @Nullable String getVersion() {
    return version;
  }

  public void setVersion(final @Nullable String version) {
    this.version = version;
  }



  @ApiStatus.Internal
  @Override
  public void acceptUnknownProperties(final @NotNull Map<String, Object> unknown) {
    this.unknown = new ConcurrentHashMap<>(unknown);
  }

  // region JsonSerializable

  public static final class JsonKeys {
    public static final String NAME = "name";
    public static final String VERSION = "version";
  }

  @Override
  public void serialize(@NotNull JsonObjectWriter writer, @NotNull ILogger logger) throws IOException {
    writer.beginObject();
    if (name != null) {
      writer.name(JsonKeys.NAME).value(name);
    }
    if (version != null) {
      writer.name(JsonKeys.VERSION).value(version);
    }
    writer.endObject();
  }

  @Nullable
  @Override
  public Map<String, Object> getUnknown() {
    return unknown;
  }

  @Override
  public void setUnknown(@Nullable Map<String, Object> unknown) {
    this.unknown = unknown;
  }

  public static final class Deserializer implements JsonDeserializer<Browser> {
    @Override
    public @NotNull Browser deserialize(@NotNull JsonObjectReader reader, @NotNull ILogger logger) throws Exception {
      reader.beginObject();
      Browser browser = new Browser();
      Map<String, Object> unknown = null;
      do {
        final String nextName = reader.nextName();
        switch (nextName) {
          case JsonKeys.NAME:
            browser.name = reader.nextStringOrNull();
            break;
          case JsonKeys.VERSION:
            browser.version = reader.nextStringOrNull();
            break;
          default:
            if (unknown == null) {
              unknown = new ConcurrentHashMap<>();
            }
            reader.nextUnknown(logger, unknown, nextName);
            break;
        }
      } while (reader.hasNext());
      browser.setUnknown(unknown);
      reader.endObject();
      return browser;
    }
  }

  // endregion
}
