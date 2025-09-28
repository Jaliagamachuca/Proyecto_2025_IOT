package com.example.proyecto_2025.Activities_Superadmin;

import retrofit2.Call;

import retrofit2.http.GET;


public interface EmployeeService {
    @GET("/")
    Call<EmployeeDto> obtenerLista();
}
