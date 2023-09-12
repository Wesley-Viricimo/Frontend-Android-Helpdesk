package com.example.helpdesk.api;

import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Credenciais;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<Void> validarUsuario(@Body Credenciais credenciais);

    @GET("clientes")
    Call<List<Cliente>> getClientes();

    @POST("clientes")
    Call<Cliente> cadastrarCliente(@Body Cliente cliente);
}
