exercice1 {

    queuename = "myqueue"

    messages {
        size {
            mean = 100
            total = 4000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "tcp://localhost:2001"
        delay = 0
    }

    receiver {
        count = 1
        url = "tcp://localhost:2001"
        delay = 0
    }

    brokers = [1]
}

exercice2 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "tcp://localhost:2001?jms.useAsyncSend=true"
        delay = 0
    }

    receiver {
        count = 10
        url = "tcp://localhost:2001?jms.prefetchPolicy.all=1"
        delay = 2
    }

    brokers = [1]
}

exercice3 {

    queuename = "myqueue"

    messages {
        size {
            mean = 200
            total = 5000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
        url = "tcp://localhost:2001?jms.useAsyncSend=true"
        delay = 0
    }

    receiver {
        count = 10
        url = "tcp://localhost:2001?jms.prefetchPolicy.all=1"
        delay = 2
    }

    brokers = [1]
}