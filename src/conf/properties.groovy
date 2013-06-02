exercice1 {

    queuename = "myqueue"

    messages {
        size {
            mean = 100
            total = 10000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 20
        url = "failover:(nio://localhost:61616)"//?jms.useAsyncSend=true"
        delay = 0
    }

    receiver {
        count = 10
        url = "failover:(nio://localhost:61616)"
        delay = 0
    }
}