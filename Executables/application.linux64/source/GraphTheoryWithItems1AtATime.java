import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GraphTheoryWithItems1AtATime extends PApplet {

ArrayList<Node> nodes;

Node selected;
Node moving;


float rad = 60;
float ring = 30;

public void setup() {
  
  ellipseMode(CENTER);
  textAlign(CENTER, CENTER);
  textSize(20);

  nodes = new ArrayList<Node>();
}

public void draw() {
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

public void mousePressed() {
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

public void mouseReleased() {
  if (mouseButton == LEFT) {
    moving = null;
  }
}

public void keyPressed() {
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
class Item{
  int amount;
  
  int col;
  
  Item(int a){
    col = color((int) random(256), (int) random(256), (int) random(256));
    
    amount = a;
  }
  
  Item(int a, int c){
    amount = a;
    col = c;
  }
  
  
}

public ArrayList<Item> copyItemList(ArrayList<Item> nA){
    ArrayList<Item> temp = new ArrayList<Item>();
    
    for(int i = 0; i < nA.size(); i++){
      Item x = nA.get(i);
      
      temp.add(new Item(x.amount, x.col));
    }
    
    return temp;
}
class Node {

  PVector pos;
  int col;

  int ringCol;
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

  public void pulse() {
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

  public void settle() {
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
      ringSize = size * 1.5f;
      ringCol = color(0, 255, 0);
    }

    if (previous > current) {
      ringSize = size * 1.5f;
      ringCol = color(255, 0, 0);
    }
  }

  public void setParent(Node c) {
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

  public void setChild(Node c) {
    if (child1 == null) {
      child1 = c;
    } else if ( child2 == null) {
      child2 = c;
    } else {
      child1 = child2;
      child2 = c;
    }
  }

  public void showPulse() {
    noFill();
    strokeWeight(2);
    stroke(ringCol);
    circle(pos.x, pos.y, ringSize);

    if (ringSize > 1) ringSize--;
  }

  public void showSelf() {
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

  public void showLines() {
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

  public void showOutput() {
    if (parent1 != null) {
      PVector edge = PVector.lerp(parent1.pos, pos, (parent1.size/2.0f)/pos.dist(parent1.pos));
      strokeWeight(2);
      stroke(255);
      fill(127, 0, 255);
      circle(edge.x, edge.y, (parent1.size/2.0f));
    }
    if (parent2 != null) {
      PVector edge = PVector.lerp(parent2.pos, pos, (parent2.size/2.0f)/pos.dist(parent2.pos));
      strokeWeight(2);
      stroke(255);
      fill(127, 0, 255);
      circle(edge.x, edge.y, (parent2.size/2.0f));
    }
  }

  public boolean isInside(PVector p) {
    return dist(p.x, p.y, pos.x, pos.y) < (size/2.0f);
  }

  public boolean isInside(float x, float y) {
    return dist(x, y, pos.x, pos.y) < (size/2.0f);
  }

  public void resetAmount() {
    items = copyItemList(initialItems);
    tempItems.clear();
    size = rad + items.size()*30;
  }

  public void resetConnections() {
    parent1 = null;
    parent2 = null;
    child1 = null;
    child2 = null;
    setColor();
  }

  public void resetMyConnections() {
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

  public void setColor() {
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
  
  public void addAllToChildTemp(ArrayList<Item> childTemp){
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
  
  public void addAllToChildTemp(ArrayList<Item> toMove, ArrayList<Item> childTemp){
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
  
  public void addToTemp(Item toMove, int qty, ArrayList<Item> childTemp){    
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
  
  public void addHalfToTwoChildTemp(ArrayList<Item> childTemp1, ArrayList<Item> childTemp2){
    
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
  public void settings() {  fullScreen(3); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GraphTheoryWithItems1AtATime" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
