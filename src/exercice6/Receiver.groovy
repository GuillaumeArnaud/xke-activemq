package exercice6

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.Message
import javax.jms.QueueReceiver
import javax.jms.QueueSession
import javax.jms.Session

public class Receiver {

    QueueSession session
    javax.jms.Queue queue
    QueueReceiver receiver
    ActiveMQConnection connection
    def config
    def configName

    public Receiver(def config, def configName) {
        this.config = config
        this.configName = configName
        init()
    }

    private void init() {
        connection = new ActiveMQConnectionFactory(config.receivers[configName].url as String).createQueueConnection() as ActiveMQConnection
        connection.start()

        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

        queue = session.createQueue(config.queuename)
        receiver = session.createReceiver(queue)
    }

    /**
     * Receive the next message from the broker(s).
     *
     * @return the JMS message
     */
    def Message receive() {
        receiver.receive()
    }

    /**
     * Close the connection and the session.
     */
    public void close() {
        try {
            if (session) {
                session.close()
            }
            if (connection) {
                connection.close()
            }
        } catch (Throwable t) {
            println "can't close connection"
            t.printStackTrace()
        }
    }
}
