package com;

import lombok.extern.slf4j.Slf4j;
import spi.IDemo;

/**
 * Description:
 *
 * @author: Tom
 * Version: 1.0
 * Create Date Time: 2019-03-06 14:06
 * Update Date Time:
 */
@Slf4j
public class Demo implements IDemo {
    public Demo() {
        log.info("\n本类的装载器：{}", this.getClass().getClassLoader().toString());
        log.info("\nSPI类的装载器：{}", IDemo.class.getClassLoader().toString());
        log.info("\nOtherClass类的装载器：{}", OtherClass.class.getClassLoader().toString());
        log.info("\nInternal类的装载器：{}", Internal.class.getClassLoader().toString());
    }

    @Override
    public void show() {

    }

    static class Internal{

    }

}


