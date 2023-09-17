package com.example.helpdesk.util;

public abstract class FuncoesUtil {
    public static String formataCPF(String CPF) {
        return(CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }
}
