package com.example.eliavmenachi.myapplication.Models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.eliavmenachi.myapplication.Entities.City;
import com.example.eliavmenachi.myapplication.Entities.CityMallStoreDetails;
import com.example.eliavmenachi.myapplication.Entities.ListData;
import com.example.eliavmenachi.myapplication.Entities.Mall;
import com.example.eliavmenachi.myapplication.Entities.User;
import com.example.eliavmenachi.myapplication.Entities.Sale;
import com.example.eliavmenachi.myapplication.Entities.Store;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainModel {

    //region Data Members

    public static MainModel instance = new MainModel();
    MainModelFirebase mainModelFirebase;
    List<Store> lstAllStores = new ArrayList<Store>();
    List<Mall> lstAllMalls = new ArrayList<Mall>();
    List<City> lstAllCities = new ArrayList<City>();

    //endregion

    //region C'Tors

    private MainModel()
    {
        mainModelFirebase = new MainModelFirebase();
    }

    // endregion

    // region Interfaces

    public interface IsUserVisibleListener{
        void onDone(boolean p_bIsValid);
    }

    public interface GetPostsByStoreIdListener{
        void onGetPosts(List<Sale> p_postToReturn);
    }

    //endregion

    //region Methods

    //region posts

    public void addPost(Sale p_postToSave)
    {
        mainModelFirebase.addPost(p_postToSave);
    }

    //endregion

    //region users

    public void addUser(User userToAdd)
    {
        mainModelFirebase.addUser(userToAdd);
    }

    public void IsUserVisible(final String p_strUserName, final String p_strPassword ,  final IsUserVisibleListener listener ){
        boolean bIsValid = true;

        mainModelFirebase.IsUserVisible(p_strUserName, p_strPassword, new IsUserVisibleListener() {
            @Override
            public void onDone(boolean p_bIsValid) {
                // Its happen when we get response from firebase
                listener.onDone(p_bIsValid);
            }
        });

        //listener.onDone(bIsValid);
    }

    //endregion

    //region get posts

    public void GetPostsByStoreId(final String storeId, final GetPostsByStoreIdListener listener)
    {
        mainModelFirebase.getPostsByStoreId(storeId, new GetPostsByStoreIdListener() {
            @Override
            public void onGetPosts(List<Sale> p_postToReturn) {
                // TODO: need to return the posts to the fragments of posts
                //listener.onGetPosts(p_postToReturn);
            }
        });
    }

    //endregion

    //endregion

    //region live data

    public class SaleListData extends MutableLiveData<List<Sale>>
    {
        @Override
        protected void onActive() {
            super.onActive();

            // new thread tsks
            // 1. get the students list from the local DB
            SaleAsyncDao.getAll(new SaleAsyncDao.SaleAsynchDaoListener<List<Sale>>() {
                @Override
                public void onComplete(List<Sale> data) {
                    // 2. update the live data with the new student list
                    setValue(data);

                    // 3. get the student list from firebase
                    mainModelFirebase.getAllSales(new MainModelFirebase.GetAllSalesListener() {
                        @Override
                            public void onSuccess(List<Sale> salesList) {
                            // 4. update the live data with the new student list
                            setValue(salesList);
                            Log.d("TAG","got students from firebase " + salesList.size());

                            // 5. update the local DB
                            SaleAsyncDao.insertAll(salesList, new SaleAsyncDao.SaleAsynchDaoListener<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    // Done
                                }
                            });
                        }
                    });
                }
            });
            /*
            // TODO: 1. get the students list from the local DB
            // TODO: 2. update the live data with the new student list

            // 3. get the student list from firebase
            mainModelFirebase.getAllSales(new MainModelFirebase.GetAllSalesListener() {
                @Override
                public void onSuccess(List<Sale> studentslist) {
                    // 4. update the live data with the new student list
                    setValue(studentslist);
                    Log.d("TAG","got students from firebase " + studentslist.size());

                    // TODO: 5. update the local DB
                }
            });*/
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            mainModelFirebase.cancellGetAllSales();
        }

        public SaleListData()
        {
            super();
            setValue(new LinkedList<Sale>());
        }
    }

    SaleListData studentListData = new SaleListData();

    public LiveData<List<Sale>> getAllSales() { return studentListData;}

    //endregion
}
