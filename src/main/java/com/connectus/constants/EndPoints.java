package com.connectus.constants;

public class EndPoints {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";



    private static final String ROOT = DEV + VERSION;

    public static final String AUTH = ROOT + "/auth";


    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String VERIFYACCOUNT = "/verify-account";
    public static final String DELETE = "/delete";
    public static final String UPDATE = "/update";

    public static final String RESETPASSWORD = "/reset-password" ;
    public static final String FORGETPASSWORD = "/forget-password";

    // Project
    public static final String PROJECT = ROOT + "/project";
    public static final String SAVE = "/save";

}
