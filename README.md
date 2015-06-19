# Siddhartha

Siddhartha is a simple, toy [DHT](https://en.wikipedia.org/wiki/Distributed_hash_table)
written in [Scala](http://www.scala-lang.org/) with 
[Akka](http://akka.io).

## Technical

### Key Space Distribution

This DHT uses a binary-tree type key space distribution algorithm, as
illustrated below:

```
1. -> node(0) = [0, 200)  -- the top level node

2.    node(0) = [0, 100),
   -> node(1) = [100, 200)

3.    node(0) = [0, 100)
      node(1) = [100, 150)
      node(2) = [150, 200)

  ...
```

The `->` signifies where a new node attaches.

#### Forwarding

If a node receives a request for a key `k` which is not in its key space `[a, b)`,
it forwards the message to:

- if `k >= b` to the child whose `k >= a(c)` (there is only one such child)
- if `k < a` to its parent

## Usage

Firstly, there are two main classes you should be concerned about:

1. `Dharma` is the 'system' class. It wraps around `ActorSystem` and manages
 a local DHT system. It does not have to be a remotely accessible system.

    - Each `Dharma` must have a `buddha`-named actor which is responsible for
    managing the Siddhartha instances and their properties. It can also be used
    for finding the Siddhartha's in a `Dharma`.

2. `SiddharthaMap` is a Map-like interface for the DHT. It requires finding out
 a `Siddhartha`-type `ActorRef` from a `Dharma` and it routes all messages
 towards it.

```scala
val dharma = Dharma()

// top level siddhartha that manages the whole keyspace
val sdh = dharma.createSiddhartha((Keyspace.min, Keyspace.max))

val map = SiddharthaMap(sdh)

// these are all futures
map.put(Bytes(0, 1, 2, 3), Bytes(0, 1, 2, 3))
map.get(Bytes(0, 1, 2, 3))
map.remove(Bytes(0, 1, 2, 3)

// child siddhartha, manages half of sdh's keyspace
val childSdh = dharma.createSiddhartha(sdh)
```

### Remoting

Read about [Akka remoting](http://doc.akka.io/docs/akka/2.2.3/scala/remoting.html)
if you want to use the system as a true P2P DHT.

*Remember:* Create Siddhartha's on the local `Dharma` but with remote `ActorRef`'s.

## License

This project is Copyright (c) 2015 Stojan Dimitrovski, except where otherwise
noted.

See the accompanying file `LICENSE.txt` for the full legal text.