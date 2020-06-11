package com.chen.blog.security;

import com.alibaba.fastjson.JSON;
import com.chen.blog.common.WordDefined;
import com.chen.blog.entity.User;
import com.chen.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 自定义登录校验服务
 */
@Service("blogSecurityProvider")
public class BlogSecurityProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String loginNum = token.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginNum);
        if (!userDetails.isEnabled()) {
            throw new DisabledException("账号已被删除");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("账号已被冻结");
        }

        String password = userDetails.getPassword();//数据库密码
        // 与authentication里面的credentials相比较
        //加盐
        String loginPwd = WordDefined.PSWD_SALT + token.getCredentials().toString();
        if (!password.equals(DigestUtils.md5DigestAsHex(loginPwd.getBytes()))) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        // 授权
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }


    @Override
    public boolean supports(Class<?> authentication) {
        // 返回true后才会执行上面的authenticate方法,这步能确保authentication能正确转换类型
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
