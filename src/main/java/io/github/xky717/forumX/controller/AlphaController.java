package io.github.xky717.forumX.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHallo(){

        return "Hallo Spring Boot.";
    }
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //request data
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));
         //return response data
        response.setContentType("text/html;charset= utf-8");
        try ( PrintWriter writer= response.getWriter();){
            writer.write("<hl>牛客网<hl>");
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    //GET请求
    //student?current=1&limit=20
    @RequestMapping(path="/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "current" ,required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit" ,required = false, defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit );
        return "some students";
    }
    //一个学生 student/123

    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id  ){
        System.out.println(id);
        return "a student";
    }

    //post请求
    @RequestMapping(path = "student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent( String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
  //响应html数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","Xky");
        modelAndView.addObject("age","30");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","tu do");
        model.addAttribute("age","80");
        return "/demo/view";
    }
    //响应json(异步请求)
//Java对象 -> JSON字符串 ->JS对象
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>>  getEmps(){
        List<Map<String, Object>>  list= new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","Xky");
        emp.put("age",31);
        emp.put("salary",3900);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","Xkyy");
        emp.put("age",322);
        emp.put("salary",31100);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","Xkky");
        emp.put("age",3111);
        emp.put("salary",23900);
        list.add(emp);
        return list;
        //JSON 字符串 {"name":"Xky","salary":3900,"age":31}
    }


}
