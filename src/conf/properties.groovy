exercice1 {

    queuename = "myqueue"

    messages {
        size {
            mean = 10000
            total = 100000000
        }
        count = size.total / size.mean
    }

    sender {
        count = 1
    }

    receiver {
        count = 1
    }
}