ArrayList<Node> nodes;

Node selected;
Node moving;


float rad = 60;
float ring = 30;

void setup() {
  fullScreen(3);
  ellipseMode(CENTER);
  textAlign(CENTER, CENTER);
  textSize(20);

  nodes = new ArrayList<Node>();
}

void draw() {
  background(51);

  if (moving != null) {
    moving.pos = new PVector(mouseX, mouseY);
  }
  
  for (Node n : nodes) {
    n.showPulse();
  }

  for (Node n : nodes) {
    n.showLines();
  }

  for (Node n : nodes) {
    n.showOutput();
  }

  for (Node n : nodes) {
    n.showSelf();
  }
}

void mousePressed() {
  if (mouseButton == RIGHT) {
    if (selected == null) {
      for (Node n : nodes) {
        if (n.isInside(mouseX, mouseY)) {
          selected = n;
        }
      }
    } else {
      boolean noneFound = true;
      for (Node n : nodes) {
        if (n.isInside(mouseX, mouseY) && selected != n) {
          n.setParent(selected);
          n.setColor();
          selected.setColor();
          noneFound = true;
        }
      }

      if (noneFound) {
        selected = null;
      }
    }
  }

  if (mouseButton == LEFT) {
    for (Node n : nodes) {
      if (n.isInside(mouseX, mouseY)) {
        moving = n;
      }
    }
  }
}

void mouseReleased() {
  if (mouseButton == LEFT) {
    moving = null;
  }
}

void keyPressed() {
  if (key == ' ') {
    for (Node n : nodes) {
      n.pulse();
    }

    for (Node n : nodes) {
      n.settle();
    }
  }

  if (key == 'i') {
    nodes.add(new Node((int) random(500), mouseX, mouseY));
  }

  if (key == 'n') {
    nodes.add(new Node(0, mouseX, mouseY));
  }

  if (key == 'c') {
    nodes.clear();
  }

  if (key == 'a') {
    for (Node n : nodes) {
      n.resetAmount();
    }
  }

  if (key == 'r') {
    if (selected == null) {
      for (Node n : nodes) {
        n.resetConnections();
      }
    } else {
      selected.resetMyConnections();
    }
  }

  if (key == 'd') {
    if (selected != null) {
      selected.resetMyConnections();

      int temp = 0;

      for (int i = 0; i < nodes.size(); i++) {
        if (selected == nodes.get(i)) {
          temp = i;
        }
      }

      selected = null;
      nodes.remove(temp);
    }
  }
}
