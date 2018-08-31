package net.authoriti.authoritiapp.api.model;

/**
 * Created by mac on 12/17/17.
 */

public class AuthLogIn {

    private boolean login;
    private boolean wipe;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isWipe() {
        return wipe;
    }

    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }
}
