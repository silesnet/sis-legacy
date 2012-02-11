package cz.silesnet.event.support;

import cz.silesnet.event.Payload;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 16:07
 */
public class JsonPayload implements Payload {
    private final String text;
    private final Map<String, Object> map;

    public static Builder builder() {
        return new Builder();
    }

    public static JsonPayload of(final Map map) {
        return new JsonPayload(map);
    }

    public static JsonPayload parse(final String text) {
        final ObjectMapper mapper = new ObjectMapper();
        final Map map;
        try {
            map = mapper.readValue(text, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JsonPayload(map);
    }

    public <T> T value(final String key, final Class<T> type) {
        return (T) map.get(key);
    }

    public String toString() {
        return text;
    }

    private JsonPayload(final Map map) {
        this.map = map;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            this.text = mapper.writeValueAsString(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        private Map map = new HashMap();

        private Builder() { }

        public JsonPayload build() {
            return new JsonPayload(map);
        }

        public Builder add(final String key, final Object value) {
            map.put(key, value);
            return this;
        }

        public Builder add(final Map map) {
            this.map.putAll(map);
            return this;
        }

    }

}
