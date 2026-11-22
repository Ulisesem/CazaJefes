package dev.cazajefes;

final class RareMenuStyle {
 static int[] slots(int count) {
  if(count<0||count>28)throw new IllegalArgumentException("La página admite entre 0 y 28 recetas");
  if(count==0)return new int[0];
  int rows=(count+6)/7,firstRow=rows<=2?2:1,used=0;
  int[] slots=new int[count];
  for(int row=0;row<rows;row++) {
   int amount=count/rows+(row<count%rows?1:0),half=amount/2;
   for(int i=0;i<amount;i++) {
    int column=4-half+i;
    if(amount%2==0&&i>=half)column++;
    slots[used++]=(firstRow+row)*9+column;
   }
  }
  return slots;
 }
 static String section(RareCompatibility.Group group){return group==null?"Esencias":group.title;}
}