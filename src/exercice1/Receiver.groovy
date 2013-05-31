import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.ActiveMQPrefetchPolicy

import javax.jms.Message

import javax.jms.QueueConnection
import javax.jms.QueueReceiver
import javax.jms.QueueSession
import javax.jms.Session

public class Receiver {

    final QueueSession session
    final javax.jms.Queue queue
    final QueueReceiver receiver
    final def config

    public Receiver(def config, String queueName, String url = "localhost:61616") {
        session = createQueueSession(url)
        queue = session.createQueue(queueName)
        receiver = session.createReceiver(queue)
        this.config = config
    }

    private static QueueSession createQueueSession(String url) {
        // create the connection
        ActiveMQConnection connection = new ActiveMQConnectionFactory(url).createQueueConnection() as ActiveMQConnection
        connection.start()

        // create the session
        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)

        initMixins(session, connection)

        session
    }

    private static void initMixins(QueueSession session, QueueConnection connection) {
        // init the mixins CloseableSession
        session.session = session
        session.connection = connection
    }

    public void close() {
        if (session) session.closeAll()
    }

    def Message receive(int currCounter) {
        receiver.receive()
    }

}
