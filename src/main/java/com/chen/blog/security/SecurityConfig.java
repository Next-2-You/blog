package com.chen.blog.security;

import com.alibaba.fastjson.JSON;
import com.chen.blog.common.CodeEnum;
import com.chen.blog.vo.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//启动方法级别的权限控制
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    UserDetailService userDetailService;
    @Autowired
    BlogSecurityProvider blogSecurityProvider;

    @Autowired
    private RedisTokenRepository redisTokenRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint((request,response,e)->{
                    //未登陆
                    combinRepVo(HttpServletResponse.SC_FORBIDDEN,CodeEnum.LOGIN_NO,response,e);
                })
                .and()
                .authorizeRequests()
                .antMatchers("/article/changeOverhead","/article/changeType","/article/delete/**","/edit/**","/manage","/comment/reply","/sort/add","/sort/user",
                        "/sort/delete/**","/sort/choice","/article/add",
                        "/article/check/**","/article/update","/center/**","/article/user","/user/update","/good/add/**","/good/delete/**",
                        "/collection/**","/user/phone").authenticated()//任何经过身份验证的用户才能访问
                .and()
                .formLogin()
                .loginPage("/logoreg")
                .loginProcessingUrl("/logoreg")
                .permitAll()
                .failureHandler((request,response,e)->{
                    //登陆失败
                    combinRepVo(HttpServletResponse.SC_UNAUTHORIZED,CodeEnum.LOGIN_FAIL,response,e);
                })
                .successHandler((request,response,auth)->{
                    //登陆成功
                    combinRepVo(HttpServletResponse.SC_OK,CodeEnum.LOGIN_SUCCESS,response,null);
                })
                .and()
//                .exceptionHandling()
//                .accessDeniedHandler((request,response,ex) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    PrintWriter out = response.getWriter();
//                    Map<String,Object> map = new HashMap<String,Object>();
//                    map.put("code",403);
//                    map.put("message", "权限不足");
//                    out.write(objectMapper.writeValueAsString(map));
//                    out.flush();
//                    out.close();
//                })
//                .and()
                .logout()
                .deleteCookies()
                .logoutSuccessHandler((request,response,auth)->{
                    //注销成功
                    combinRepVo(HttpServletResponse.SC_OK,CodeEnum.LOGOUT_SUCCESS,response,null);
                })
                .invalidateHttpSession(true)
                .permitAll();
        //开启跨域访问
//        http.cors().disable();
        //开启模拟请求，比如API POST测试工具的测试，不开启时，API POST为报403错误
        http.csrf().disable();

        http.rememberMe()
                .rememberMeParameter("remberme")
                .tokenRepository(redisTokenRepository);


        http.headers().frameOptions().sameOrigin();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        //自定义用户权限信息类
        return userDetailService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //自定义用户登录校验类 (认证)
        auth.authenticationProvider(blogSecurityProvider);
    }


    public void combinRepVo(int status,CodeEnum code,HttpServletResponse response,AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        RespVo<Object> respVo = RespVo.general(code, e == null?null:e.getMessage(), null);
        String json = JSON.toJSONString(respVo);
        out.write(json);
        out.flush();
        out.close();
    }

}
