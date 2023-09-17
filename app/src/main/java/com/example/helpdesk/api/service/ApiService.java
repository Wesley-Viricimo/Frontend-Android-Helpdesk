package com.example.helpdesk.api.service;

import com.example.helpdesk.model.Cliente;
import com.example.helpdesk.model.Credenciais;
import com.example.helpdesk.model.Tecnico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("login")
    Call<Void> validarUsuario(@Body Credenciais credenciais);

    @GET("clientes")
    Call<List<Cliente>> getClientes();

    @GET("clientes/{id}")
    Call<Cliente> getCliente(@Path("id") String id);

    @PUT("clientes/{id}")
    Call<Void> putCliente(@Path("id") String id, @Body Cliente cliente);

    @DELETE("clientes/{id}")
    Call<Void> deleteCliente(@Path("id") String id);

    @POST("clientes")
    Call<Void> cadastrarCliente(@Body Cliente cliente);

    @GET("tecnicos")
    Call<List<Tecnico>> getTecnicos();

    @GET("tecnicos/{id}")
    Call<Tecnico> getTecnico(@Path("id") String id);

    @POST("tecnicos")
    Call<Void> cadastrarTecnico(@Body Tecnico tecnico);

    @PUT("tecnicos/{id}")
    Call<Void> putTecnico(@Path("id") String id, @Body Tecnico tecnico);

    @DELETE("tecnicos/{id}")
    Call<Void> deleteTecnico(@Path("id") String id);
}
