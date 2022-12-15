package com.example.veggie;

import android.content.Context;

public class Login {
    public String getUser() {
        return user;
    }

    public Login(String user, String password, boolean estado) {
        this.user = user;
        this.password = password;
        this.estado = estado;
    }
    public boolean verificarUser(Context context){
        String url = "http://alugamadeira.pt/api.php?username="+getUser()+"&password="+getPassword()+"";
       Api resquest = new Api(url,context);
        if(resquest.postDataUsingVolley(url))
            return true;
        else
            return false;

    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    private String user,password;
    private boolean estado;
}
