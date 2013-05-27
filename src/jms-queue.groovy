@Grab(group = 'com.netflix.rxjava', module = 'rxjava-groovy', version = '0.8.4')
@Grab(group = "org.apache.activemq", module = "activemq-all", version = "5.8.0")
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

import static java.lang.System.nanoTime
import static java.util.concurrent.TimeUnit.NANOSECONDS
import static java.util.concurrent.TimeUnit.SECONDS

Thread.sleep(10000)

def maxMsg = 5000000
def nbSndr = 30
def nbRcvr = 30

def queueName = "test"
AtomicLong currSndMsg = new AtomicLong(0), currRcvMsg = new AtomicLong(0)
def rcvStopped = new LinkedBlockingQueue<Long>()
def sndStopped = new LinkedBlockingQueue<Long>()

JMSUtils.purgeQueue(queueName = "myqueue")

nbSndr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def session
            try {
                session = JMSUtils.createQueueSession()
                def queue = session.createQueue(queueName)
                def producer = session.createProducer(queue)

                long startTime = nanoTime()
                while (currSndMsg.get() < maxMsg) {
                    def m = currSndMsg.getAndIncrement()
                    producer.send(queue, session.createTextMessage("test $m"))
                }
                sndStopped << nanoTime() - startTime
            } finally {
                if (session) session.closeAll()
            }
        }
    }).start()
}

nbRcvr.times {
    new Thread(new Runnable() {
        @Override
        void run() {
            def session
            try {
                session = JMSUtils.createQueueSession()
                def stop = false

                def queue = session.createQueue(queueName)
                def rcv = session.createReceiver(queue)

                def startTime = nanoTime()
                while (!stop) {
                    def msg = rcv.receive()
                    stop = msg.getBooleanProperty("poison.pill")
                    if (!stop) currRcvMsg.incrementAndGet()
                }
                rcvStopped << nanoTime() - startTime
            } finally {
                if (session) session.closeAll()
            }
        }
    }).start()
}

new Thread(new Runnable() {

    @Override
    void run() {
        // waiting the end of all senders
        long sndElapsed = 0
        nbSndr.times { sndElapsed += sndStopped.take() }
        println """
 end of sends:
    total elapsed time : ${NANOSECONDS.toMillis(sndElapsed)}ms
    total sent requests : ${currSndMsg.intValue()}
    avg send elapsed time : ${NANOSECONDS.toMillis((long) sndElapsed / currSndMsg.get().intValue())}ms/req
 start poison pills
"""
        // send poison pills to receivers
        def session
        try {
            session = JMSUtils.createQueueSession()

            def queue = session.createQueue(queueName)
            def producer = session.createProducer(queue)

            def poisonPill = session.createTextMessage()
            poisonPill.setBooleanProperty("poison.pill", true)

            long rcvElapsed = 0
            nbRcvr.times {
                producer.send(queue, poisonPill)
                rcvElapsed += rcvStopped.poll(1, SECONDS)
            }
            println """
 end of receptions:
    total elapsed time : ${NANOSECONDS.toMillis(rcvElapsed)}ms
    total received requests : ${currRcvMsg.intValue()}
    avg reception elapsed time : ${NANOSECONDS.toMillis((long) rcvElapsed / currRcvMsg.intValue())}ms/req
"""
        } finally {
            if (session) session.closeAll()
            System.exit(0)
        }
    }
}).start()

