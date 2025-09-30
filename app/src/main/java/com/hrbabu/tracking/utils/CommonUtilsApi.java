package com.hrbabu.tracking.utils;

import com.hrbabu.tracking.BuildConfig;
import com.hrbabu.tracking.interfaces.IApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CommonUtilsApi {


    public void readMessage(int status, IApiResponse iApiResponse) throws IOException {

        if (iApiResponse == null) {
            throw new FileNotFoundException();
        }

        BufferedReader br = null;

//        File file = new File("data/data/com.win.free/responsecode/response.txt");
        File file = new File(Appconstant.filePath.replace("#", BuildConfig.APPLICATION_ID));
//        if (!file.exists()) {
//            file = new File("data/data/com.social.pe/responsecode/response.txt");
//
//            if (!file.exists()) {
//                iApiResponse.onFileNotFound();
//            }
//        }
        StringBuilder data = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                data.append(line);
                data.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                if (br != null) {
                    br.close();
                }
            }
            try {
                JSONObject jsonObject = new JSONObject(data.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("rc");
                for (int i = 0; i < jsonArray.length(); i++) {
                    int statusCode = jsonArray.getJSONObject(i).getInt("msgCode");
                    if (status == statusCode) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        if (statusCode == 209) {
                            iApiResponse.ionSuccess(json.getString("msgInEng"));
                            return;
                        } else {
                            if (json.getBoolean("typeOfMsg")) {
                                iApiResponse.ionSuccess(json.getString("msgInEng"));
                                return;

                            } else {
                                iApiResponse.ionError(json.getString("msgInEng"));
                                return;
                            }
                        }

                    }
                }

                iApiResponse.OnkeyNotFound();


            } catch (JSONException e) {
                iApiResponse.ionError("json exception");
            }

        }
    }
}
