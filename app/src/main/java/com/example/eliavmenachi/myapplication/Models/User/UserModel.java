package com.example.eliavmenachi.myapplication.Models.User;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.eliavmenachi.myapplication.Entities.User;
import com.example.eliavmenachi.myapplication.Models.MainAppLocalDb;

public class UserModel {

    //region DataMembers
    public static UserModel instance = new UserModel();
    UserModelFirebase userModelFirebase;
    private UserData user;
    private String username;
    private String password;
    //endregion

    //region Methods
    private UserModel() {
        userModelFirebase = new UserModelFirebase();
    }

//    public LiveData<User> getUser(int id) {
//        return user;
//    }

    public LiveData<User> getUser(String username, String password) {
        this.username = username;
        this.password = password;
        user = new UserData();
        return user;
    }

    public LiveData<User> getCurrentUser() {
        user = new UserData();
        return user;
    }

    class UserData extends MutableLiveData<User> {
        @Override
        protected void onActive() {
            super.onActive();

            if (username == null) {
                UserAsynchDao.getCurrentUser(new UserAsynchDao.UserAsynchDaoListener<User>() {
                    @Override
                    public void onComplete(User data) {
                        if (user != null) {
                            setValue(data);
                        }

                        userModelFirebase.getUserById(data.id, new UserModelFirebase.getUserByIdListener() {
                            @Override
                            public void onSuccess(final User user) {
                                setValue(user);
                                UserAsynchDao.removeAll(new UserAsynchDao.UserAsynchDaoListener<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        UserAsynchDao.insert(user, new UserAsynchDao.UserAsynchDaoListener<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean data) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });


            } else {
                UserAsynchDao.getData(username, new UserAsynchDao.UserAsynchDaoListener<User>() {
                    @Override
                    public void onComplete(User data) {
                        if (data != null) {
                            setValue(data);
                        }

                        userModelFirebase.getUserByUsernamePassword(username, password, new UserModelFirebase.getUserByUsernamePasswordListener() {
                            @Override
                            public void onSuccess(User user) {
                                setValue(user);

                                UserAsynchDao.insert(user, new UserAsynchDao.UserAsynchDaoListener<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {

                                    }
                                });
                            }
                        });
                    }
                });
            }
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            userModelFirebase.cancelGetUser();
            Log.d("TAG", "cancellGetAllStudents");
        }

        public UserData() {
            super();
            //setValue(AppLocalDb.db.studentDao().getAll());
            //setValue(new User());
        }

        public UserData(String username) {
            super();
            //setValue(AppLocalDb.db.studentDao().getAll());
            setValue(MainAppLocalDb.db.userDao().getUserByUsername(username));
        }
    }

//    public void IsUserExists(final String p_strUserName, final String p_strPassword, final IsUserExistsListener listener) {
//        userModelFirebase.IsUserExists(p_strUserName, p_strPassword, new IsUserExistsListener() {
//            @Override
//            public void onDone(boolean p_bIsExists) {
//                // This happens when we get response from firebase
//                listener.onDone(p_bIsExists);
//            }
//        });
//        //listener.onDone(bIsValid);
//    }

    public void addUser(User userToAdd) {
        userModelFirebase.addUser(userToAdd);
    }

    public void setUser(User user) {
        userModelFirebase.setUser(user);
    }

    //endregion
}
