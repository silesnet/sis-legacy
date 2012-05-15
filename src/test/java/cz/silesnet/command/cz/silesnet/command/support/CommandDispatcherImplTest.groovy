package cz.silesnet.command.cz.silesnet.command.support

import cz.silesnet.command.Command
import cz.silesnet.command.CommandHandler
import cz.silesnet.command.CommandName
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 15.5.12
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
class CommandDispatcherImplTest extends Specification {

    def "should execute command via registered handler"() {
        def commandName = CommandName.of('updateCustomerName')

        def handler = Mock(CommandHandler)
        handler.handles() >> [commandName]

        def command = Mock(Command)
        command.name() >> commandName

        def dispatcher = new CommandDispatcherImpl()
        dispatcher.addHandler(handler)

        when:
            dispatcher.dispatch(command)
        then:
            1 * handler.execute(command)
    }
}
