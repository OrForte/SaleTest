package com.example.eliavmenachi.myapplication.Models.CityMallAndStore;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.eliavmenachi.myapplication.Entities.City;
import com.example.eliavmenachi.myapplication.Entities.CityMallStoreDetails;
import com.example.eliavmenachi.myapplication.Entities.ListData;
import com.example.eliavmenachi.myapplication.Entities.Mall;
import com.example.eliavmenachi.myapplication.Entities.Store;

import java.util.ArrayList;
import java.util.List;

public class CityMallAndStoreModel {

    //region Data Members

    public static CityMallAndStoreModel instance = new CityMallAndStoreModel();
    CityMallAndStoreModelFirebase cityMallAndStoreModelFirebase;
    List<Store> lstAllStores = new ArrayList<Store>();
    List<Mall> lstAllMalls = new ArrayList<Mall>();
    List<City> lstAllCities = new ArrayList<City>();

    //endregion

    //region C'Tors

    private CityMallAndStoreModel() {
        cityMallAndStoreModelFirebase = new CityMallAndStoreModelFirebase();
    }

    // endregion

    public interface GetListOfCitiesMallsAndStoresListener {
        void onGetListOfCitiesMallsANdStoresResults(ListData data);
    }

    public void GetListOfCitiesMallsAndStores(final GetListOfCitiesMallsAndStoresListener listener) {
        cityMallAndStoreModelFirebase.GetListOfCitiesMallsAndStores(new GetListOfCitiesMallsAndStoresListener() {
            @Override
            public void onGetListOfCitiesMallsANdStoresResults(ListData data) {
                listener.onGetListOfCitiesMallsANdStoresResults(data);
            }
        });
    }


    public interface GetDetailsByStoreIdListener {
        void onGetDetailsByStoreIdResults(CityMallStoreDetails data);
    }

    public void GetDetailsByStoreId(final int storeId, final GetDetailsByStoreIdListener listener) {
        cityMallAndStoreModelFirebase.GetDetailsByStoreId(storeId, new GetDetailsByStoreIdListener() {
            @Override
            public void onGetDetailsByStoreIdResults(CityMallStoreDetails data) {
                listener.onGetDetailsByStoreIdResults(data);
            }
        });
    }


    public interface GetMallsByCityIdListener {
        void onGetMallsByCityIdResults(List<Mall> p_mallList);
    }

    public void GetMallsByCityId(final String cityId, final GetMallsByCityIdListener listener) {
        cityMallAndStoreModelFirebase.GetMallsByCityId(cityId, new GetMallsByCityIdListener() {
            @Override
            public void onGetMallsByCityIdResults(List<Mall> p_mallList) {
                listener.onGetMallsByCityIdResults(p_mallList);
            }
        });
    }


    public interface GetStoreByMallIdListener {
        void onGetStoresByMallIdResults(List<Store> p_storeList);
    }

    public void GetStoresByMallId(final String mallId, final GetStoreByMallIdListener listener) {
        cityMallAndStoreModelFirebase.GetStoresByMallId(mallId, new GetStoreByMallIdListener() {
            @Override
            public void onGetStoresByMallIdResults(List<Store> p_storeList) {
                listener.onGetStoresByMallIdResults(p_storeList);
            }
        });
    }


    public interface GetCitiesListener {
        void onGetCitiesResults(List<City> p_citiesList);
    }

    public void GetCities(final GetCitiesListener listener) {
        cityMallAndStoreModelFirebase.GetCities(new GetCitiesListener() {
            @Override
            public void onGetCitiesResults(List<City> p_citiesList) {
                listener.onGetCitiesResults(p_citiesList);
            }
        });
    }


    public interface GetStoreByStoreIdListener {
        void onGetStoreByStoreIdResults(Store storeData);
    }

    public void GetStoreByStoreId(final int storeId, final GetStoreByStoreIdListener listener) {
        cityMallAndStoreModelFirebase.GetStoreByStoreId(storeId, new GetStoreByStoreIdListener() {
            @Override
            public void onGetStoreByStoreIdResults(Store storeData) {
                listener.onGetStoreByStoreIdResults(storeData);
            }
        });
    }

    //region CityMallAndStoreListData

    public class CityMallAndStoreListData extends MutableLiveData<ListData> {
        @Override
        protected void onActive() {
            super.onActive();

            CityMallAndStoreAsyncDao.getCitiesMallsAndStores(new CityMallAndStoreAsyncDao.CityMallAndStoreAsynchDaoListener<ListData>() {
                @Override
                public void onComplete(ListData data) {
                    setValue(data);
                    cityMallAndStoreModelFirebase.GetListOfCitiesMallsAndStores(new CityMallAndStoreModel.GetListOfCitiesMallsAndStoresListener() {
                        @Override
                        public void onGetListOfCitiesMallsANdStoresResults(ListData data) {
                            //if (!getValue().equals(data)) {
                            setValue(data);

                            CityMallAndStoreAsyncDao.insertAll(data, new CityMallAndStoreAsyncDao.CityMallAndStoreAsynchDaoListener<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                }
                            });
                            //}
                        }
                    });
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
        }

        public CityMallAndStoreListData() {
            super();
            setValue(new ListData());
        }
    }

    CityMallAndStoreListData cityMallAndStoreListData = new CityMallAndStoreListData();

    public LiveData<ListData> getAllCityMalssAndStores() {
        return cityMallAndStoreListData;
    }

    //endregion
}
