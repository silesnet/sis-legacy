package cz.silesnet.event

import spock.lang.Specification

/**
 * User: der3k
 * Date: 6.2.12
 * Time: 22:37
 */
class KeyPatternTest extends Specification {
    def 'should match key by regex'() {
    expect:
        KeyPattern.of(regex).matches(Key.of(key))
    where:
        regex | key
        'a' | 'a'
        '*' | 'a'
        'a.b*' | 'a.b.c'
        'a.b.*' | 'a.b.c'
    }
}
