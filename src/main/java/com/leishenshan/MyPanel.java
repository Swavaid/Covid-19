package com.leishenshan;

import com.leishenshan.entity.Hospital;
import com.leishenshan.entity.Person;
import com.leishenshan.entity.PersonPool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
 * Java Swing JFrame和JPanel：窗口容器和面板容器
 */

//设置面板色块，其中白色为安全，黄色为潜伏，红色为感染
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

