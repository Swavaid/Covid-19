package com.leishenshan;

import com.leishenshan.constant.Constants;
import com.leishenshan.entity.Person;
import com.leishenshan.entity.PersonPool;

import javax.swing.*;//图形用户界面
import java.util.List;//集合类
import java.util.Random;//产生随机数

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
