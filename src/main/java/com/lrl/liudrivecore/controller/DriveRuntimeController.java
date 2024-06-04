package com.lrl.liudrivecore.controller;

import com.lrl.liudrivecore.data.pojo.User;
import com.lrl.liudrivecore.service.UserAuthService;
import com.lrl.liudrivecore.service.util.template.UserAuthTemplate;
import com.lrl.liudrivecore.service.util.template.frontendInteractive.UserInfoAlterationTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v2")
public class DriveRuntimeController {

    private static Logger logger = LoggerFactory.getLogger(DriveRuntimeController.class);

    UserAuthService userAuthService;

    @Autowired
    public DriveRuntimeController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @RequestMapping(value = "/drive/auth", method = RequestMethod.POST)
    public UserAuthTemplate userAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody UserAuthTemplate userAuthData) {


        logger.info("Authentication: controller: " + userAuthData.toString());
        User user = userAuthService.authenticate(userAuthData);

        if (user == null) {


            response.setStatus(403);
            logger.info("Authentication failed.");
            return null;
        } else {

            String token = userAuthService.attachToken(user);

            response.setHeader("Authorization", "Bearer "+token);
            logger.info("Authentication passed.");
        }

        //Register in runtime modules
//        String token = userAuthService.registerToken(user.getUserId());
//        userAuthService.registerSession(user.getUserId(), request.getSession().getId());

        return UserAuthTemplate.safeCopyAndAddToken(user, "testToken");

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
    @RequestMapping(value = "/token", method = RequestMethod.PUT)
    public UserAuthTemplate updateUserTokenShort(HttpServletRequest request, HttpServletResponse response, @RequestBody UserAuthTemplate userAuthData) {


        logger.debug("Token: controller: " + userAuthData.toString());

        //Refresh the token with short time span
        userAuthService.updateToken(userAuthData.getUserId(), request.getHeader("Authorization").split(" ")[1]);

        return userAuthData;

    }

    /**
     * M4.1.4 v0.1.5
     * Register user info
     *
     * @param request
     * @param response
     * @param user
     * @return
     */

    @RequestMapping(value = "/drive/auth/user", method = RequestMethod.POST)
    public UserAuthTemplate register(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {

        logger.info("DriveUser Register: Unfolded input: " + user.toString());

        if (userAuthService.hasUser(user.getUserId())) {
            response.setStatus(400);
            return null;
        }

        user = userAuthService.register(user);
        if(user == null) response.setStatus(400);

        logger.info("User Registered: " + user.toString());


        return UserAuthTemplate.safeCopy(user);
    }


    /**
     * M4.1.5 Modify user info
     * @param request
     * @param response
     * @param template
     * @return
     */
    @RequestMapping(value = "/drive/auth/user/{userId}", method = RequestMethod.PUT)
    public void alterUserInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody UserInfoAlterationTemplate template, @PathVariable String userId) {

        logger.info("User Alter Info: Unfolded input: " + template.toString());
        if (!userAuthService.validToken(request.getHeader("Authorization"), userId)) {
            response.setStatus(400);
            logger.info("Authentication failed.");
            return;
        }

        User newUser = userAuthService.updateUserInfo(template.getNewUserInfo());
        if (newUser == null) response.setStatus(400);
        else response.setStatus(204);


    }

    /**
     * M4.1.6 Remove user info
     * @param request
     * @param response
     * @param template
     * @param userId
     */
    @RequestMapping(value = "/drive/auth/user/{userId}", method = RequestMethod.DELETE)
    public void deleteUserInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody UserAuthTemplate template, @PathVariable String userId){
        logger.info("Delete User Info: Unfolded input: " + template.toString());


        if (!userAuthService.validToken(template.getUserId(), request.getHeader("Authorization"))) {
            response.setStatus(400);
            logger.info("Token invalid.");
            return;
        }

        if(userAuthService.authenticate(template) == null){
            response.setStatus(400);
            logger.info("Authentication failed.");
            return;
        }

        userAuthService.deleteUser(template.getUsername());
        response.setStatus(204);
    }

    /**
     * M4.1.7 Get user secret
     * @param request
     * @param response
     * @param template
     * @param userId
     * @return
     */
    @RequestMapping(value = "/drive/auth/user/{userId}", method = RequestMethod.GET)
    public UserAuthTemplate getUserInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody UserAuthTemplate template, @PathVariable String userId){
        logger.info("GET User Info: Unfolded input: " + template.toString());


        if (!userAuthService.validToken(template.getUserId(), request.getHeader("Authorization"))) {
            response.setStatus(400);
            logger.info("Token invalid.");
            return null;
        }

        if(userAuthService.authenticate(template) == null){
            response.setStatus(400);
            logger.info("Authentication failed.");
            return null;
        }

        UserAuthTemplate result = userAuthService.getUser(template.getUserId());
        if(result == null){
            response.setStatus(404);
            return null;
        }

        response.setStatus(204);
        return result;
    }
}
