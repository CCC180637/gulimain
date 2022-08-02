package com.ccc.gulimallcart.interceptor;


import com.ccc.common.constant.AuthServerConstant;
import com.ccc.common.vo.MemberRespVo;
import com.ccc.gulimallcart.vo.UserInfoTo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class CartInterceptor implements HandlerInterceptor {


    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        ObjectMapper mapper = new ObjectMapper();
        /* 在ObjectMapper对象设置忽略多余属性 */
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MemberRespVo member =mapper.convertValue(session.getAttribute(AuthServerConstant.LOGIN_USER), MemberRespVo.class);

        if (member != null) {
            userInfoTo.setUserId(member.getId());
        }
        threadLocal.set(userInfoTo);
        return true;
    }
}
