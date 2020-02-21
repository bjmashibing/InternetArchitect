package com.mashibing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//@RestController
public class JDBCController {

//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    @RequestMapping("selectAll")
//    public List<Map<String,Object>> selectAll(){
//        String sql = "select * from emp";
//        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
//        return maps;
//    }
//
//    @RequestMapping("addEmp")
//    public String addEmp(){
//        String sql = "insert into emp(empno,ename) values(1234,'zhangsan')";
//        int update = jdbcTemplate.update(sql);
//        System.out.println(update);
//        return "add_success";
//    }
//
//    @RequestMapping("updateEmp/{empno}")
//    public String updateEmp(@PathVariable("empno") int empno){
//        String sql = "update emp set ename=? where empno ="+empno;
//        String ename = "heheda";
//        int update = jdbcTemplate.update(sql, ename);
//        System.out.println(update);
//        return "update_success";
//    }
//
//    @RequestMapping("deleteEmp/{empno}")
//    public String deleteEmp(@PathVariable("empno")int empno){
//        String sql = "delete from emp where empno="+empno;
//        int update = jdbcTemplate.update(sql);
//        System.out.println(update);
//        return "delete_success";
//
//    }
}
