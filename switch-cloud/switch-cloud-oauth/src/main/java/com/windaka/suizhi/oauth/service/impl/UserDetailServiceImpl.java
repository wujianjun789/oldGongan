package com.windaka.suizhi.oauth.service.impl;

import com.windaka.suizhi.api.common.ReturnConstants;
import com.windaka.suizhi.api.user.LoginAppUser;
import com.windaka.suizhi.oauth.feign.UserClient;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 根据用户名获取用户<br>
 * <p>
 * 密码校验请看下面两个类
 * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 */
@Slf4j
@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        //为了后期可以支持多类型登录，这里username后面拼装上登录类型,如username&type&password
        //用户名密码登录的，将password也拼在里面返回
        int firstParamsIndex = username.indexOf("|");
        int lastParamsIndex = username.lastIndexOf("|");
        String realUsername = null;
        String password = null;
        //真正的登录名为第一个|之前的部分
        if(firstParamsIndex>=0) {
            realUsername = username.substring(0, firstParamsIndex);
        }
        if(firstParamsIndex != lastParamsIndex) {
            password = username.substring(firstParamsIndex+1,lastParamsIndex);
        }
        Map<String,Object> loginAppUserMap = userClient.queryByUsername(realUsername,password);

        LoginAppUser loginAppUser = null;

        //如果用户不存在抛出身份验证异常
        if(loginAppUserMap==null){
            throw new AuthenticationServiceException(
                    ReturnConstants.CODE_LOGIN_FAILEED_USER_OR_PWD_BAD+"|^|^|"+ReturnConstants.MSG_LOGIN_FAILEED_USER_OR_PWD_BAD);
        }else{
            //不为空，并且返回map中的status=1000，则正常执行
            if(ReturnConstants.STATUS_SUCCESS.equals(loginAppUserMap.get("status"))){
                loginAppUser = JSONObject.parseObject(JSONObject.toJSONString(loginAppUserMap.get("data")), LoginAppUser.class);
            }else{ //返回status=0000 则说明存在校验异常，直接终止执行程序即可
                String code = loginAppUserMap.get("code").toString();
                String msg = loginAppUserMap.get("msg").toString();
                throw new AuthenticationServiceException(code + "|^|^|" + msg);
            }
        }

        /*
        if (firstParamsIndex == lastParamsIndex) {
            // 登录类型
            String loginType = username.substring(lastParamsIndex+1);
            String[] params = new String[]{realUsername,loginType};
            CredentialType credentialType = CredentialType.valueOf(loginType);
            if (CredentialType.PHONE == credentialType) {// 短信登录 后期如果有需要将会启用
                handlerPhoneSmsLogin(loginAppUser, params);
            } else if (CredentialType.WECHAT_OPENID == credentialType) {// 微信登陆 后期如果有需要将会启用
                handlerWechatLogin(loginAppUser, params);
            }
        }
         */

        return loginAppUser;
    }


}
