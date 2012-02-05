package cz.silesnet.event.support

import spock.lang.Specification

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 16:11
 */
class JsonEventTest extends Specification {
    def 'should build from key-value pairs'() {
        def event = JsonEvent.builder().add("key", "value").build()
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should build from map'() {
        def event = JsonEvent.builder().add(key: 'value').build()
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should parse json'() {
        def event = JsonEvent.parse('{"key":"value"}')
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should return value by key'() {
        def event = JsonEvent.parse('{"key":"value"}')
    expect:
        event.value('key', String) == 'value'
    }

    def 'should return typed value by key'() {
        def event = JsonEvent.parse('{"key":1}')
    expect:
        event.value('key', Integer) instanceof Integer
    }
}
