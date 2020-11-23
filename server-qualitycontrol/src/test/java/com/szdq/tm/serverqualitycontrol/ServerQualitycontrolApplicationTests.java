package com.szdq.tm.serverqualitycontrol;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@SpringBootTest()
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServerQualitycontrolApplication.class)
class ServerQualitycontrolApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Test
    void testDataSource() throws Exception{
        System.out.println(dataSource.getConnection());
    }

    @Test
    void doTest(){

    }

}
