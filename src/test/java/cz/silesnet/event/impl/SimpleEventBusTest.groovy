package cz.silesnet.event.impl

import cz.silesnet.event.Event
import cz.silesnet.event.EventConsumer
import cz.silesnet.event.EventKey
import cz.silesnet.event.PublishedEvent
import spock.lang.Specification

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 17:56
 */
class SimpleEventBusTest extends Specification {
    def 'should publish and consume events'() {
        def bus = new SimpleEventBus()
        def pattern = new EventKey()
        def event
        def consumer = new EventConsumer() {
            void consume(final PublishedEvent evt) {
                event = evt
            }
        }
        bus.subscribe(consumer, pattern)
    when:
        bus.publish(pattern, new Event() {
            def <T> T value(final String key, final Class<T> type) { 'value' }
        })
    then:
        event.value('key', String) == 'value'
    }

    def 'should add consumers spring friendly'() {
        def bus = new SimpleEventBus()
        def consumerWithPattern = new SimpleEventBus.ConsumerWithPattern()
        consumerWithPattern.consumer = new EventConsumer() {
            void consume(final PublishedEvent event) { }
        }
        consumerWithPattern.pattern = '*'
        bus.consumers = [consumerWithPattern]
    expect:
        bus.consumers.size() == 1
    }

    def 'should instantiate from spring context file'() {
    expect:
        false
    }
}
