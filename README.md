# FactorioGraphTheory

You can create 2 types of nodes:

1. Input Node (starts with a value of 100)
2. Standard Node (starts with a value of 0)

___
Color logic

* If a node has no parents or children, it's white.
* If a node only has children (meaning it's the input), it's blue
* If a node only has parents (meaning it's an output), it's red
* If a node has both parents and children, (meaning it's a splitter) it's yellow

___
Line Logic

* If two nodes are connected with a line, they're connected in code
* The end of the line with a purple bubble marks the output of the parent, and the output travels to the node with no bubble.
  * Sometimes a line will have a purple bubble on both sides. That means that they feed each other part of their output.

___

Other Logic

* All nodes can only have 2 parents and 2 children (to be consistent with splitters in Factorio). If you try to make a third connection, the oldest connection will be deleted.

___

Controls

* Place an Input Node by pressing 'I'
* Place a Regular Node by pressing 'N'

* Select a node by right-clicking
* If you select 2 nodes, the first become the parent to the second.
* Move a node by left-click-dragging

* Reset the numbers by pressing 'A'
* Delete a selected node by pressing 'D'
* You can clear connections 2 ways:
  * Selected Only, press 'R'
  * All connections, clear the selection and press 'R'
* Pulse the value-transfers by pressing Spacebar.
