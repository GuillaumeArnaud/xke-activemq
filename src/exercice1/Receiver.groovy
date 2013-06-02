package exercice1

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.JMSException
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
        def reattempt = true

        while (reattempt) {
            try {
                connection = new ActiveMQConnectionFactory(config.receiver.url as String).createQueueConnection() as ActiveMQConnection
                connection.start()

                session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

                queue = session.createQueue(config.queuename)
                receiver = session.createReceiver(queue)
                reattempt = false
            } catch (JMSException e) {
                reattempt = true
                sleep(1000)
            }
        }
    }

    /**
     * Receive the next message from the broker(s).
     *
     * @return the JMS message
     */
    def Message receive() {
        def msg = null
        def reattempt = true

        while (reattempt)
            try {
                msg = receiver.receive()
                reattempt = false
            } catch (JMSException e) {
                close()
                sleep(1000)
                init()
                reattempt = true
                println "reattempt receiver"
            }
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
