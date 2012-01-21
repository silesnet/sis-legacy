package cz.silesnet.util;


import spock.lang.Specification
import static cz.silesnet.util.StringUtils.join

/**
 * User: admin
 * Date: 21.1.12
 * Time: 12:55
 */
public class StringUtilsTest extends Specification {
    def 'join should return string of separated members'() {
    expect:
        join(list, ', ') == joined
    where:
        list               | joined
        []                 | ''
        [1]                | '1'
        [1, 2]             | '1, 2'
        [1, 2, 3, 'hello'] | '1, 2, 3, hello'
    }

    def 'join should fail when iterator is null'() {
    when:
        join(null, ', ')
    then:
        thrown IllegalArgumentException
    }

    def 'join should fail when separator is null'() {
    when:
        join([], null)
    then:
        thrown IllegalArgumentException
    }
}
