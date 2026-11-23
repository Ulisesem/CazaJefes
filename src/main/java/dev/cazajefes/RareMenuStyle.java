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
 static int[] bookSlots(int count){if(count<0||count>14)throw new IllegalArgumentException("Máximo 14 libros");int[] out=new int[count];int used=0;for(int band=0;used<count;band++){int take=Math.min(7,count-used),start=(9-take)/2;for(int i=0;i<take;i++)out[used++]=(band*2+1+i%2)*9+start+i;}return out;}
 static int[] materialSlots(int count){if(count<0||count>20)throw new IllegalArgumentException("Máximo 20 materiales");if(count==0)return new int[0];int rows=(count+4)/5,base=count/rows,extra=count%rows;int[] out=new int[count];int used=0;for(int row=0;row<rows;row++){int amount=base+((row==(rows/2)&&extra>0)?1:0);if(extra>1&&row<extra-1)amount++;int half=amount/2;for(int i=0;i<amount;i++){int column=4-half+i;if(amount%2==0&&i>=half)column++;out[used++]=(row+1)*9+column;}}return out;}
 static String section(RareCompatibility.Group group){return group==null?"Esencias":group.title;}
}