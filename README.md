1.代码出处
==========

本代码参考改造于“CSDN咨询”https://blog.csdn.net/csdnnews/article/details/104218200 ；

参考2020年2月6日由人民日报公众号转发了一则由Ele实验室制作的疫情传播仿真程序。

2.代码思路
==========

1.`Constants.java`
----------------

```java
public class Constants {
    //初始感染数量
    public static int ORIGINAL_COUNT=50;
    //传播率，可通过戴口罩降低
    public static float BROAD_RATE = 0.2f;
    //潜伏时间
    public static float SHADOW_TIME = 140;
    //医院收治响应时间
    public static int HOSPITAL_RECEIVE_TIME=10;
    //医院床位
    public static int BED_COUNT=1000;
    //流动意向平均值，限制出行，减少人员流动
    public static float u=-0.99f;
}
```
Constants类中定义了项目的一系列常量参数。

2.`city,java`
-----------

```java
public class City {
    private int centerX;
    private int centerY;

    public City(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }
}
```
City控制点的显示区域相关属性

`centerX`及`centerY`控制人口显示区域的中心位置坐标。

3.`Point.java`
--------------
```java
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
```

Point类控制人的位置坐标，结构与City类类似。

4.`Bed.java`
------------

```java
public class Bed extends Point{
    public Bed(int x, int y) {
        super(x, y);
    }
    private boolean isEmpty=true;

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}

```

Bed类继承了Point类，子类将继承父类的非私有的属性和方法。

5.`Hospital.java`
-----------------

```java
public class Hospital {
    private int x=800;
    private int y=110;
    private int width;
    private int height=606;
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private static Hospital hospital = new Hospital();
    public static Hospital getInstance(){
        return hospital;
    }
    private Point point = new Point(800,100);
    private List<Bed> beds = new ArrayList<Bed>();

    private Hospital() {
        if(Constants.BED_COUNT==0){
            width=0;
            height=0;
        }
        int column = Constants.BED_COUNT/100;
        width = column*6;

        for(int i=0;i<column;i++){

            for(int j=10;j<=610;j+=6){
                Bed bed = new Bed(point.getX()+i*6,point.getY()+j);
                beds.add(bed);

            }

        }
    }

    public Bed pickBed(){
        for(Bed bed:beds){
            if(bed.isEmpty()){
                return bed;
            }
        }
        return null;
    }
}
```

Hospital类用来控制界面右侧代表医院的绿色矩形图形绘制；

其中，成员变量`x`和`y`控制矩形框左上角顶点的位置，其初始位置为`（800，100）`，`width`和`height`控制矩形框的宽和高；初始化了一个Point实例`point`和由Bed类实例构成的链表`beds`。

6.`PersonPool.java`
-------------------

```java
public class PersonPool {
    private static PersonPool personPool = new PersonPool();
    public static PersonPool getInstance(){
        return personPool;
    }

    List<Person> personList = new ArrayList<Person>();

    public List<Person> getPersonList() {
        return personList;
    }

    private PersonPool() {
        City city = new City(400,400);
        for (int i = 0; i < 5000; i++) {
            Random random = new Random();
            int x = (int) (100 * random.nextGaussian() + city.getCenterX());
            int y = (int) (100 * random.nextGaussian() + city.getCenterY());
            if(x>700){
                x=700;
            }
            Person person = new Person(city,x,y);
            personList.add(person);
        }
    }
}
```

PersonPool类用标准正态分布模型人群与人的流动意向均按照正态分布模拟；

新建由Person类的实例构成的链表及链表返回方法，后续把所有“人”都保存进来；

PersonPool构造器方法中实例化了一个参数为（400，400）的城市（参数（400，400）为点分布的中心坐标），用for循环向其中添加随机点。调用`random.nextGaussian()`方法生成符合标准正态分布的伪随机数，float类型。

7.`MoveTarget.java`
-------------------

```java
public class MoveTarget {
    private int x;
    private int y;
    private boolean arrived=false;

    public MoveTarget(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }
}
```

MoveTarget类同样是对坐标X与Y的相关操作，定义了一个boolean类的成员变量`isArrived`来指示点是否已经移动。

8.`Person.java`
---------------

### 基本属性

```java

    private City city;
    private int x;
    private int y;
    private MoveTarget moveTarget;
    int sig=1;


    double targetXU;
    double targetYU;
    double targetSig=50;


    public interface State{
        int NORMAL = 0;
        int SUSPECTED = NORMAL+1;
        int SHADOW = SUSPECTED+1;

        int CONFIRMED = SHADOW+1;
        int FREEZE = CONFIRMED+1;
        int CURED = FREEZE+1;
    }
```

第一个成员方法为接口State，定义了点的当前感染状态。这样做的好处在于使得表示每种状态的常量值在任意时刻都不相同。

### 构造器

```java
public Person(City city, int x, int y) {
        this.city = city;
        this.x = x;
        this.y = y;
        targetXU = 100*new Random().nextGaussian()+x;
        targetYU = 100*new Random().nextGaussian()+y;

    }
```

### 辅助方法

```java
public boolean wantMove(){
        double value = sig*new Random().nextGaussian()+ Constants.u;
        return value>0;
    }

    private int state=State.NORMAL;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    int infectedTime=0;
    int confirmedTime=0;
    public boolean isInfected(){
        return state>=State.SHADOW;
    }
    public void beInfected(){
        state = State.SHADOW;
        infectedTime= MyPanel.worldTime;
    }

    public double distance(Person person){
        return Math.sqrt(Math.pow(x-person.getX(),2)+Math.pow(y-person.getY(),2));
    }

    private void freezy(){
        state = State.FREEZE;
    }
    private void moveTo(int x,int y){
        this.x+=x;
        this.y+=y;
    }
```

`wantMove`:Constants中定义了一个流动意向参数，通过参数计算出value的值，若为正则移动，其中流动意向参数的取值范围为[-1,1]。

`distance`：计算坐标(x,y)与(person.getX(),person.getY())之间的距离。

`moveTo`：改变实例person的坐标位置。

### action方法
```java
private void action(){
        if(state==State.FREEZE){
            return;
        }
        if(!wantMove()){
            return;
        }
        if(moveTarget==null||moveTarget.isArrived()){

            double targetX = targetSig*new Random().nextGaussian()+targetXU;
            double targetY = targetSig*new Random().nextGaussian()+targetYU;
            moveTarget = new MoveTarget((int)targetX,(int)targetY);

        }


        int dX = moveTarget.getX()-x;
        int dY = moveTarget.getY()-y;
        double length=Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2));

        if(length<1){
            moveTarget.setArrived(true);
            return;
        }
        int udX = (int) (dX/length);
        if(udX==0&&dX!=0){
            if(dX>0){
                udX=1;
            }else{
                udX=-1;
            }
        }
        int udY = (int) (dY/length);
        if(udY==0&&udY!=0){
            if(dY>0){
                udY=1;
            }else{
                udY=-1;
            }
        }

        if(x>700){
            moveTarget=null;
            if(udX>0){
                udX=-udX;
            }
        }
        moveTo(udX,udY);




    }
```

控制点的一次移动；

计算源点与目标点的距离，当距离小于1像素时，认为已经移动到目标点，重新计算目标。

### update方法

```java
public void update(){
        //@TODO找时间改为状态机
        if(state>=State.FREEZE){
            return;
        }
        if(state==State.CONFIRMED&&MyPanel.worldTime-confirmedTime>=Constants.HOSPITAL_RECEIVE_TIME){
            Bed bed = Hospital.getInstance().pickBed();
            if(bed==null){
                System.out.println("隔离区没有空床位");
            }else{
                state=State.FREEZE;
                x=bed.getX();
                y=bed.getY();
                bed.setEmpty(false);
            }
        }
        if(MyPanel.worldTime-infectedTime>Constants.SHADOW_TIME&&state==State.SHADOW){
            state=State.CONFIRMED;
            confirmedTime = MyPanel.worldTime;
        }

        action();

        List<Person> people = PersonPool.getInstance().personList;
        if(state>=State.SHADOW){
            return;
        }
       for(Person person:people){
           if(person.getState()== State.NORMAL){
               continue;
           }
           float random = new Random().nextFloat();
           if(random<Constants.BROAD_RATE&&distance(person)<SAFE_DIST){
               this.beInfected();
           }
       }
    }
}
```

当目前点状态为CONFIRMED并且当前距离确诊时间已超过医院收治响应时间时，对bed进行获取床位的操作：剩余床位已经空时print床位不足提示；若还有床位则获取“床位点”的坐标，占领床位，并将点送入隔离区；

当点处于潜伏状态且目前时刻与被感染时刻之差超过潜伏期，状态更改为CONFIRMED，并更新确诊时间；

调用action方法完成点的移动；

在当前点状态未被感染时对people链表中的所有点遍历，寻找当前时刻每一个被感染点的坐标，计算这个点与当前点的距离，若小于安全距离则当前点被感染。

### 总结

点的更新与移动相互配合

针对当前时刻的所有点计算下一帧的移动目标点并移动；

将已确诊的点送去医院隔离；

针对当前时刻的所有已经感染的点继续对其周围小于安全距离的正常点进行感染；

完成后保存所有点的状态，待下一帧刷新时将所有点绘制在画面上。

9.`MyPanel.java`
----------------

```java
public class MyPanel extends JPanel implements Runnable {


   private int pIndex=0;

    public MyPanel() {
        this.setBackground(new Color(0x444444));
    }



    @Override
    public void paint(Graphics arg0) {
        super.paint(arg0);
        //画出医院的边框
        arg0.setColor(new Color(0x00ff00));
        arg0.drawRect(Hospital.getInstance().getX(),Hospital.getInstance().getY(),
                Hospital.getInstance().getWidth(),Hospital.getInstance().getHeight());



        List<Person> people = PersonPool.getInstance().getPersonList();
        if(people==null){
            return;
        }
        people.get(pIndex).update();
        for(Person person:people){

            switch (person.getState()){
                case Person.State.NORMAL:{
                    arg0.setColor(new Color(0xdddddd));

                }break;
                case Person.State.SHADOW:{
                    arg0.setColor(new Color(0xffee00));

                }break;
                case Person.State.CONFIRMED:
                case Person.State.FREEZE:{
                    arg0.setColor(new Color(0xff0000));

                }break;
            }
            person.update();
            arg0.fillOval(person.getX(), person.getY(), 4, 4);

        }
        pIndex++;
        if(pIndex>=people.size()){
            pIndex=0;
        }
    }

    public static int worldTime=0;
    public void run() {
        while (true) {

            this.repaint();//画面刷新

            try {
                Thread.sleep(100);
                worldTime++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
```

MyPanel类用来控制GUI图形界面，继承JPanel类并实现Runnable接口进行多线程操作

10.`Main.java`
--------------

```java
public class Main {
 public static void main(String[] args) {
        //初始化创建新面板
        MyPanel p = new MyPanel();
        Thread panelThread = new Thread(p);
        //构造一个初始时不可见的新窗体。
        JFrame frame = new JFrame("新冠病毒在人群中传播仿真模拟");
        frame.add(p);
        //设置窗口的属性 窗口位置以及窗口的大小
        frame.setSize(1000, 1000);
        frame.setLocationRelativeTo(null);
        //设置窗口可见
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelThread.start();//启动线程实现点位更新

        List<Person> people = PersonPool.getInstance().getPersonList();
        for(int i = 0; i< Constants.ORIGINAL_COUNT; i++){
            int index = new Random().nextInt(people.size()-1);
            Person person = people.get(index);

            while (person.isInfected()){
                index = new Random().nextInt(people.size()-1);
                person = people.get(index);
            }
            person.beInfected();

        }


    }
}
```
对初始感染人数进行遍历，初始化这些人“被感染进入潜伏期”。



3.结果展示
==========

1.病毒爆发初期，人员自由流动，无佩戴口罩的习惯，也没有专门的隔离医院：

![疫情初期](https://user-images.githubusercontent.com/77970177/109804560-57192300-7c5d-11eb-8961-4568edfb039c.png)


2.当意识到疫情的严重性，国家建立了隔离医院后，疫情在一定程度上得到了控制，但当隔离病床注满后，疫情将再次变得不可控：

![仅建造医院](https://user-images.githubusercontent.com/77970177/109804605-639d7b80-7c5d-11eb-9de7-753b84e6893c.png)


3.当每一个都意识到疫情的严重性，并减少不必要的外出且外出佩戴口罩时，疫情最终得到了有效地控制：

![减少流动](https://user-images.githubusercontent.com/77970177/109804640-70ba6a80-7c5d-11eb-8111-46e0aa96df75.png)








