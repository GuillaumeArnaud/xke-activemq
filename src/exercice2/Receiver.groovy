package exercice2

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

    public Receiver(def config) {
        this.config = config

        init()
    }

    private void init() {

        connection = new ActiveMQConnectionFactory(config.receiver.url as String).createQueueConnection() as ActiveMQConnection
        connection.start()

        session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE)

        queue = session.createQueue(config.queuename)
        receiver = session.createReceiver(queue)
    }

    def counter = 0
    /**
     * Receive the next message from the broker(s).
     *
     * @return the JMS message
     */
    def Message receive() {
        def msg = receiver.receive()
        counter++
        if (counter % 1 == 0) msg.acknowledge()
        msg
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
