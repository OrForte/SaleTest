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
import com.example.eliavmenachi.myapplication.Models.CityMallAndStore.CityMallAndStoreModel;
import com.example.eliavmenachi.myapplication.Models.CityMallAndStore.CityMallAndStoreModelFirebase;
import com.example.eliavmenachi.myapplication.Models.Sale.SaleModelFirebase;


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
    CityMallAndStoreModelFirebase cityMallAndStoreModelFirebase;
    SaleModelFirebase saleModelFirebase;

    //endregion

    //region C'Tors

    private MainModel()
    {
        mainModelFirebase = new MainModelFirebase();
        cityMallAndStoreModelFirebase = new CityMallAndStoreModelFirebase();
        saleModelFirebase = new SaleModelFirebase();
    }

    // endregion

    //region live data

    //region SaleListData

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

    //region CityMallAndStoreListData

    public class CityMallAndStoreListData extends MutableLiveData<ListData>
    {
        @Override
        protected void onActive() {
            super.onActive();

            // TODO: 1. get the students list from the local DB
            // TODO: 2. update the live data with the new student list

            // 3. get the student list from firebase
            cityMallAndStoreModelFirebase.GetListOfCitiesMallsAndStores(new CityMallAndStoreModel.GetListOfCitiesMallsAndStoresListener() {
                @Override
                public void onGetListOfCitiesMallsANdStoresResults(ListData data) {
                    // 4. update the live data with the new student list
                    setValue(data);
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }
        public CityMallAndStoreListData()
        {
            super();
            setValue(new ListData());
        }
    }

    CityMallAndStoreListData cityMallAndStoreListData = new CityMallAndStoreListData();
    public LiveData<ListData> getAllCityMalssAndStores() {
        return cityMallAndStoreListData;
    }


    //endregion

    //region SaleListDataByStore

    public class SaleListDataByStore extends MutableLiveData<List<Sale>>
    {
        String m_storeId = "";

        @Override
        protected void onActive() {
            super.onActive();

            // 1. get the students list from the local DB
            SaleAsyncDao.getSalesByStoreId(m_storeId, new SaleAsyncDao.SaleAsynchDaoListener<List<Sale>>() {
                @Override
                public void onComplete(List<Sale> data) {
                    // 2. update the live data with the new student list
                    setValue(data);
                    // 3. get the sale list from firebase
                    mainModelFirebase.GetAllSalesByStoreId(m_storeId, new MainModelFirebase.GetAllSalesByStoreListener() {
                        @Override
                        public void onSuccess(List<Sale> results) {
                            // 4. update the live data with the new student list
                            setValue(results);
                        }
                    });
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        public SaleListDataByStore()
        {
            super();
            setValue(new LinkedList());
        }

        public void InitStoreId(String p_strStoreId)
        {
            m_storeId = p_strStoreId;
        }
    }


    public SaleListDataByStore saleListDataByStore = new SaleListDataByStore();
    public LiveData<List<Sale>> getAllSalesByStoreId(String p_strStoreId)
    {
        return saleListDataByStore;
    }

    public void InitStoreId(String p_strStoreId)
    {
        if (saleListDataByStore == null)
        {
            saleListDataByStore = new SaleListDataByStore();
        }

        saleListDataByStore.InitStoreId(p_strStoreId);
    }

    // endregion

    //region SalesByUserId
    public class SaleListDataByUserId extends MutableLiveData<List<Sale>>
    {
        String m_userName = "";

        @Override
        protected void onActive() {
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        public SaleListDataByUserId()
        {
            super();
            setValue(new LinkedList());
        }

        public void InitUserName(String p_strUserName)
        {
            m_userName = p_strUserName;
        }
    }

    public SaleListDataByUserId saleListDataByUserId = new SaleListDataByUserId();
    public LiveData<List<Sale>> getSalesByUserName(String p_strUserName)
    {
        return saleListDataByStore;
    }
    public void InitUserName(String p_strUserName)
    {
        if (saleListDataByUserId == null)
        {
            saleListDataByUserId = new SaleListDataByUserId();
        }

        saleListDataByStore.InitStoreId(p_strUserName);
    }

    // region GetSaleBySaleId

    public class SaleBySaleIdData extends MutableLiveData<Sale>
    {
        String m_saleId = "";

        @Override
        protected void onActive() {
            super.onActive();

            //SaleAsyncDao.getSaleBySaleId(m_saleId, new SaleAsyncDao.SaleAsynchDaoListener2<List<Sale>>() {
           //     @Override
            //    public void onCompleteOneSale(Sale data) {
            //        setValue(data);

                    mainModelFirebase.GetSaleBySaleId(m_saleId, new MainModelFirebase.GetSaleBySaleId() {
                        @Override
                        public void onGetData(Sale data) {
                            setValue(data);
                        }
                    });
            //    }
            //});
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        public SaleBySaleIdData()
        {
            super();
            setValue(new Sale());
        }

        public void InitSaleId(String p_saleId)
        {
            m_saleId = p_saleId;
        }
    }

    public SaleBySaleIdData saleBySaleIdData = new SaleBySaleIdData();
    public LiveData<Sale> getSaleBySaleId(String p_strSaleId)
    {
        return saleBySaleIdData;
    }
    public void InitSaleId(String p_SaleId)
    {
        if (saleBySaleIdData == null)
        {
            saleBySaleIdData = new SaleBySaleIdData();
        }

        saleBySaleIdData.InitSaleId(p_SaleId);
    }

    //endregion
}
