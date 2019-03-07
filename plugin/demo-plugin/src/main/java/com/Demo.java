package com;

import lombok.extern.slf4j.Slf4j;
import spi.IDemo;

/**
 * Description:
 *
 * @author: baiyu
 * Version: 1.0
 * Create Date Time: 2019-03-06 14:06
 * Update Date Time:
 */
@Slf4j
public class Demo implements IDemo {
    public Demo() {
        log.info("\n本类的装载器：{}", this.getClass().getClassLoader().toString());
        log.info("\nSPI类的装载器：{}", IDemo.class.getClassLoader().toString());
    }

    public void show() {

    }

    public static void main(String[] args) {

    }
}
