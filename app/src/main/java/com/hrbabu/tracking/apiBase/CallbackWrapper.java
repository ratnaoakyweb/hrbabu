package com.hrbabu.tracking.apiBase;


import android.util.Log;

import com.hrbabu.tracking.interfaces.IApiResponse;
import com.hrbabu.tracking.request_response.base.BaseResponse;
import com.hrbabu.tracking.request_response.getResponse.GetResponse;
import com.hrbabu.tracking.utils.CommonUtilsApi;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public abstract class CallbackWrapper<T> extends DisposableObserver<T> {

    public  CallbackWrapper() {

    }

    protected abstract void onSuccess(T t);

    protected  void onSuccessMsg(T t,String msg){

    }

    protected abstract void onError(String t);

    protected abstract void onTimeout();

    protected abstract void onUnknownError();

    protected abstract void onLogout();

    @Override
    public void onNext(final T t) {

        if (t instanceof GetResponse && ((GetResponse) t).getRs()==1) {
            onSuccess(t);
        }

        /*else if(t instanceof BaseResponse){


            BaseResponse baseResponse = ((BaseResponse) (t));

            try {
                new CommonUtilsApi().readMessage(baseResponse.getRs(), new IApiResponse() {
                    @Override
                    public void ionSuccess(String msg) {
                        try {
                            onSuccess(t);
                            onSuccessMsg(t,msg);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ionError(String error) {
                        onError(error);
                    }

                    @Override
                    public void OnkeyNotFound() {
                        onSuccess(t);
                    }

                    @Override
                    public void onFileNotFound() {
                        onError("File not found");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                onSuccess(t);
            } catch (NumberFormatException ee) {
                ee.printStackTrace();
                onSuccess(t);
            }
        }*/
        else {
            onSuccess(t);
        }

    }

    @Override
    public void onError(Throwable e) {

        if (e instanceof HttpException) {

            if(((HttpException) e).code()==401){
                onLogout();
            }else {
                ResponseBody responseBody = ((HttpException) e).response().errorBody();
                onError(getErrorMessage(responseBody));
            }
        } else if (e instanceof SocketTimeoutException) {
            onTimeout();
        } else if (e instanceof IOException) {
            onTimeout();
        }else  if (e instanceof NullPointerException){
            onError("User is inactive");
        }
        else {
            onUnknownError();
        }
    }


    @Override
    public void onComplete() {
        Log.e("onComplete","onComplete");
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("ResponseMessage");
        } catch (Exception e) {
            return "We are working. Please try after some time.";
        }
    }

}