package com.x64technology.linex.services;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.x64technology.linex.utils.Constants;

public class AuthManager {
   private final CognitoUserPool userPool;
   Context context;

   public AuthManager(Context context1) {
       this.context = context1;
       userPool = new CognitoUserPool(context1, Constants.poolID, Constants.clientID, Constants.clientSecret, Constants.awsRegion);
   }

   public void signIn(String username, String password, AuthenticationHandler handler) {
       CognitoUser cognitoUser = userPool.getUser();
       cognitoUser.initiateUserAuthentication(
               new AuthenticationDetails(username, password, null), handler, true).run();
   }

   public void signUp(String name, String email, String picture, String username, String password, SignUpHandler handler) {
       CognitoUserAttributes userAttributes = new CognitoUserAttributes();
       userAttributes.addAttribute("name", name);
       userAttributes.addAttribute("email", email);
       userAttributes.addAttribute("picture", picture);

       userPool.signUpInBackground(username, password, userAttributes, null, handler);
   }

   public void confirmUser(String username, String code, GenericHandler genericHandler) {
       userPool.getUser(username).confirmSignUpInBackground(code, false, genericHandler);
   }

   public void userLoggedIn(AuthenticationHandler handler) {
       userPool.getCurrentUser().getSession(handler);
   }

   public CognitoUser getUser() {
       return userPool.getCurrentUser();
   }
}
