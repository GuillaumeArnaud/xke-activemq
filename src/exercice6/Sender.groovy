package exercice6

import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.*

public class Sender {

    QueueSession session
    Queue queue
    MessageProducer producer
    def config
    def messageContent
    QueueConnection connection
    def configName

    public Sender(def config, def configName) {
        this.config = config
        this.configName = configName
        init()
        createMessageContent()
    }

    private void init() {
        connection = new ActiveMQConnectionFactory(config.senders[configName].url as String).createQueueConnection() as ActiveMQConnection
        connection.start()
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        queue = session.createQueue(config.queuename)
        producer = session.createProducer(queue)
        producer.setDeliveryMode(DeliveryMode.PERSISTENT)
    }

    /**
     * Create message content in relation to the message size configured in properties.
     */
    private void createMessageContent() {
        StringBuffer msgBuf = new StringBuffer()
        config.messages.size.mean.times { msgBuf.append('0') }
        messageContent = msgBuf.toString()
    }


    def send(TextMessage textMessage) {
        producer.send(queue, textMessage)
    }

    /**
     * Create a JMS TextMessage
     * @param msgId the identifier of the message
     * @return
     */
    TextMessage create(long msgId) {
        def message = session.createTextMessage(messageContent)
        message.setLongProperty("msgId", msgId)
        message
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
