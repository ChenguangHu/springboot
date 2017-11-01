package com.geb.interceptor;

import com.geb.entity.User;
import com.geb.util.ApplicationException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class UserInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(UserInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object o) throws ApplicationException, IOException, ServletException {
        User user=(User)req.getSession().getAttribute("user");
        String reqUrl = req.getServletPath();
        if(reqUrl.equals("/index")||reqUrl.equals("/addUser")||reqUrl.equals("/getUser")){
            return true;
        }
        if(user == null){
            req.getRequestDispatcher("/index").forward(req, res);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, ModelAndView mav) throws ApplicationException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterCompletion(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, Exception excptn) throws ApplicationException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
