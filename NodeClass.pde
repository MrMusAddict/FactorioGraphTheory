class Node {

  PVector pos;
  color col;

  color ringCol;
  float ringSize;

  Node parent1;
  Node parent2;

  Node child1;
  Node child2;


  float size;


  ArrayList<Item> items;
  ArrayList<Item> tempItems;
  ArrayList<Item> previousItems;

  ArrayList<Item> initialItems;
  
  boolean splitToChild1 = true;

  Node(int a, float x, float y) {

    pos = new PVector(x, y);
    col = color(255);

    ringCol = color(255);

    items = new ArrayList<Item>();
    tempItems = new ArrayList<Item>();
    previousItems  = new ArrayList<Item>();

    if (a > 0) {
      items.add(new Item(a));
    }

    initialItems = copyItemList(items);

    size = rad + items.size()*30;
  }

  void pulse() {
    if (child1 != null && child2 == null) {
      addAllToChildTemp(child1.tempItems);
    }
    if (child2 != null && child1 == null) {
      addAllToChildTemp(child2.tempItems);
    }
    if(child1 != null && child2 != null){
      addHalfToTwoChildTemp(child1.tempItems, child2.tempItems);
    }
  }

  void settle() {
    previousItems = copyItemList(items);
    if (child1 == null && child2 == null) {

      for (int i = 0; i < tempItems.size(); i++) {
        boolean found = false;
        for (int j = 0; j < items.size(); j++) {
          if (tempItems.get(i).col == items.get(j).col) {
            items.get(j).amount += tempItems.get(i).amount;
            found = true;
          }
        }

        if (!found) {
          items.add(new Item(tempItems.get(i).amount, tempItems.get(i).col));
        }
      }
    } else {
      items = copyItemList(tempItems);
    }
    tempItems.clear();

    float previous = 0;
    float current = 0;

    for (int i = 0; i < previousItems.size(); i++) {
      previous += previousItems.get(i).amount;
    }

    for (int i = 0; i < items.size(); i++) {
      current += items.get(i).amount;
    }

    size = rad + items.size()*30;

    if (previous < current) {
      ringSize = size * 1.5;
      ringCol = color(0, 255, 0);
    }

    if (previous > current) {
      ringSize = size * 1.5;
      ringCol = color(255, 0, 0);
    }
  }

  void setParent(Node c) {
    if (parent1 == null) {
      parent1 = c;
      c.setChild(this);
    } else if ( parent2 == null && parent1 != c) {
      parent2 = c;
      c.setChild(this);
    } else if (parent1 != c && parent2 != c) {
      parent1 = parent2;
      parent2 = c;
      c.setChild(this);
    }
  }

  void setChild(Node c) {
    if (child1 == null) {
      child1 = c;
    } else if ( child2 == null) {
      child2 = c;
    } else {
      child1 = child2;
      child2 = c;
    }
  }

  void showPulse() {
    noFill();
    strokeWeight(2);
    stroke(ringCol);
    circle(pos.x, pos.y, ringSize);

    if (ringSize > 1) ringSize--;
  }

  void showSelf() {
    if (selected == this) {
      fill(0, 0, 255);
    } else {
      fill(col);
    }
    stroke(0);
    circle(pos.x, pos.y, size);
    fill(0);

    for (int i = 0; i < items.size(); i++) {
      fill(items.get(i).col);
      circle(pos.x-30, pos.y - ((items.size()-1)*15) + (30 * i), 15);
      fill(0);
      text((int) items.get(i).amount, pos.x, pos.y - ((items.size()-1)*15) + (30 * i));
    }
  }

  void showLines() {
    if (parent1 != null) {
      strokeWeight(2);
      stroke(255);
      line(parent1.pos.x, parent1.pos.y, pos.x, pos.y);
    }
    if (parent2 != null) {
      strokeWeight(2);
      stroke(255);
      line(parent2.pos.x, parent2.pos.y, pos.x, pos.y);
    }
  }

  void showOutput() {
    if (parent1 != null) {
      PVector edge = PVector.lerp(parent1.pos, pos, (parent1.size/2.0)/pos.dist(parent1.pos));
      strokeWeight(2);
      stroke(255);
      fill(127, 0, 255);
      circle(edge.x, edge.y, (parent1.size/2.0));
    }
    if (parent2 != null) {
      PVector edge = PVector.lerp(parent2.pos, pos, (parent2.size/2.0)/pos.dist(parent2.pos));
      strokeWeight(2);
      stroke(255);
      fill(127, 0, 255);
      circle(edge.x, edge.y, (parent2.size/2.0));
    }
  }

  boolean isInside(PVector p) {
    return dist(p.x, p.y, pos.x, pos.y) < (size/2.0);
  }

  boolean isInside(float x, float y) {
    return dist(x, y, pos.x, pos.y) < (size/2.0);
  }

  void resetAmount() {
    items = copyItemList(initialItems);
    tempItems.clear();
    size = rad + items.size()*30;
  }

  void resetConnections() {
    parent1 = null;
    parent2 = null;
    child1 = null;
    child2 = null;
    setColor();
  }

  void resetMyConnections() {
    if (parent1 != null) {
      if (parent1.child1 == this) {
        parent1.child1 = parent1.child2;
      }
      parent1.child2 = null;
      parent1.setColor();
    }
    if (parent2 != null) {
      if (parent2.child1 == this) {
        parent2.child1 = parent2.child2;
      }
      parent2.child2 = null;
      parent2.setColor();
    }

    if (child1 != null) {
      if (child1.parent1 == this) {
        child1.parent1 = child1.parent2;
      }
      child1.parent2 = null;
      child1.setColor();
    }

    if (child2 != null) {
      if (child2.parent1 == this) {
        child2.parent1 = child2.parent2;
      }
      child2.parent2 = null;
      child2.setColor();
    }

    parent1 = null;
    parent2 = null;
    child1 = null;
    child2 = null;

    setColor();

    println(parent1);
    println(parent2);
    println(child1);
    println(child2);
  }

  void setColor() {
    if (parent1 == null && parent2 == null && (child1 != null || child2 != null)) {
      col = color(70, 160, 255);
    }
    if ((parent1 != null || parent2 != null) && (child1 != null || child2 != null)) {
      col = color(255, 255, 70);
    }
    if ((parent1 != null || parent2 != null) && child1 == null && child2 == null) {
      col = color(255, 70, 70);
    }
    if (parent1 == null && parent2 == null && child1 == null && child2 == null) {
      col = color(255);
    }
  }
  
  void addAllToChildTemp(ArrayList<Item> childTemp){
    for (int i = 0; i < items.size(); i++) {
        boolean found = false;
        for (int j = 0; j < childTemp.size(); j++) {
          if (items.get(i).col == childTemp.get(j).col) {
            childTemp.get(j).amount += items.get(i).amount;
            found = true;
          }
        }

        if (!found) {
          childTemp.add(new Item(items.get(i).amount, items.get(i).col));
        }
      }
  }
  
  void addAllToChildTemp(ArrayList<Item> toMove, ArrayList<Item> childTemp){
    for (int i = 0; i < toMove.size(); i++) {
        boolean found = false;
        for (int j = 0; j < childTemp.size(); j++) {
          if (toMove.get(i).col == childTemp.get(j).col) {
            childTemp.get(j).amount += toMove.get(i).amount;
            found = true;
          }
        }

        if (!found) {
          childTemp.add(new Item(toMove.get(i).amount, toMove.get(i).col));
        }
      }
  }
  
  void addToTemp(Item toMove, int qty, ArrayList<Item> childTemp){    
      boolean found = false;
      for (int j = 0; j < childTemp.size(); j++) {
        if (toMove.col == childTemp.get(j).col) {
          childTemp.get(j).amount += qty;
          found = true;
        }
      }

      if (!found) {
        childTemp.add(new Item(qty, toMove.col));
      } 
  }
  
  void addHalfToTwoChildTemp(ArrayList<Item> childTemp1, ArrayList<Item> childTemp2){
    
    ArrayList<Item> tempItems1 = new ArrayList<Item>();
    ArrayList<Item> tempItems2 = new ArrayList<Item>();
    
    for(int i = 0; i < items.size(); i++){
     for(int j = 0; j < items.get(i).amount; j++){
      if(splitToChild1){
        addToTemp(items.get(i), 1, tempItems1);
      }else{
        addToTemp(items.get(i), 1, tempItems2);
      }
      splitToChild1 = !splitToChild1;
     }
    }
    
    addAllToChildTemp(tempItems1, childTemp1);
    addAllToChildTemp(tempItems2, childTemp2);
  }
}
