xke-activemq
============

Hands on pour le XKE d'ActiveMQ

Exercice 1

* 1 broker / 1 sender / 1 receiver
* start / stop du broker
* pas de perte de message

=> catch connexion exceptions / persistence

Exercice 2

* 1 broker / 1 sender / 1 receiver
* taille des messages variables
* test de performances

=> grouper / acknowledge / fetch

Exercice 3

* 2 brokers actif/passif / 1 à n senders / 1 à n receiver
* start / stop du master

=> failover / failback / performance du broker actif

Exercice 4

* 2 brokers en network duplex / 2 senders / 2 receivers
* start / stop d'un des brokers
* stop d'un des receivers
* aucune perte de messages

=> network de broker / failover / balancing des clients

Exercice 5

* 2 brokers _sender_ / 2 brokers _receiver_ / 2 senders / 2 receivers
* start / stop des brokers à tour de rôle
* aucune perte de messages

=> mode unidirectionnel / load balancing à l'émission et réception

Exercice 6

* 2 brokers en network duplex / 2 brokers en passif / 1 à n senders / 1 à n receivers
* start / stop des masters à tour de rôles
* aucune perte de messages
* load balancing _equitable_ entre les brokers

=> actif/passif + network / performance par rapport à l'exercice 3

Exercice 7

* 1 à n brokers en multicast / 0 network / n senders / n receivers
* request / reply (queues temporaires)

=> scaling / performance
