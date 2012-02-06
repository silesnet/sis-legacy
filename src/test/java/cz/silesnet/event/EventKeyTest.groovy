package cz.silesnet.event

import spock.lang.Specification
import static Key.*

/**
 * User: der3k
 * Date: 6.2.12
 * Time: 21:46
 */
class EventKeyTest extends Specification {
    def 'should match only itself'() {
    expect:
        of('a').matches(of('a'))
        !of('b').matches(of('a'))
    }

    def 'should return event prefix and name'() {
    expect:
        of(key).domain() == parts[0]
        of(key).name() == parts[1]
    where:
        key     | parts
        'a'     | ['', 'a']
        '.a'    | ['', 'a']
        'a.a'   | ['a', 'a']
        'a.a.a' | ['a.a', 'a']
    }

    def 'should fail when creating illegal key'() {
    expect:
        try {
            of(key)
            assert false
        } catch (Exception e) {
            // expected
        }
    where:
        key << [null, '', '.', 'a.', '..a', 'a*a', ' ', 'a a']
    }

    def 'should return whole key as its stirng representation'() {
    expect:
        of('a').toString() == 'a'
        of('a.b.c').toString() == 'a.b.c'
    }
}
