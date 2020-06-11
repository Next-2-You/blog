package com.chen.blog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chen.blog.entity.Blog;
import com.chen.blog.entity.User;
import com.chen.blog.repository.BlogRepository;
import com.chen.blog.repository.UserRepository;
import com.chen.blog.service.BlogService;
import com.chen.blog.utils.OthersUtils;
import com.chen.blog.utils.SensitivewordFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;


import java.time.LocalDateTime;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTests {

    @Test
    public void contextLoads() {
    }

//    @Autowired
//    private TestUserRepository testUserRepository;


/*    @Test
    public void test01(){
        final TestUser testUser = new TestUser(null, "lao", "123456", "广东揭阳");
        testUserRepository.save(testUser);


    }*/

/*    @Test
    public void test02(){
        final List<TestUser> list = testUserRepository.getAllByUsernameLike("%lao%");
        for (TestUser testUser : list) {
            System.out.println(testUser);
        }

    }*/

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test03(){
        //final User user = userRepository.getOne(1L);//非懒加载字段也有可能出现异常
        final User user = userRepository.findById(1L).get();

        String password = user.getPassword();
//        String username = user.getUsername();
//        System.out.println(username);
        System.out.println(password);

        System.out.println(user);

//        Blog blog = user.getBlog();
//        System.out.println(blog.getBlogName());

//        final Optional<User> optionalUser = userRepository.findById(2L);
//        System.out.println(optionalUser.isPresent());


    }

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    @Transactional
    public void test04(){
        Blog blog = blogService.getById(5l);
        LocalDateTime createTime = OthersUtils.getCreateTime();
        int i = blogRepository.updateBlogInfo(createTime,"boke","hhh",blog.getId());
    }

    @Test
    public void test05() throws NoSuchFieldException, IllegalAccessException {
        String userStr = "User(id=5, account=52968490645, password=null, phone=13556518175, sex=null, birthday=null, email=null, briefIntr=好的喔, nickname=快递546, headurl=group1/M00/00/00/wKi4eFy6MfCAU-boAAAX153IpA8220.png, createtime=null, updatetime=null, deleteSign=null, lockSign=null, viewSum=null, goodSum=null, commentSum=null, attentionList=null, blogName=null, description=null)";
        String[] fields = userStr.replace("[User(]", "")
                .replace("[)]", "")
                .split(", ");
        JSONObject jsonObj = new JSONObject();
        for (String field : fields) {
            if(field.contains("=null")){
                continue;
            }
            String[] s = field.split("=");
            jsonObj.put(s[0],s[1]);
        }
        String json = jsonObj.toJSONString();
        User user = JSON.parseObject(json, User.class);
        String account = user.getAccount();
        System.out.println(account);
        

//        String userJson = user.replaceAll("=", "\":\"")
//                .replaceAll(",","\",\"")
//                .replaceAll("[(]", "{\"")
//                .replaceAll("[)]", "\"}")
//                .replaceAll("User", "")
//                .replaceAll("\"null\"","null");
//        User user2 = JSON.parseObject(userJson, User.class);
//        JSONObject jsonObject = JSON.parseObject(userJson);
//        String account = jsonObject.getString("account");
//        Object o = jsonObject.get("account");
//        Class<User> clazz = User.class;
//        Field field = clazz.getDeclaredField("id");
//        field.setAccessible(true);
//
//        User userhh = new User();
//        field.set(userhh,5l);
//        System.out.println(userhh);
        

    }

    @Test
    public void test(){
        String str = "基本框架搭好的前提下。\n" +
                "\n" +
                "#### 一、普通springmvc文件上传\n" +
                "1.导jar包\n" +
                "```\n" +
                "<dependency>\n" +
                "\t<groupId>commons-fileupload</groupId>\n" +
                "\t<artifactId>commons-fileupload</artifactId>\n" +
                "\t<version>1.3</version>\n" +
                "</dependency>\n" +
                "```\n" +
                "2.在springmvc配置文件中配置文件上传解析器\n" +
                "```\n" +
                "<bean id=\"multipartResolver\" class=\"org.springframework.web.multipart.commons.CommonsMultipartResolver\">\n" +
                "<!--指定文件上传的总大小不超过20M-->\n" +
                "<property name=\"maxUploadSize\" value=\"20000000\"/>\n" +
                "<property name=\"defaultEncoding\" value=\"utf-8\"/>\n" +
                "</bean>\n" +
                "```\n" +
                "3.表单修改成enctype=“multipart/form-data”\n" +
                "```\n" +
                "<form action=\"/TestProblem/test2\" enctype=\"multipart/form-data\" method=\"post\">\n" +
                "\t<input type=\"file\" name=\"file\"></input>\n" +
                "\t<input type=\"submit\" >普通文件上传提交</input>\n" +
                "</form>\n" +
                "```\n" +
                "4.Controller\n" +
                "```\n" +
                "@RequestMapping(value=\"/test2\",method=RequestMethod.POST)\n" +
                "public String test2(MultipartFile file) {\n" +
                "System.out.println(\"测试文件上传\");\n" +
                "if(!file.isEmpty()) {\n" +
                "System.out.println(\"文件不为空\");\n" +
                "String originalFilename = file.getOriginalFilename();\n" +
                "System.out.println(originalFilename);\n" +
                "}\n" +
                "return \"index2\";\n" +
                "}\n" +
                "```\n" +
                "一切正常。。。。\n" +
                "#### 二、Restful风格\n" +
                "1.在web.xml文件中配置HiddenHttpMethodFilter过滤器\n" +
                "```\n" +
                "<filter>\n" +
                "<filter-name>HiddenHttpMethodFilter</filter-name>\n" +
                "<filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>\n" +
                "</filter>\n" +
                "<filter-mapping>\n" +
                "<filter-name>HiddenHttpMethodFilter</filter-name>\n" +
                "<url-pattern>/*</url-pattern>\n" +
                "</filter-mapping>\n" +
                "```\n" +
                "2.在表单域中需要携带一个name值为_method，value值为put或者delete的参数，发送post请求\n" +
                "```\n" +
                "<form action=\"/TestProblem/test3\" enctype=\"application/x-www-form-urlencoded\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"_method\" value=\"PUT\">\n" +
                "\t用户名：<input type=\"text\" name=\"username\"></input>\n" +
                "\t密码：<input type=\"password\" name=\"password\"></input>\n" +
                "\t<input type=\"submit\">restful风格提交</input>\n" +
                "</form>\n" +
                "```\n" +
                "3.Controller\n" +
                "```\n" +
                "@RequestMapping(value=\"/test3\", method=RequestMethod.PUT)\n" +
                "public String testPut(String username,String password) {\n" +
                "System.out.println(username);\n" +
                "System.out.println(password);\n" +
                "return \"index2\";\n" +
                "}\n" +
                "```\n" +
                "遇到JSPs only permit GET POST or HEAD错误，\n" +
                "这是因为Tomcat7升级到Tomcat8后不支持delete或者put的方法，解决这个问题后\n" +
                "\n" +
                "一切正常。。。。\n" +
                "#### 三、当springmvc文件上传遇上Restful风格\n" +
                "1.表单改为enctype=\"multipart/form-data\"并加上_method\n" +
                "```\n" +
                "<form action=\"/TestProblem/test4\" enctype=\"multipart/form-data\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"_method\" value=\"PUT\">\n" +
                "\t<input type=\"file\" name=\"file\"></input>\n" +
                "\t<input type=\"submit\" >PUT文件上传提交</input>\n" +
                "</form>\n" +
                "```\n" +
                "2.Controller的mapping改为put方式\n" +
                "```\n" +
                "@RequestMapping(value=\"/test4\",method=RequestMethod.PUT)\n" +
                "public String test4(MultipartFile file) {\n" +
                "if(!file.isEmpty()) {\n" +
                "String originalFilename = file.getOriginalFilename();\n" +
                "System.out.println(originalFilename);\n" +
                "}\n" +
                "return \"index2\";\n" +
                "}\n" +
                "```\n" +
                "重点来了，出现错误\n" +
                "### HTTP Status 405 - Request method ‘POST’ not supported\n" +
                ">当使用RESTFUL风格，表达的方法为非GET以及POST时，需要使用HiddenHttpMethodFilter来对隐藏的方法进行解析。通常就是直接调用getParameter方法解析出隐藏域来值，然后传给Controller就行了。\n" +
                "但是，当表单中有文件上传时，表单实际上变为了一个输入流，需要进过特殊处理（解析）后，才能被HiddenHttpMethodFilter所识别，然后确定方法类型。而解析输入流的东西，就是CommonsMultipartResolver。\n" +
                "使用SpringMVC时，通常在配置文件中配置CommonsMultipartResolver为一个Bean，然后它完成对Form的解析，解析出普通的上传域以及MultipartFile类型的上传域（也就是file），自动绑定到相应的类中。\n" +
                "如果在Form中，隐藏了name=\"_method\" value=\"PUT/DELETE/…“这个域，我们原本希望HiddenHttpMethodFilter来解析为指定的方法，但是，此时必须先经过CommonsMultipartResolver解析出相应的域后，HiddeHttpMethodFilter才能找到name=”_method\"这个隐藏域，进行处理。\n" +
                "SpringMVC的执行流程中，过滤器起作用，比如HiddenHttpMethodFilter过滤器配置的拦截路径为所有的请求，那么它会在请求发送到DispatcherServlet的时候，就进行解析。但是没有经过CommonsMultipartResolver的解析（它是在DisptacherServlet通过HandlerAdapter来调用相应Handler的过程中，进行参数的解析绑定时才起作用的），此时HidedenHttpMethodFilter解析不出来什么鸟东西，请求的方法还是为post方法，就会找不到相应的Handler。\n" +
                "\n" +
                "![](http://192.168.184.120:8888/group1/M00/00/00/wKi4eFy9DQaAJtrWAANwi9vGSmU006.png)\n" +
                "怎么办呢？我们可以在HiddenHttpMethodFilter之前，再配置一个Filter，先把参数解析了不就好了嘛。\n" +
                "\n" +
                "于是乎就配置一个MultipartFilter这个过滤器，指定一个初始化参数：multipartResolverBeanName，它的值为我们在spirng配置文件中配置好的CommonsMultipartResolver这个Bean的id就好了。\n" +
                "```\n" +
                "<filter>\n" +
                "\t<filter-name>MultipartFilter</filter-name>\n" +
                "\t<filter-class>org.springframework.web.multipart.support.MultipartFilter</filter-\tclass>\n" +
                "\t<init-param>\n" +
                "\t\t<param-name>multipartResolverBeanName</param-name>\n" +
                "\t\t<!--对应文件上传解析器的id -->\n" +
                "\t\t<param-value>multipartResolver</param-value>\n" +
                "\t</init-param>\n" +
                "</filter>\n" +
                "<filter-mapping>\n" +
                "\t<filter-name>MultipartFilter</filter-name>\n" +
                "\t<url-pattern>/*</url-pattern>\n" +
                "</filter-mapping>\n" +
                "```\n" +
                "继续尝试，出现错误。\n" +
                "\n" +
                "### HTTP Status 500 - Could not parse multipart servlet request; nested exception is java.lang.IllegalStateException: Unable to process parts as no multi-part configuration has been provided\n" +
                "\n" +
                "解析MultipartFilter的源码\n" +
                "```\n" +
                "protected MultipartResolver lookupMultipartResolver() {\n" +
                "\tWebApplicationContext wac = \t\t\tWebApplicationContextUtils.getWebApplicationContext(getServletContext());\n" +
                "\tString beanName = getMultipartResolverBeanName();\n" +
                "\tif (wac != null && wac.containsBean(beanName)) {//false\n" +
                "\t\tif (logger.isDebugEnabled()) {\n" +
                "\t\t\tlogger.debug(\"Using MultipartResolver '\" + beanName + \"' for \tMultipartFilter\");\n" +
                "\t\t}\n" +
                "\t\treturn wac.getBean(beanName, MultipartResolver.class);\n" +
                "\t}\n" +
                "\telse {\n" +
                "\t\treturn this.defaultMultipartResolver;//运行时进入了这里面\n" +
                "\t}\n" +
                "}\n" +
                "```\n" +
                "发现MultipartFilter在进行过滤的时候，调用的Bean，是在Spring的根容器中配置的Bean。我们配置的普通的Spirng配置文件，只是DispatcherServlet的配置文件，并不是根容器的配置文件。(其实DispatcherServlet只是一个普通Servlet，我们可以简单的认为它也有一个自己的上下文环境，起着Spring容器的作用)。\n" +
                "\n" +
                "这里简单说一下，上下文环境(context)，有ServletContext、DispatcherServlet的上下文、以及spring根上下文webApplicationContext。在web项目中，ServletContext包含着webApplicationContext以及Dispatcher的Context，同时，web Application Context也是Dispatcher的context的父容器。\n" +
                "\n" +
                "spring在执行ApplicationContext的个体Bean时，如果在自己的context上下文中找不到Bean，就会到父容器中去找。但是父容器不会找子容器。\n" +
                "\n" +
                "所以我们需要把文件上传解析器从springmvc配置文件中转义到spring的配置文件中。\n" +
                "\n" +
                "也就是在web.xml中监听启动的spring文件。\n" +
                "\n" +
                "转移好后，\n" +
                "\n" +
                "一切OK。。。。\n" +
                "\n";

        System.out.println(str);
        String s = HtmlUtils.htmlEscape(str);
        System.out.println(s);
        String s1 = HtmlUtils.htmlUnescape(s);
        System.out.println(s1);
        String str2 = HtmlUtils.htmlUnescape(str);
        System.out.println(str2);

        

    }

    @Autowired
    private SensitivewordFilter sensitivewordFilter;

    @Test
    public void test2(){
        System.out.println("敏感词的数量：" + sensitivewordFilter.getSensitiveWordMap().size());
        String string = "太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = sensitivewordFilter.getSensitiveWord(string, 2);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
        beginTime = System.currentTimeMillis();
        String str = sensitivewordFilter.replaceSensitiveWord(string, 2, "*");
        System.out.println(str);
        endTime = System.currentTimeMillis();
        System.out.println("总共消耗时间为：" + (endTime - beginTime));

    }


}
