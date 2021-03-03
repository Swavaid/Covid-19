1.代码出处
==========

本代码参考改造于“CSDN咨询”https://blog.csdn.net/csdnnews/article/details/104218200 ；

参考2020年2月6日由人民日报公众号转发了一则由Ele实验室制作的疫情传播仿真程序。

2.代码思路
==========

1.Constants.java
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
Constants类中定义了项目的一系列常量参数其中。

2.city,java
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



3.将城市中人群的种类分为三种，健康（白色），潜伏期（黄色）和患者（红色）。人群与人的流动意向均按照正态分布模拟；

3.通过设置一系列的参数实现对病毒控制的不同手段，其中包括病毒的传播概率（可通过戴口罩、保持社交距离而降低），医院所能容纳患者的隔离病床数目（参照我国建立的方舱医院及雷神山火神山医院）以及人口流动意向平均值（减少不必要的外出和聚会）；

4.通过控制各参数和变量，观察图示中被感染者数目密集程度从而实现对新冠病毒的传播模拟。

3.结果展示
==========

1.病毒爆发初期，人员自由流动，无佩戴口罩的习惯，也没有专门的隔离医院：

![疫情初期](https://user-images.githubusercontent.com/77970177/109767594-bca3ea00-7c32-11eb-8757-854efeb68c27.jpg)

2.当意识到疫情的严重性，国家建立了隔离医院后，疫情在一定程度上得到了控制，但当隔离病床注满后，疫情将再次变得不可控：

![仅建造医院](https://user-images.githubusercontent.com/77970177/109769243-e3fbb680-7c34-11eb-9e47-5da4c9f06d95.jpg)

3.当每一个都意识到疫情的严重性，并减少不必要的外出且外出佩戴口罩时，疫情最终得到了有效地控制：

![减少流动](https://user-images.githubusercontent.com/77970177/109771476-cda32a00-7c37-11eb-9b3a-63cb88e82158.jpg)







