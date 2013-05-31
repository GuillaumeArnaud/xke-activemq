import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.DeliveryMode
import javax.jms.MessageProducer
import javax.jms.QueueConnection
import javax.jms.QueueSession
import javax.jms.Session
import javax.jms.Queue
import javax.jms.TextMessage

public class Sender {

    final QueueSession session
    final Queue queue
    final MessageProducer producer
    def final config
    def final messageContent

    public Sender(def config, String queueName, String host = "localhost", int port = 61616) {
        session = createQueueSession(host, port)
        queue = session.createQueue(queueName)
        producer = session.createProducer(queue)
        producer.setDeliveryMode(DeliveryMode.PERSISTENT)
        this.config = config
        StringBuffer msgBuf = new StringBuffer()
        config.messages.size.mean.times { msgBuf.append("0") }
        messageContent = msgBuf.toString()
        println this.messageContent
    }

    private static QueueSession createQueueSession(String host = "localhost", int port = 61616) {
        // create the connection
        def connection = new ActiveMQConnectionFactory("tcp://$host:$port").createQueueConnection()
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

    def send(TextMessage textMessage) {
        producer.send(queue, textMessage)
    }

    TextMessage create(long msgId) {
        def message = session.createTextMessage(messageContent)
        message.setLongProperty("msgId", msgId)
        message
    }
}
