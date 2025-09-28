package com.hrbabu.tracking.apiBase;

import android.content.Context;
import android.os.Build;

import com.hrbabu.tracking.BuildConfig;
import com.hrbabu.tracking.apiBase.apiList.ApiList;
import com.hrbabu.tracking.utils.PrefKeys;
import com.hrbabu.tracking.utils.PrefUtil;

import com.social.pe.apibase.HttpInterceptorRetry;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private Retrofit retrofit;

    private ApiList apiList;

    public ApiList create(final Context context) {

        if (retrofit == null) {

            long NETWORK_CALL_TIMEOUT = 60;


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                retrofit = new Retrofit.Builder().baseUrl(Appconstant.AUTH_API).client(new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                                return hv.verify("hrapi.anmoluphaar.in", session);
                            }
                        }).sslSocketFactory(getSSLSocketFactory()).addInterceptor(new Interceptor() {
                            @NotNull
                            @Override
                            public Response intercept(@NotNull Chain chain) throws IOException {
                                Request original = chain.request();
                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder();
                                //Log.e("===>",chain.request().url().toString());

                                if (PrefUtil.Init(context).getString(PrefKeys.token).isEmpty()) {


                                } else {
                                    requestBuilder.header("Authorization", "Bearer" + " " + PrefUtil.Init(context).getString(PrefKeys.token));
                                }

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })

                        .addInterceptor(new HttpLoggingInterceptor()

                                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE)).readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS).addInterceptor(new HttpInterceptorRetry(context)).writeTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS).build()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

            } else {
                retrofit = new Retrofit.Builder().baseUrl(Appconstant.AUTH_API).client(new OkHttpClient.Builder().hostnameVerifier(new AllowAllHostnameVerifier()).addInterceptor(new Interceptor() {
                            @NotNull
                            @Override
                            public Response intercept(@NotNull Chain chain) throws IOException {
                                Request original = chain.request();
                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder();
                                //Log.e("===>",chain.request().url().toString());

                                if (PrefUtil.Init(context).getString(PrefKeys.token).isEmpty()) {


                                } else {
                                    requestBuilder.header("Authorization", "Bearer" + " " + PrefUtil.Init(context).getString(PrefKeys.token));
                                }

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })

                        .addInterceptor(new HttpLoggingInterceptor()

                                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE)).readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS).addInterceptor(new HttpInterceptorRetry(context)).writeTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS).build()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

            }


        }

        if (apiList == null) {
            apiList = retrofit.create(ApiList.class);
        }

        return apiList;

    }

    private static SSLSocketFactory getSSLSocketFactory() {
        // API level 28 is Android 9
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

}
