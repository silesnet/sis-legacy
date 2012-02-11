package cz.silesnet.event.impl

import cz.silesnet.event.support.JsonPayload
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Specification
import cz.silesnet.event.*

/**
 * User: der3k
 * Date: 5.2.12
 * Time: 17:56
 */
class SimpleEventBusTest extends Specification {
    def 'should publish and consume events'() {
        def bus = new SimpleEventBus()
        def key = Key.of('a.b')
        Event event
        def consumer = new EventConsumer() {
            void consume(final Event evt) {
                event = evt
            }
        }
        bus.subscribe(consumer, KeyPattern.of('a.*'))
    when:
        bus.publish(JsonPayload.of(key: 'value'), key)
    then:
        event.name() == 'b'
        event.domain() == 'a'
        event.value('key', String) == 'value'
        println event
    }

    def 'should add consumers spring friendly'() {
        def bus = new SimpleEventBus()
        def consumerWithPattern = new SimpleEventBus.ConsumerWithPattern()
        consumerWithPattern.consumer = new EventConsumer() {
            void consume(final Event event) { }
        }
        consumerWithPattern.pattern = '*'
        bus.consumers = [consumerWithPattern]
    expect:
        bus.consumers.size() == 1
    }

    def 'should instantiate from spring context file'() {
        def context = new ClassPathXmlApplicationContext('context/sis-bus.xml')
        def bus = context.getBean("sisBus", EventBus)
    expect:
        bus != null
    }
}
