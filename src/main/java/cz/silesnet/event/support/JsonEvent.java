package cz.silesnet.event.support;

import cz.silesnet.event.Event;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 16:07
 */
public class JsonEvent implements Event {
    private final String text;
    private final Map<String, Object> map;

    private JsonEvent(final Map<String, Object> map) {
        this.map = map;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            this.text = mapper.writeValueAsString(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static JsonEvent parse(final String text) {
        final ObjectMapper mapper = new ObjectMapper();
        final Map map;
        try {
            map = mapper.readValue(text, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JsonEvent(map);
    }

    public <T> T value(final String key, final Class<T> type) {
        return (T) map.get(key);
    }

    public String toString() {
        return text;
    }

    public static class Builder {
        private Map<String, Object> map = new HashMap<String, Object>();

        private Builder() { }

        public JsonEvent build() {
            return new JsonEvent(map);
        }

        public Builder add(final String key, final String value) {
            map.put(key, value);
            return this;
        }

        public Builder add(final Map<String, Object> map) {
            this.map.putAll(map);
            return this;
        }

    }

}
