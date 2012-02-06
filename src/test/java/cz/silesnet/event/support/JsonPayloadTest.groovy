package cz.silesnet.event.support

import spock.lang.Specification

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 16:11
 */
class JsonPayloadTest extends Specification {
    def 'should build from key-value pairs'() {
        def event = JsonPayload.builder().add("key", "value").build()
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should build from map'() {
        def event = JsonPayload.builder().add(key: 'value').build()
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should parse json'() {
        def event = JsonPayload.parse('{"key":"value"}')
    expect:
        event.toString() == '{"key":"value"}'
    }

    def 'should return value by key'() {
        def event = JsonPayload.parse('{"key":"value"}')
    expect:
        event.value('key', String) == 'value'
    }

    def 'should return typed value by key'() {
        def event = JsonPayload.parse('{"key":1}')
    expect:
        event.value('key', Integer) instanceof Integer
    }
}
