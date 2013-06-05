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
        // TODO créer la connexion ActiveMQ (l'url se trouve dans l'objet config.receiver.url)
        //connection =
        connection.start()

        // TODO crée une QueueSession en AUTO_ACKNOWLEDGE
        // session =

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
