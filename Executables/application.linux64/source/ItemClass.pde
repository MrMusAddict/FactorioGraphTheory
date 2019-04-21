class Item{
  int amount;
  
  color col;
  
  Item(int a){
    col = color((int) random(256), (int) random(256), (int) random(256));
    
    amount = a;
  }
  
  Item(int a, color c){
    amount = a;
    col = c;
  }
  
  
}

ArrayList<Item> copyItemList(ArrayList<Item> nA){
    ArrayList<Item> temp = new ArrayList<Item>();
    
    for(int i = 0; i < nA.size(); i++){
      Item x = nA.get(i);
      
      temp.add(new Item(x.amount, x.col));
    }
    
    return temp;
}
