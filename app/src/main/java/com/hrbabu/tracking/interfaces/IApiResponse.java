package com.hrbabu.tracking.interfaces;

public interface IApiResponse {


    void ionSuccess(String msg);

    void ionError(String error);

    void OnkeyNotFound();

    void onFileNotFound();




}
