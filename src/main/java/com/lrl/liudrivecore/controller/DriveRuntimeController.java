package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.service.UserAuthService;
import com.lrl.liudrivecore.service.tool.mune.TokenSpan;
import com.lrl.liudrivecore.service.tool.runtime.SessionManager;
import com.lrl.liudrivecore.service.tool.template.UserAuthTemplate;
import com.lrl.liudrivecore.service.tool.template.frontendInteractive.UserInfoAlterationTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/drive")
public class DriveRuntimeController {

    private static Logger logger = LoggerFactory.getLogger(DriveRuntimeController.class);
    private final SessionManager sessionManager;

    UserAuthService userAuthService;

    @Autowired
    public DriveRuntimeController(UserAuthService userAuthService, SessionManager sessionManager) {
        this.userAuthService = userAuthService;
        this.sessionManager = sessionManager;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public UserAuthTemplate userAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody UserAuthTemplate userAuthData) {


        logger.info("Authentication: controller: " + userAuthData.toString());
        User user = userAuthService.authenticate(userAuthData);

        if (user == null) {

            response.setStatus(400);
            logger.info("Authentication failed.");
            return null;
        } else {
            logger.info("Authentication passed.");
        }

        //Register in runtime modules
        String token = userAuthService.registerToken(user.getUserId());
        userAuthService.registerSession(user.getUserId(), request.getSession().getId());

        return UserAuthTemplate.safeCopyAndAddToken(user, token);

    }

    /**
     * User token refresher - increase a short time span
     * Require TOKEN
     *
     * @param request
     * @param response
     * @param userAuthData
     * @return
     */
    @RequestMapping(value = "/security/token", method = RequestMethod.PUT)
    public UserAuthTemplate updateUserTokenShort(HttpServletRequest request, HttpServletResponse response, @RequestBody UserAuthTemplate userAuthData) {


        logger.debug("Token: controller: " + userAuthData.toString());
        if (!userAuthService.quickAuth(userAuthData, request.getSession().getId())) {
            response.setStatus(400);
            logger.info("Authentication failed.");
            return null;
        }

        //Refresh the token with short time span
        userAuthService.updateToken(userAuthData, TokenSpan.SHORT_SPAN);

        return userAuthData;

    }

    /**
     * Register user info
     *
     * @param request
     * @param response
     * @param user
     * @return
     */

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserAuthTemplate register(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {

        logger.info("DriveUser Register: Unfolded input: " + user.toString());

        if (userAuthService.hasUser(user.getUsername())) {
            response.setStatus(400);
            return null;
        }

        user = userAuthService.register(user);
        if(user == null) response.setStatus(400);

        logger.info("User Registered: " + user.toString());


        return UserAuthTemplate.safeCopy(user);
    }


    /**
     * @param request
     * @param response
     * @param template
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public void alterUserInfo(HttpServletRequest request, HttpServletResponse response, UserInfoAlterationTemplate template) {

        logger.info("User Alter Info: Unfolded input: " + template.toString());
        if (!userAuthService.quickAuth(template.getUserAuthTemplate(), request.getSession().getId())) {
            response.setStatus(400);
            logger.info("Authentication failed.");
            return;
        }

        User newUser = userAuthService.updateUserInfo(template.getNewUserInfo());
        if (newUser == null) response.setStatus(400);
        else response.setStatus(204);


    }

    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    public void deleteUserInfo(HttpServletRequest request,HttpServletResponse response, UserInfoAlterationTemplate template){
        logger.info("Delete User Info: Unfolded input: " + template.toString());
        if (!userAuthService.quickAuth(template.getUserAuthTemplate(), request.getSession().getId())) {
            response.setStatus(400);
            logger.info("Authentication failed.");
            return;
        }

        userAuthService.deleteUser(template.getUsername());
    }
}
